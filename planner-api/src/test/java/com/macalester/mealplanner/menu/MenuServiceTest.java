package com.macalester.mealplanner.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.macalester.mealplanner.recipes.Recipe;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    private static final UUID uuid1 = UUID.randomUUID();
    private static final UUID uuid2 = UUID.randomUUID();
    private static final UUID uuid3 = UUID.randomUUID();
    private static final UUID uuid4 = UUID.randomUUID();
    private static final String name1 = "recipe a";
    private static final String name2 = "recipe b";
    private static final String name3 = "recipe c";
    private static final String name4 = "recipe d";

    private static final Recipe recipe1 = new Recipe(uuid1, name1, Set.of());
    private static final Recipe recipe2 = new Recipe(uuid2, name2, Set.of());
    private static final Recipe recipe3 = new Recipe(uuid3, name3, Set.of());
    private static final Recipe recipe4 = new Recipe(uuid4, name4, Set.of());

    @Nested
    @DisplayName("Get random recipes")
    class GetRandomRecipesTest {


            @Test
            @DisplayName("Valid request")
            void getRandomRecipes_numberRecipeRequestedLessThanNumberAvailable_returnsListOfUniqueRecipes(){
                assertEquals(3, menuService.getRandomUniqueRecipes(3, Set.of(recipe1,recipe2,recipe3,recipe4)).stream().distinct().count());
            }

            @Test
            @DisplayName("Requested more recipes than available")
            void getRandomRecipes_numberRecipeRequestedMoreThanNumberAvailable_throwsIllegalArgumentException(){
                Set<Recipe> input = Set.of(recipe1);
                assertThrows(IllegalArgumentException.class, () -> menuService.getRandomUniqueRecipes(20, input));
            }

            @Test
            @DisplayName("Requested < 1 recipe")
            void getRandomRecipes_requestedLessThan1Recipe_throwsIllegalArgumentException(){
                Set<Recipe> input = Set.of(recipe1);
                assertThrows(IllegalArgumentException.class, () -> menuService.getRandomUniqueRecipes(-1, input));
            }

            @Test
            @DisplayName("No recipes given to select from")
            void getRandomRecipes_noRecipesToSelectFromGiven_throwsIllegalArgumentException(){
                Set<Recipe> input = Set.of();
                assertThrows(IllegalArgumentException.class, () -> menuService.getRandomUniqueRecipes(1, input));
        }
    }
}
