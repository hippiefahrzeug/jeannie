package com.sb.jeannie.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Processor {

	/**
	 * the type is compared and if it matches, the processor will
	 * be activated.
	 * 
	 * the default is 'default' which activates the Default-processor
	 */
	String name() default "default";

	/**
	 * @return priority of this processor. the lower the value, 
	 * the lower the priority.
	 */
	int prio() default 0;

}
