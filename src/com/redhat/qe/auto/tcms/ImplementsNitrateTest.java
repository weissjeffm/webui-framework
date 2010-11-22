package com.redhat.qe.auto.tcms;

import java.lang.annotation.*;

/**
 * Annotation to add metdata pairing automated tests to manual Nitrate test cases
 * @author ssalevan
 * @author jsefler
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface ImplementsNitrateTest {
	int caseId();
	int version() default 0; // will be interpreted as latest
	int fromPlan() default 0;
	String baseUrl() default "http://tcms.engineering.redhat.com";
}
