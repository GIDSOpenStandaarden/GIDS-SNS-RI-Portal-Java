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
