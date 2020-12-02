/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 */
@SpringBootApplication
@EntityScan(value = {"nl.gidsopenstandaarden.ri.portal.entity"})
@ComponentScan(value = {"nl.gidsopenstandaarden.ri.portal", "org.gidsopenstandaarden.solid.client"})
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
