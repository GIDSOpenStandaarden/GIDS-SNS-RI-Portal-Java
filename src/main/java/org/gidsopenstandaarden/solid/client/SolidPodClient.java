package org.gidsopenstandaarden.solid.client;

import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.shared.AccessDeniedException;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

/**
 *
 */
@Service
public class SolidPodClient {
	public static final Resource TYPE_RESOURCE = ResourceFactory.createResource("http://www.w3.org/ns/ldp#Resource");
	public static final Resource TYPE_CONTAINER = ResourceFactory.createResource("http://www.w3.org/ns/ldp#Container");
	public static final Property PROPERTY_TYPE = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	public static final Resource RESOURCE_AUTHOROZATIOPN = ResourceFactory.createResource("http://www.w3.org/ns/auth/acl#Authorization");
	public static final Property PROPERTY_MODE = ResourceFactory.createProperty("http://www.w3.org/ns/auth/acl#mode");
	public static final Resource RESOURCE_READ = ResourceFactory.createResource("http://www.w3.org/ns/auth/acl#Read");
	public static final Resource RESOURCE_WRITE = ResourceFactory.createResource("http://www.w3.org/ns/auth/acl#Write");
	public static final Resource RESOURCE_CONTROL = ResourceFactory.createResource("http://www.w3.org/ns/auth/acl#Control");
	public static final Property PROPERTY_AGRENT = ResourceFactory.createProperty("http://www.w3.org/ns/auth/acl#agent");
	public static final Property PROPERTY_DEFAULT = ResourceFactory.createProperty("http://www.w3.org/ns/auth/acl#default");
	public static final Property PROPERTY_ACCESSTO = ResourceFactory.createProperty("http://www.w3.org/ns/auth/acl#accessTo");
	private final static Map<String, String> CONTENT_TYPE_MAP_RDF_ = new HashMap<>();
	protected final static Map<String, String> CONTENT_TYPE_MAP_RDF = Collections.unmodifiableMap(CONTENT_TYPE_MAP_RDF_);

	static {
		CONTENT_TYPE_MAP_RDF_.put("application/rdf+xml", "RDF/XML");
		CONTENT_TYPE_MAP_RDF_.put("text/turtle", "TURTLE");
	}

	protected final SolidAuthClient solidAuthClient;
	protected final HttpClientCreator httpClientCreator;

	public SolidPodClient(SolidAuthClient solidAuthClient, HttpClientCreator httpClientCreator) {
		this.solidAuthClient = solidAuthClient;
		this.httpClientCreator = httpClientCreator;
	}

	public void addReadAcl(OAuth2Token token, String webId) throws IOException {
		Resource webIdResource = ResourceFactory.createResource(webId);
		String url = getBaseUrl(token.getIdToken(), "/fhir/Task/.acl");
		String resourceUrl = getBaseUrl(token.getIdToken(), "/fhir/Task/");
		Model model = getRdfRequest(token, url, "GET");
		Resource readAccessSubject = findReadAccessSubject(model);
		boolean modified = false;
		if (readAccessSubject != null) {
			List<Statement> agents = readAccessSubject.listProperties(PROPERTY_AGRENT).toList();
			boolean found = false;
			for (Statement statement : agents) {
				RDFNode agent = statement.getObject();
				if (webIdResource.equals(agent)) {
					found = true;
				}
			}
			if (!found) {
				modified = true;
				readAccessSubject.addProperty(PROPERTY_AGRENT, webIdResource);
			}
		} else {

			// The read ACL
			{
				Resource aclResource = model.createResource(resourceUrl + ".acl#Read");
				aclResource.addProperty(PROPERTY_AGRENT, webIdResource);
				try {
					aclResource.addProperty(PROPERTY_AGRENT, ResourceFactory.createResource(getSubject(token.getIdToken())));
				} catch (ParseException e) {
					throw new IOException(e);
				}
				aclResource.addProperty(PROPERTY_DEFAULT, ResourceFactory.createResource(resourceUrl));
				aclResource.addProperty(PROPERTY_ACCESSTO, ResourceFactory.createResource(resourceUrl));
				aclResource.addProperty(PROPERTY_MODE, RESOURCE_READ);
				Statement aclStatement = ResourceFactory.createStatement(aclResource, PROPERTY_TYPE, RESOURCE_AUTHOROZATIOPN);
				model.add(aclStatement);
			}
			// The owner ACL
			{
				Resource aclResource = model.createResource(resourceUrl + ".acl#owner");
				try {
					aclResource.addProperty(PROPERTY_AGRENT, ResourceFactory.createResource(getSubject(token.getIdToken())));
				} catch (ParseException e) {
					throw new IOException(e);
				}
				aclResource.addProperty(PROPERTY_DEFAULT, ResourceFactory.createResource(resourceUrl));
				aclResource.addProperty(PROPERTY_ACCESSTO, ResourceFactory.createResource(resourceUrl));
				aclResource.addProperty(PROPERTY_MODE, RESOURCE_READ);
				aclResource.addProperty(PROPERTY_MODE, RESOURCE_WRITE);
				aclResource.addProperty(PROPERTY_MODE, RESOURCE_CONTROL);
				Statement aclStatement = ResourceFactory.createStatement(aclResource, PROPERTY_TYPE, RESOURCE_AUTHOROZATIOPN);
				model.add(aclStatement);
			}


			modified = true;
		}
		if (modified)
			putModel(token, model);
	}

	public boolean canReadPodOf(OAuth2Token token, String webId) throws IOException {
		String url = UrlUtils.getBaseUrl(webId, "/fhir/Task/");
		try {
			getRdfRequest(token, url, "GET");
			return true;
		} catch (AccessDeniedException e) {
			return false;
		}
	}

	public Resource findReadAccessSubject(Model model) {
		List<Resource> subjects = model.listSubjectsWithProperty(PROPERTY_TYPE, RESOURCE_AUTHOROZATIOPN).toList();
		for (Resource subject : subjects) {
			RDFNode mode = subject.getRequiredProperty(PROPERTY_MODE).getObject();
			if (mode.equals(RESOURCE_READ)) {
				return subject;
			}
		}
		return null;
	}

	public String getBaseUrl(String token, String path) throws IOException {
		try {
			String subject = getSubject(token);
			return UrlUtils.getBaseUrl(subject, path);
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	public List<String> getReadAcl(OAuth2Token token) throws IOException {
		List<String> rv = new ArrayList<>();
		String url = getBaseUrl(token.getIdToken(), "/fhir/Task/.acl");
		Model model = getRdfRequest(token, url, "GET");
		Resource readAccessSubject = findReadAccessSubject(model);
		if (readAccessSubject != null) {
			List<Statement> agents = readAccessSubject.listProperties(PROPERTY_AGRENT).toList();
			for (Statement agent : agents) {
				rv.add(agent.getObject().toString());
			}
		}
		return rv;
	}

	public String getWebId(OAuth2Token token) throws ParseException {
		return getSubject(token.getIdToken());
	}

	public boolean hasReadAcl(OAuth2Token token, String webId) throws IOException {
		Resource webIdResource = ResourceFactory.createResource(webId);
		String url = getBaseUrl(token.getIdToken(), "/fhir/Task/.acl");
		Model model = getRdfRequest(token, url, "GET");
		Resource readAccessSubject = findReadAccessSubject(model);
		if (readAccessSubject != null) {
			return readAccessSubject.hasProperty(PROPERTY_AGRENT, webIdResource);
		}
		return false;
	}

	public Map<String, Object> listFiles(OAuth2Token token, String path) throws IOException {
		Map<String, Object> rv = new HashMap<>();
		String url = getBaseUrl(token.getIdToken(), path);
		Model model = getRdfRequest(token, url, "GET");
		Property property = ResourceFactory.createProperty("http://www.w3.org/ns/ldp#contains");
		final StmtIterator statements = model.listStatements(null, property, (String) null);
		while (statements.hasNext()) {
			final Statement next = statements.next();
			final RDFNode object = next.getObject();
			final Resource item = object.asResource();
			final String name = getLocalName(item);
			if (model.contains(item, RDF.type, TYPE_CONTAINER)) {
				String childPath;
				if (StringUtils.endsWith(path, "/")) {
					childPath = String.format("%s%s/", path, name);
				} else {
					childPath = String.format("%s/%s/", path, name);
				}

				rv.put(name, listFiles(token, childPath));
			} else if (model.contains(item, RDF.type, TYPE_RESOURCE)) {
				rv.put(name, name);
			}

		}
		return rv;
	}

	public boolean putFile(OAuth2Token token, String path, String content, String type, String encoding) throws IOException {
		String url = getBaseUrl(token.getIdToken(), path);
		Map<String, String> headers = new HashMap<>();
		headers.put("link", "<http://www.w3.org/ns/ldp#Resource>; rel=\"type\"");
		headers.putAll(solidAuthClient.getAuthorizationHeaders(token, url, "PUT"));
		if (StringUtils.isNotEmpty(type)) {
			headers.put("Content-Type", type);
		}

		try (CloseableHttpClient client = httpClientCreator.createHttpClient()) {
			HttpPut httpPut = new HttpPut(url);
			setHeaders(headers, httpPut);
			httpPut.setEntity(new StringEntity(content, encoding));

			try (CloseableHttpResponse response = client.execute(httpPut)) {
				return response.getStatusLine().getStatusCode() == 201;
			}
		}

	}

	public void removeReadAcl(OAuth2Token token, String webId) throws IOException {
		Resource webIdResource = ResourceFactory.createResource(webId);
		String url = getBaseUrl(token.getIdToken(), "/fhir/Task/.acl");
		Model model = getRdfRequest(token, url, "GET");
		Resource readAccessSubject = findReadAccessSubject(model);
		boolean modified = false;
		if (readAccessSubject != null) {
			List<Statement> agents = readAccessSubject.listProperties(PROPERTY_AGRENT).toList();
			for (Statement statement : agents) {
				RDFNode agent = statement.getObject();
				if (webIdResource.equals(agent)) {
					model.remove(statement);
					modified = true;
				}
			}
		}
		if (modified)
			putModel(token, model);
	}

	private String getLocalName(Resource item) {
		String uri = item.getURI();
		if (StringUtils.endsWith(uri, "/")) {
			uri = StringUtils.removeEnd(uri, "/");
		}
		return StringUtils.substringAfterLast(uri, "/");
	}

	protected Model getRdfRequest(OAuth2Token token, String url, String method) throws IOException {
		return getRdfRequest(token, url, method, "application/rdf+xml");
	}

	protected Model getRdfRequest(OAuth2Token token, String url, String method, String contentType) throws IOException {
		Map<String, String> headers = new HashMap<>();

		headers.put("Accept", contentType);

		try (CloseableHttpClient client = httpClientCreator.createHttpClient()) {
			HttpGet httpGet = new HttpGet(url);
			if (token != null) {
				headers.putAll(solidAuthClient.getAuthorizationHeaders(token, url, method));
			}
			setHeaders(headers, httpGet);

			try (CloseableHttpResponse response = client.execute(httpGet)) {
				Model model = ModelFactory.createDefaultModel();
				final HttpEntity entity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					final String content = EntityUtils.toString(entity, StandardCharsets.UTF_8);
					return model.read(new StringReader(content), url, CONTENT_TYPE_MAP_RDF.get(contentType));
				} else if (statusCode == 403 || statusCode == 401) {
					throw new AccessDeniedException(String.format("Cannot access recource %s", url));
				} else if (statusCode == 404) {
					return model; // Return an empty model
				} else {
					throw new IOException(String.format("Unexpected return code %s", statusCode));
				}
			}
		}
	}

	private String getSubject(String token) throws ParseException {
		return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
	}

	private void putModel(OAuth2Token token, Model model) throws IOException {
		String contentType = "text/turtle";
		StringBuilder stringBuilder = new StringBuilder();
		model.write(new StringBuilderWriter(stringBuilder), CONTENT_TYPE_MAP_RDF.get(contentType));
		putFile(token, "/fhir/Task/.acl", stringBuilder.toString(), contentType, "UTF8");
	}

	private void setHeaders(Map<String, String> headers, HttpRequest request) {
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			request.setHeader(entry.getKey(), entry.getValue());
		}
	}
}
