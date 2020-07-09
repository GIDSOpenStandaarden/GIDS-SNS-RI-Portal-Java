/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class NotLoggedInException extends RuntimeException {
	public NotLoggedInException(String message) {
		super(message);
	}
}
