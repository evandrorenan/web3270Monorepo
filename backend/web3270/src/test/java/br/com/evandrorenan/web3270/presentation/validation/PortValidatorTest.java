package br.com.evandrorenan.web3270.presentation.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PortValidatorTest {

    private PortValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new PortValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "80", "443", "3270", "65535"})
    void shouldAcceptValidPorts(String port) {
        assertThat(validator.isValid(port, context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "65536", "abc", ""})
    void shouldRejectInvalidPorts(String port) {
        assertThat(validator.isValid(port, context)).isFalse();
    }

    @Test
    void shouldAcceptNull() {
        assertThat(validator.isValid(null, context)).isTrue();
    }
}
