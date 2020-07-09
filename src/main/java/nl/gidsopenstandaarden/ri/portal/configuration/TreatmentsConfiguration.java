/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@ConfigurationProperties(prefix = "module.treatment")
public class TreatmentsConfiguration {
	private String launchUrl;

	public String getLaunchUrl() {
		return launchUrl;
	}

	public void setLaunchUrl(String launchUrl) {
		this.launchUrl = launchUrl;
	}
}
