/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 *
 */
public class UrlUtils {
	public static boolean isDefault(String scheme, int serverPort) {
		return serverPort == -1 || // Port is -1
				(StringUtils.equals("http", scheme) && serverPort == 80 // Scheme is http and port is 80
						|| StringUtils.equals("https", scheme) && serverPort == 443); // Scheme is https and port is 443
	}

	@NotNull
	public static String getServerUrl(String path, HttpServletRequest servletRequest) {
		int serverPort = servletRequest.getServerPort();
		String scheme = servletRequest.getScheme();
		String serverName = servletRequest.getServerName();
		return getServerUrl(path, serverPort, scheme, serverName);
	}

	@NotNull
	public static String getServerUrl(String path, URL url) {
		int serverPort = url.getPort();
		String scheme = url.getProtocol();
		String serverName = url.getHost();
		return getServerUrl(path, serverPort, scheme, serverName);
	}

	@NotNull
	private static String getServerUrl(String path, int serverPort, String scheme, String serverName) {
		if (isDefault(scheme, serverPort)) {
			return scheme + "://" + serverName + path;
		} else {
			return scheme + "://" + serverName + ":" + serverPort + path;
		}
	}

}
