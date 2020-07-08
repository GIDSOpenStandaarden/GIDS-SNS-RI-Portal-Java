package nl.gidsopenstandaarden.ri.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.gidsopenstandaarden.ri.portal.configuration.HtiConfiguration;
import nl.gidsopenstandaarden.ri.portal.entities.PortalUser;
import nl.gidsopenstandaarden.ri.portal.entities.Task;
import nl.gidsopenstandaarden.ri.portal.entities.Treatment;
import nl.gidsopenstandaarden.ri.portal.repository.TaskRepository;
import nl.gidsopenstandaarden.ri.portal.util.KeyUtils;
import nl.gidsopenstandaarden.ri.portal.valueobject.LaunchValueObject;
import nl.gidsopenstandaarden.ri.portal.valueobject.TaskValueObject;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
@Service
public class HtiLaunchService {

	private HtiConfiguration htiConfiguration;
	private ObjectMapper objectMapper;
	private TaskRepository taskRepository;

	@Autowired
	public void setHtiConfiguration(HtiConfiguration htiConfiguration) {
		this.htiConfiguration = htiConfiguration;
	}

	@Autowired
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Autowired
	public void setTaskRepository(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public LaunchValueObject startLaunch(PortalUser portalUser, Treatment treatment) throws JoseException {
		LaunchValueObject rv = new LaunchValueObject();
		rv.setUrl(treatment.getUrl());
		rv.setToken(generateToken(portalUser, treatment));
		return rv;
	}

	private Task buildTask(Treatment treatment, PortalUser portalUser) {
		String treatmentReference = "TaskDefinition/" + treatment.getId();
		String userReference = "Person/" + portalUser.getIdentifier();
		Optional<Task> optional = taskRepository.findTaskByDefinitionReferenceAndForUser(treatmentReference, userReference);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			Task task = new Task();
			task.setStatus("request");
			task.setIntent("plan");
			task.setIdentifier(UUID.randomUUID().toString());
			task.setDefinitionReference(treatmentReference);
			task.setForUser(userReference);
			taskRepository.save(task);
			return task;
		}
	}

	private String generateToken(PortalUser portalUser, Treatment treatment) throws JoseException {
		try {
			JsonWebSignature jws = new JsonWebSignature();
			JwtClaims claims = new JwtClaims();
			claims.setAudience(treatment.getAud());
			claims.setIssuer(htiConfiguration.getIssuer());
			claims.setIssuedAtToNow();
			claims.setGeneratedJwtId();
			claims.setExpirationTimeMinutesInTheFuture(5);
			claims.setClaim("task", toMap(toDto(buildTask(treatment, portalUser))));
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);
			jws.setPayload(claims.toJson());
			jws.setKey(KeyUtils.getRsaPrivateKey(htiConfiguration.getPrivateKey()));
			return jws.getCompactSerialization();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
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

	private <T> Map toMap(T task) {
		return objectMapper.convertValue(task, Map.class);
	}
}
