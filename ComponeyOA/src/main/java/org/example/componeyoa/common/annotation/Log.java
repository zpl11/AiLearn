package org.example.componeyoa.common.annotation;

import org.example.componeyoa.common.enums.BusinessType;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块名称 / 功能说明
     */
    String title() default "";

    /**
     * 业务操作类型 (BusinessType.OTHER, INSERT, UPDATE, DELETE 等)
     */
    int businessType() default BusinessType.OTHER;

    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的返回数据
     */
    boolean isSaveResponseData() default true;
}
