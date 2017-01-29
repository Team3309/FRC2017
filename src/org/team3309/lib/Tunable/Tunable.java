package org.team3309.lib.Tunable;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Tunable {
	public String Category() default "Uncategorized";
	public String DisplayName() default "";
	public boolean ReadOnly() default false;
}
