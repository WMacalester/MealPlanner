package com.macalester.mealplanner.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UserTest {
    private static final String user1Name = "User 1";
    private static final String user2Name = "User 2";
    private static final String user1Passworda = "User 1 password";
    private static final String user1Passwordb = "User 1 password alternate";
    private static final User user1a = new User.UserBuilder().username(user1Name).password(user1Passworda).role(UserRole.USER).build();
    private static final User user1b = new User.UserBuilder().username(user1Name).password(user1Passwordb).role(UserRole.ADMIN).id(UUID.randomUUID()).build();
    private static final User user2 = new User.UserBuilder().username(user2Name).password(user1Passworda).role(UserRole.USER).build();

    @Nested
    @DisplayName("Equals and HashCode")
    class EqualsAndHashCodeTest {
        @Test
        @DisplayName("Objects are equal")
        void equalsAndHashCode_givenEqualObjects_objectsEqual() {
            assertEquals(user1a,user1b);
            assertEquals(user1a.hashCode(),user1b.hashCode());
        }

        @Test
        @DisplayName("Objects are not equal")
        void equalsAndHashCode_givenUnequalObjects_objectsNotEqual() {
            assertNotEquals(user1a, user2);
            assertNotEquals(user1a.hashCode(), user2.hashCode());
        }
    }
}
