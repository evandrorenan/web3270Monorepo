package br.com.evandrorenan.web3270.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IPAddressValidator.class)
public @interface ValidIPAddress {
    String message() default "Invalid IP address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
