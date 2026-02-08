package sjpp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Define} class.
 */
class DefineTest {

	@Test
	void doesApplyOn_shouldReturnTrue_whenStringContainsWhenAndId() {
		Define define = new Define("FOO");

		assertTrue(define.doesApplyOn("// when FOO"));
		assertTrue(define.doesApplyOn("something when FOO else"));
		assertTrue(define.doesApplyOn("   when   FOO   "));
	}

	@Test
	void doesApplyOn_shouldReturnFalse_whenStringDoesNotContainWhen() {
		Define define = new Define("FOO");

		assertFalse(define.doesApplyOn("FOO"));
		assertFalse(define.doesApplyOn("// FOO"));
		assertFalse(define.doesApplyOn("something FOO else"));
	}

	@Test
	void doesApplyOn_shouldReturnFalse_whenStringDoesNotContainId() {
		Define define = new Define("FOO");

		assertFalse(define.doesApplyOn("// when BAR"));
		assertFalse(define.doesApplyOn("when"));
		assertFalse(define.doesApplyOn("something when else"));
	}

	@Test
	void doesApplyOn_shouldReturnFalse_whenIdAppearsBeforeWhen() {
		Define define = new Define("FOO");

		assertFalse(define.doesApplyOn("FOO when"));
		assertFalse(define.doesApplyOn("FOO something when"));
	}

}
