/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.entity.Treatment;
import nl.gidsopenstandaarden.ri.portal.exception.NotLoggedInException;
import nl.gidsopenstandaarden.ri.portal.service.*;
import nl.gidsopenstandaarden.ri.portal.util.UrlUtils;
import nl.gidsopenstandaarden.ri.portal.valueobject.LaunchValueObject;
import org.apache.commons.lang3.StringUtils;
import org.gidsopenstandaarden.solid.client.OAuth2Token;
import org.gidsopenstandaarden.solid.client.SolidFhirClient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Task;
import org.jose4j.lang.JoseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 */
@RestController
@RequestMapping("/api/task")
public class TaskController {

	private final TreatmentService treatmentService;
	private final PortalUserService portalUserService;
	private final HtiLaunchService htiLaunchService;
	private final SolidFhirClient solidFhirClient;
	private final TaskService taskService;

	public TaskController(TreatmentService treatmentService, PortalUserService portalUserService, HtiLaunchService htiLaunchService, SolidFhirClient solidFhirClient, TaskService taskService) {
		this.treatmentService = treatmentService;
		this.portalUserService = portalUserService;
		this.htiLaunchService = htiLaunchService;
		this.solidFhirClient = solidFhirClient;
		this.taskService = taskService;
	}

	@RequestMapping(value = "launch/{taskId}", produces = MediaType.TEXT_HTML_VALUE)
	public String start(@PathVariable("taskId") String taskId, HttpSession session, HttpServletRequest request) throws JoseException, IOException {
		PortalUser user = (PortalUser) session.getAttribute("user");
		OAuth2Token token = (OAuth2Token) session.getAttribute("token");
		if (user == null || token == null) {
			throw new NotLoggedInException("No active session found");
		}
		Task fhirTask = solidFhirClient.getTask(token, htiLaunchService.getUserReference(user), taskId);
		nl.gidsopenstandaarden.ri.portal.entity.Task task = convertTask(user, fhirTask);

		String definitionReference = ((Reference) fhirTask.getDefinition()).getReference();
		String definitionId = StringUtils.removeStart(definitionReference, "ActivityDefinition/");
		Treatment treatment = treatmentService.getTreatment(definitionId);

		LaunchValueObject launchValueObject = htiLaunchService.startLaunch(treatment, task, UrlUtils.getServerUrl("", request));

		return "<html>\n" +
				"<head>\n" +
				"</head>\n" +
				"<body onload=\"document.forms[0].submit();\">\n" +
				"<form action=\"" + launchValueObject.getUrl() + "\" method=\"post\">\n" +
				"<input type=\"hidden\" name=\"token\" value=\"" + launchValueObject.getToken() + "\"/>\n" +
				"</form>\n" +
				"</body>\n" +
				"</html>";
	}

	@RequestMapping(value = "launch/{taskId}/patient/{patientId}", produces = MediaType.TEXT_HTML_VALUE)
	public String start(@PathVariable("taskId") String taskId, @PathVariable("patientId") Long patientId, HttpSession session, HttpServletRequest request) throws JoseException, IOException {
		PortalUser user = (PortalUser) session.getAttribute("user");
		OAuth2Token token = (OAuth2Token) session.getAttribute("token");
		if (user == null || token == null) {
			throw new NotLoggedInException("No active session found");
		}


		PortalUser patient = portalUserService.getPortalUser(patientId);

		Task fhirTask = solidFhirClient.getOtherPersonsTask(token, patient.getWebId(), htiLaunchService.getUserReference(patient), taskId);
		nl.gidsopenstandaarden.ri.portal.entity.Task task = convertTask(user, fhirTask);

		String definitionReference = ((Reference) fhirTask.getDefinition()).getReference();
		String definitionId = StringUtils.removeStart(definitionReference, "ActivityDefinition/");
		Treatment treatment = treatmentService.getTreatment(definitionId);

		LaunchValueObject launchValueObject = htiLaunchService.startLaunch(treatment, task, UrlUtils.getServerUrl("", request));

		return "<html>\n" +
				"<head>\n" +
				"</head>\n" +
				"<body onload=\"document.forms[0].submit();\">\n" +
				"<form action=\"" + launchValueObject.getUrl() + "\" method=\"post\">\n" +
				"<input type=\"hidden\" name=\"token\" value=\"" + launchValueObject.getToken() + "\"/>\n" +
				"</form>\n" +
				"</body>\n" +
				"</html>";
	}

	private String convertStatus(Task fhirTask) {
		return fhirTask.getStatus().toCode();
	}

	private nl.gidsopenstandaarden.ri.portal.entity.Task convertTask(PortalUser user, Task fhirTask) {
		String definitionReference = ((Reference) fhirTask.getDefinition()).getReference();
		nl.gidsopenstandaarden.ri.portal.entity.Task task = taskService.getTask(fhirTask.getId());
		if (task == null) {
			task = new nl.gidsopenstandaarden.ri.portal.entity.Task();
		}
		task.setIdentifier(fhirTask.getId());
		task.setStatus(convertStatus(fhirTask));
		task.setForUser(htiLaunchService.getUserReference(user));
		task.setDefinitionReference(definitionReference);
		taskService.save(task);
		return task;
	}

}
