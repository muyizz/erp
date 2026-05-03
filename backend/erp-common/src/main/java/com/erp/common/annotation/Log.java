package com.erp.common.annotation;

import com.erp.common.enums.BusinessType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    String title() default "";
    BusinessType businessType() default BusinessType.OTHER;
}
