/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.gidsopenstandaarden.ri.portal.configuration.HtiConfiguration;
import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.entity.Task;
import nl.gidsopenstandaarden.ri.portal.entity.Treatment;
import nl.gidsopenstandaarden.ri.portal.util.KeyUtils;
import nl.gidsopenstandaarden.ri.portal.valueobject.LaunchValueObject;
import nl.gidsopenstandaarden.ri.portal.valueobject.TaskValueObject;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
@Service
public class HtiLaunchService {

	protected final HtiConfiguration htiConfiguration;
	protected final ObjectMapper objectMapper;
	protected final TaskService taskService;

	public HtiLaunchService(HtiConfiguration htiConfiguration, ObjectMapper objectMapper, TaskService taskService) {
		this.htiConfiguration = htiConfiguration;
		this.objectMapper = objectMapper;
		this.taskService = taskService;
	}

	public String getUserReference(PortalUser portalUser) {
		return "Person/" + portalUser.getIdentifier();
	}


	public LaunchValueObject startLaunch(Treatment treatment, Task task, String host) throws JoseException {
		LaunchValueObject rv = new LaunchValueObject();
		rv.setUrl(treatment.getUrl());
		rv.setToken(generateToken(treatment, task, host));
		return rv;
	}

	public LaunchValueObject startLaunch(PortalUser portalUser, Treatment treatment, String host) throws JoseException {
		LaunchValueObject rv = new LaunchValueObject();
		rv.setUrl(treatment.getUrl());
		Task task = getOrCreateTask(treatment, portalUser);
		rv.setToken(generateToken(treatment, task, host));
		return rv;
	}

	private String generateToken(Treatment treatment, Task task, String issuer) throws JoseException {
		try {
			JsonWebSignature jws = new JsonWebSignature();
			JwtClaims claims = new JwtClaims();
			claims.setAudience(treatment.getAud());
			claims.setIssuer(getIssuer(issuer));
			claims.setIssuedAtToNow();
			claims.setGeneratedJwtId();
			claims.setExpirationTimeMinutesInTheFuture(5);
			claims.setClaim("task", toMap(toDto(task)));
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);
			jws.setKeyIdHeaderValue(KeyUtils.getFingerPrint(KeyUtils.getRsaPublicKey(htiConfiguration.getPublicKey())));
			jws.setPayload(claims.toJson());
			jws.setKey(KeyUtils.getRsaPrivateKey(htiConfiguration.getPrivateKey()));
			return jws.getCompactSerialization();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	private String getIssuer(String issuer) {
		return StringUtils.isNotEmpty(htiConfiguration.getIssuerOverride()) ? htiConfiguration.getIssuerOverride() : issuer;
	}

	private Task getOrCreateTask(Treatment treatment, PortalUser portalUser) {
		String treatmentReference = getTreatmentReference(treatment);
		String userReference = getUserReference(portalUser);
		Task task = taskService.getByDefinitionReferenceAndForUser(getTreatmentReference(treatment), getUserReference(portalUser));
		if (task != null) {
			return task;
		} else {
			task = new Task();
			task.setStatus(org.hl7.fhir.dstu3.model.Task.TaskStatus.REQUESTED.toCode());
			task.setIntent(org.hl7.fhir.dstu3.model.Task.TaskIntent.PLAN.toCode());
			task.setIdentifier(UUID.randomUUID().toString());
			task.setDefinitionReference(treatmentReference);
			task.setForUser(userReference);
			taskService.save(task);
			return task;
		}
	}

	private String getTreatmentReference(Treatment treatment) {
		return "ActivityDefinition/" + treatment.getId();
	}

	private TaskValueObject toDto(Task task) {
		TaskValueObject rv = new TaskValueObject();
		rv.setId(task.getIdentifier());
		rv.setIntent(task.getIntent());
		rv.setResourceType(task.getResourceType());
		rv.setStatus(task.getStatus());
		rv.getDefinitionReference().setReference(task.getDefinitionReference());
		rv.getForUser().setReference(task.getForUser());
		return rv;
	}

	@SuppressWarnings("unchecked")
	private <T> Map<String, Object> toMap(T task) {
		return objectMapper.convertValue(task, Map.class);
	}
}
