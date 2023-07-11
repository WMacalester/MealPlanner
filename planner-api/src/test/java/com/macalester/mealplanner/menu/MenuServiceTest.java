package com.macalester.mealplanner.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.RecipeRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private RecipeRepository recipeRepository;
    @InjectMocks
    private MenuService menuService;

    private static final UUID uuid1 = UUID.randomUUID();
    private static final UUID uuid2 = UUID.randomUUID();
    private static final UUID uuid3 = UUID.randomUUID();
    private static final UUID uuid4 = UUID.randomUUID();
    private static final String name1 = "recipe 1";
    private static final String name2 = "recipe 2";
    private static final String name3 = "recipe 3";
    private static final String name4 = "recipe 4";

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
            doReturn(List.of(recipe1,recipe2, recipe3,recipe4)).when(recipeRepository).findAll();

            assertEquals(3, menuService.getRandomUniqueRecipes(3).stream().distinct().count());
        }

        @Test
        @DisplayName("Requested more recipes than available")
        void getRandomRecipes_numberRecipeRequestedMoreThanNumberAvailable_throwsIllegalArgumentException(){
            doReturn(List.of(recipe1,recipe2, recipe3,recipe4)).when(recipeRepository).findAll();

            assertThrows(IllegalArgumentException.class, () -> menuService.getRandomUniqueRecipes(20));
        }

        @Test
        @DisplayName("Requested < 1 recipe")
        void getRandomRecipes_requestedLessThan1Recipe_throwsIllegalArgumentException(){

            assertThrows(IllegalArgumentException.class, () -> menuService.getRandomUniqueRecipes(-1));
        }
    }
}
