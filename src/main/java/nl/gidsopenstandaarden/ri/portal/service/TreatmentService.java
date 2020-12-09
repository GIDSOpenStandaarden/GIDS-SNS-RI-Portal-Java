/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import nl.gidsopenstandaarden.ri.portal.configuration.TreatmentsConfiguration;
import nl.gidsopenstandaarden.ri.portal.controller.TreatmentController;
import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.entity.Treatment;
import nl.gidsopenstandaarden.ri.portal.repository.TreatmentRepository;
import nl.gidsopenstandaarden.ri.portal.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 *
 */
@Service
public class TreatmentService {


	private final TreatmentRepository treatmentRepository;

	private final ResourceLoader resourceLoader;

	private final TreatmentsConfiguration treatmentsConfiguration;

	public TreatmentService(TreatmentRepository treatmentRepository, ResourceLoader resourceLoader, TreatmentsConfiguration treatmentsConfiguration) {
		this.treatmentRepository = treatmentRepository;
		this.resourceLoader = resourceLoader;
		this.treatmentsConfiguration = treatmentsConfiguration;
	}

	public Treatment getTreatment(String id) {
		return treatmentRepository.findById(id).orElse(null);
	}

	public List<Treatment> getTreatmentsForUser(PortalUser portalUser) {
		return treatmentRepository.findAll();
	}

	@PostConstruct
	public void init() throws IOException {
		readCsv();
	}

	private void readCsv() throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceLoader.getResource("classpath:treatments.csv").getInputStream()))) {
			String line;
			int index = 0;
			while ((line = reader.readLine()) != null) {
				if (index > 0) {
					String[] items = StringUtils.split(line, ",");
					if (items.length == 4) {
						String indentifier = items[0];
						String name = items[1];
						String description = items[2];
						Treatment treatment = new Treatment();
						treatment.setId(indentifier);
						treatment.setName(name);
						treatment.setDescription(description);
						updateTreatment(treatment);
					}
				}
				index++;
			}
		}


	}

	private void readYaml() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();

		TreatmentController.Treatments treatments = mapper.readValue(resourceLoader.getResource("classpath:treatments.yaml").getURL(), TreatmentController.Treatments.class);
		for (Treatment treatment : treatments.getTreatments()) {
			updateTreatment(treatment);
		}
	}

	private void updateTreatment(Treatment treatment) throws MalformedURLException {
		Treatment original = treatmentRepository.findById(treatment.getId()).orElse(treatment);
		if (StringUtils.isEmpty(treatment.getAud())) {
			original.setAud(UrlUtils.getServerUrl("", new URL(treatmentsConfiguration.getLaunchUrl())));
		} else {
			original.setAud(treatment.getAud());
		}
		if (StringUtils.isEmpty(treatment.getUrl())) {
			original.setUrl(treatmentsConfiguration.getLaunchUrl());
		} else {
			original.setUrl(treatment.getUrl());
		}
		original.setDescription(treatment.getDescription());
		original.setName(treatment.getName());
		treatmentRepository.save(original);
	}

}
