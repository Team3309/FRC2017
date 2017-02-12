package org.team3309.lib.tunable;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Dashboard {
	public String category() default "Uncategorized";

	public String displayName() default "";

	public boolean tunable() default false;
}
