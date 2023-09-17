package com.wh.gmall.common.cache;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited()
@Documented
public @interface MyGmallCache {

    String prefix() default "cache:";
}
