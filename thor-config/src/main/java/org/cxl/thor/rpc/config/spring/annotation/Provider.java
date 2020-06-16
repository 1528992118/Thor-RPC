package org.cxl.thor.rpc.config.spring.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cxl
 * @Description: 服务提供者注解
 * @date 2020/6/11 14:57
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Provider {

    Class<?> value();

    String version() default "v1.0.0";

    long timeOut() default 5000L;

    String url() default "";

}
