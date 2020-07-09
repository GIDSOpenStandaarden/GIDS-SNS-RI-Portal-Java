/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 *
 */
public class UrlUtils {
	public static boolean isDefault(String scheme, int serverPort) {
		return StringUtils.equals("http", scheme) && serverPort == 80
				|| StringUtils.equals("https", scheme) && serverPort == 443;
	}

	@NotNull
	public static String getServerUrl(String path, HttpServletRequest servletRequest) {
		int serverPort = servletRequest.getServerPort();
		String scheme = servletRequest.getScheme();
		if (isDefault(scheme, serverPort)) {
			return scheme + "://" + servletRequest.getServerName() + path;
		} else {
			return scheme + "://" + servletRequest.getServerName() + ":" + serverPort + path;
		}
	}
}
