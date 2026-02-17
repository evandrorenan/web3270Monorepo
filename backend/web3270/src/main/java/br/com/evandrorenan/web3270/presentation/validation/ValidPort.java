package br.com.evandrorenan.web3270.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PortValidator.class)
public @interface ValidPort {
    String message() default "Port must be between 1 and 65535";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
