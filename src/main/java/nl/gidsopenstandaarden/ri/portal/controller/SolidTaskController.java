package nl.gidsopenstandaarden.ri.portal.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.exception.NotLoggedInException;
import nl.gidsopenstandaarden.ri.portal.service.HtiLaunchService;
import nl.gidsopenstandaarden.ri.portal.service.PortalUserService;
import org.gidsopenstandaarden.solid.client.OAuth2Token;
import org.gidsopenstandaarden.solid.client.SolidFhirClient;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Task;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping("fhir/Task")
public class SolidTaskController {

	private final SolidFhirClient solidFhirClient;
	private final FhirContext fhirContext;
	private final PortalUserService portalUserService;
	private final HtiLaunchService htiLaunchService;

	public SolidTaskController(SolidFhirClient solidFhirClient, FhirContext fhirContext, PortalUserService portalUserService, HtiLaunchService htiLaunchService) {
		this.solidFhirClient = solidFhirClient;
		this.fhirContext = fhirContext;
		this.portalUserService = portalUserService;
		this.htiLaunchService = htiLaunchService;
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getTasks(HttpSession session) throws IOException {
		OAuth2Token token = (OAuth2Token) session.getAttribute("token");
		if (token == null) {
			throw new RuntimeException("No solid session found");
		}
		PortalUser user = (PortalUser) session.getAttribute("user");
		List<Task> tasks = solidFhirClient.listTasks(token, htiLaunchService.getUserReference(user));

		return toBundle(tasks);
	}

	@RequestMapping(value = "patient/{patientId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getTasksForPatient(@PathVariable("patientId") Long patientId, HttpSession session) throws IOException {
		PortalUser user = (PortalUser) session.getAttribute("user");
		OAuth2Token token = (OAuth2Token) session.getAttribute("token");
		if (user == null || token == null) {
			throw new NotLoggedInException("No active session found");
		}

		PortalUser patient = portalUserService.getPortalUser(patientId);

		String patientWebId = patient.getWebId();
		List<Task> tasks = new ArrayList<>();
		if (patientWebId != null) {
			tasks = solidFhirClient.listOtherPersonsTasks(token, patientWebId, htiLaunchService.getUserReference(patient));
		}
		return toBundle(tasks);

	}

	@RequestMapping(value = "authorize", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String authorizeUser(@RequestParam() String webId, HttpSession session) throws IOException {
		OAuth2Token token = (OAuth2Token) session.getAttribute("token");
		if (token == null) {
			throw new RuntimeException("No solid session found");
		}
		solidFhirClient.addReadAcl(token, webId);
		return "{}";

	}

	@RequestMapping(value = "deauthorize", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String deauthorizeUser(@RequestParam() String webId, HttpSession session) throws IOException {
		OAuth2Token token = (OAuth2Token) session.getAttribute("token");
		if (token == null) {
			throw new RuntimeException("No solid session found");
		}
		solidFhirClient.removeReadAcl(token, webId);
		return "{}";

	}

	private String toBundle(List<Task> tasks) {
		Bundle bundle = new Bundle();
		for (Task task : tasks) {
			bundle.addEntry().setResource(task);
			bundle.setType(Bundle.BundleType.COLLECTION);

		}
		IParser jsonParser = fhirContext.newJsonParser();
		jsonParser.setPrettyPrint(true);
		return jsonParser.encodeResourceToString(bundle);
	}

}
