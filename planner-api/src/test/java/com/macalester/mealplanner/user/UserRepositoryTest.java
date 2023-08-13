package com.macalester.mealplanner.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.macalester.mealplanner.BasePostgresContainer;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest extends BasePostgresContainer {

    @Autowired
    private UserRepository userRepository;

    private static final String user1Name = "User 1";
    private static final String user1Password = "User 1 password";
    private static final User user1 = new User.UserBuilder().username(user1Name).password(user1Password).role(UserRole.USER).build();
    private static final String userNameNotExist = "user does not exist";

    @BeforeAll
    static void init(@Autowired UserRepository userRepository) {
        userRepository.save(user1);
    }

    @AfterAll
    static void teardown(@Autowired UserRepository userRepository) {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Find by username")
    class FindByUsernameTest {

        @Test
        @DisplayName("Returns optional of user if user in db")
        void findByUsername_userInDb_returnsOptionalUser() {
            assertEquals(Optional.of(user1), userRepository.findByUsername(user1Name));
        }

        @Test
        @DisplayName("Returns optional of empty if user not in db")
        void findByUsername_userNotInDb_returnsOptionalEmpty() {
            assertEquals(Optional.empty(), userRepository.findByUsername(userNameNotExist));
        }
    }


    @Nested
    @DisplayName("Exists by Username")
    class ExistsByUsernameTest {
        @Test
        @DisplayName("User exists by username returns true")
        void existsByUsername_userExistsWithUsername_returnsTrue(){
            assertTrue(userRepository.existsByUsername(user1Name));
        }

        @Test
        @DisplayName("User does not exist by username returns false")
        void existsByUsername_userNotExistsWithUsername_returnsFalse(){
            assertFalse(userRepository.existsByUsername(userNameNotExist));
        }
    }
}
