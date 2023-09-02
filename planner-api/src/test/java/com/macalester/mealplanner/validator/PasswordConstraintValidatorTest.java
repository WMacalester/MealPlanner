package com.macalester.mealplanner.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PasswordConstraintValidatorTest {
    @Mock
    private PasswordConstraint passwordConstraint;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    private final PasswordConstraintValidator passwordConstraintValidator = new PasswordConstraintValidator();

    @Test
    @DisplayName("Password only contains legal characters - returns true")
    void passwordContainsOnlyAlphaCharactersAndWhitespace_returnsTrue() {
        assertTrue(passwordConstraintValidator.isValid("a valid message$#?", constraintValidatorContext));
        assertTrue(passwordConstraintValidator.isValid("  a   valid     message  ", constraintValidatorContext));
    }

    @Nested
    @DisplayName("Invalid Messages")
    class InvalidMessagesTest {

        @Test
        @DisplayName("Password is null - returns false")
        void passwordIsNull_returnsFalse() {
            doReturn(constraintViolationBuilder).when(constraintValidatorContext).buildConstraintViolationWithTemplate(any(String.class));
            passwordConstraintValidator.initialize(passwordConstraint);

            assertFalse(passwordConstraintValidator.isValid(null, constraintValidatorContext));
        }

        @Test
        @DisplayName("Password is empty - returns false")
        void passwordIsEmpty_returnsFalse() {
            assertFalse(passwordConstraintValidator.isValid("", constraintValidatorContext));
        }

        @Test
        @DisplayName("Password only contains whitespace - returns false")
        void passwordIsBlank_returnsFalse() {
            assertFalse(passwordConstraintValidator.isValid("            ", constraintValidatorContext));
        }

        @Test
        @DisplayName("Password contains illegal characters - returns false")
        void passwordContainsNonAlphaCharacters_returnsFalse() {
            assertFalse(passwordConstraintValidator.isValid("aaa` 1a 2`", constraintValidatorContext));
        }
    }
}
