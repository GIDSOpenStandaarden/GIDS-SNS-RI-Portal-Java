package nl.gidsopenstandaarden.ri.portal.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.service.HtiLaunchService;
import org.gidsopenstandaarden.solid.client.OAuth2Token;
import org.gidsopenstandaarden.solid.client.SolidFhirClient;
import org.hl7.fhir.dstu3.model.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 *
 */
@RestController
@RequestMapping("fhir/Task")
public class SolidTaskController {

	private  final SolidFhirClient solidFhirClient;
	private  final FhirContext fhirContext;
	private final HtiLaunchService htiLaunchService;

	public SolidTaskController(SolidFhirClient solidFhirClient, FhirContext fhirContext, HtiLaunchService htiLaunchService) {
		this.solidFhirClient = solidFhirClient;
		this.fhirContext = fhirContext;
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
