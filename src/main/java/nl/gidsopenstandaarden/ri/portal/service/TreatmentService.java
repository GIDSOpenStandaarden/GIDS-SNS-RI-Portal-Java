/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import nl.gidsopenstandaarden.ri.portal.controller.TreatmentController;
import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.entity.Treatment;
import nl.gidsopenstandaarden.ri.portal.repository.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 *
 */
@Service
public class TreatmentService {

	@Autowired
	public void setTreatmentRepository(TreatmentRepository treatmentRepository) {
		this.treatmentRepository = treatmentRepository;
	}

	TreatmentRepository treatmentRepository;

	private ResourceLoader resourceLoader;

	public Treatment getTreatment(String id) {
		return treatmentRepository.findById(id).orElse(null);
	}

	public List<Treatment> getTreatmentsForUser(PortalUser portalUser) {
		return treatmentRepository.findAll();
	}

	@PostConstruct
	public void init() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();

		TreatmentController.Treatments treatments = mapper.readValue(resourceLoader.getResource("classpath:treatments.yaml").getURL(), TreatmentController.Treatments.class);
		for (Treatment treatment : treatments.getTreatments()) {
			if (!treatmentRepository.findById(treatment.getId()).isPresent()) {
				treatmentRepository.save(treatment);
			}
		}
	}

	@Autowired
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
