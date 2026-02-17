package br.com.evandrorenan.web3270.presentation.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class IPAddressValidatorTest {

    private IPAddressValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new IPAddressValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"127.0.0.1", "192.168.0.1", "10.0.0.1", "255.255.255.255", "0.0.0.0"})
    void shouldAcceptValidIps(String ip) {
        assertThat(validator.isValid(ip, context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "127.0.0", "127.0.0.1.1", "256.0.0.1", ""})
    void shouldRejectInvalidIps(String ip) {
        assertThat(validator.isValid(ip, context)).isFalse();
    }

    @Test
    void shouldAcceptNull() {
        assertThat(validator.isValid(null, context)).isTrue();
    }
}
