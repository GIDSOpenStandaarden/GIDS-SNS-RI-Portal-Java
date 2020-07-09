package nl.gidsopenstandaarden.ri.portal.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 */

public class TreatmentTest {

	@Test
	public void compareTo() {

	}

	private Treatment createTreatment(String name) {
		Treatment a = new Treatment();
		a.setName(name);
		return a;
	}

	@Test
	public void testEquals() {
		Treatment a = createTreatment("a");
		Treatment b = createTreatment("b");

		Assertions.assertEquals(a, createTreatment("a"));
		Assertions.assertEquals(b, createTreatment("b"));
		Assertions.assertNotEquals(a, b);
	}

	@Test
	public void testHashCode() {
	}
}
