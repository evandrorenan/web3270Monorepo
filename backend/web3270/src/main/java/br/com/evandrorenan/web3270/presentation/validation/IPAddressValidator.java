package br.com.evandrorenan.web3270.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IPAddressValidator implements ConstraintValidator<ValidIPAddress, String> {
    private static final String OCTET = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private static final Pattern IP_PATTERN = Pattern.compile("^" + OCTET + "\\." + OCTET + "\\." + OCTET + "\\." + OCTET + "$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return IP_PATTERN.matcher(value).matches();
    }
}
