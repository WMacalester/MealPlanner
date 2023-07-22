package com.macalester.mealplanner.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NameConstraintValidatorTest {
    @Mock private NameConstraint nameConstraint;
    @Mock private ConstraintValidatorContext constraintValidatorContext;
    @Mock private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    private final NameConstraintValidator nameConstraintValidator = new NameConstraintValidator();

    @Test
    @DisplayName("Name only contains alpha characters and whitespace - returns true")
    void nameContainsOnlyAlphaCharactersAndWhitespace_returnsTrue(){
        assertTrue(nameConstraintValidator.isValid("a valid message", constraintValidatorContext));
        assertTrue(nameConstraintValidator.isValid("  a   valid     message  ", constraintValidatorContext));
    }

    @Nested
    @DisplayName("Invalid Messages")
    class InvalidMessagesTest {
        @BeforeEach
        void init(){
            doReturn(constraintViolationBuilder).when(constraintValidatorContext).buildConstraintViolationWithTemplate(any(String.class));
            nameConstraintValidator.initialize(nameConstraint);
        }

        @Test
        @DisplayName("Name is null - returns false")
        void nameIsNull_returnsFalse(){
            assertFalse(nameConstraintValidator.isValid(null, constraintValidatorContext));
        }

        @Test
        @DisplayName("Name is empty - returns false")
        void nameIsEmpty_returnsFalse(){
            assertFalse(nameConstraintValidator.isValid("", constraintValidatorContext));
        }

        @Test
        @DisplayName("Name only contains whitespace - returns false")
        void nameIsBlank_returnsFalse(){
            assertFalse(nameConstraintValidator.isValid("            ", constraintValidatorContext));
        }

        @Test
        @DisplayName("Name contains non-alpha characters - returns false")
        void nameContainsNonAlphaCharacters_returnsFalse(){
            assertFalse(nameConstraintValidator.isValid("aaa 1a 2`", constraintValidatorContext));
        }
    }

}