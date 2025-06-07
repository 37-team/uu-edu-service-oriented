package com.bigitcompany.cloudaireadmodel.common.domain.model.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark auto-generated code (e.g. equals & hash-code) for exclusion from jacoco
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Generated {
}