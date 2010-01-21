package com.redhat.qe.auto.tcms;

/**
 * Annotation to add metdata pairing automated tests to
 * manual TCMS test cases
 * @author ssalevan
 */
public @interface ImplementsTCMS {
	String id();
	String url() default "";
	String comment() default "";
	String tcms() default "nitrate";
}
