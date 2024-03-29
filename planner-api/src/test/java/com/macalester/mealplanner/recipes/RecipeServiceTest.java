package com.macalester.mealplanner.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.ingredients.Ingredient;
import java.util.List;
import java.util.Optional;
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
class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;
    @Mock
    private RecipeRepository recipeRepository;

    private static final UUID uuid1 = UUID.randomUUID();
    private static final UUID uuid2 = UUID.randomUUID();
    private static final String name1 = "recipe 1";
    private static final String name2 = "recipe 2";

    private final Recipe recipe1 = new Recipe(uuid1, name1, DietType.MEAT, Set.of());
    private final Recipe recipe2 = new Recipe(uuid2, name2, DietType.MEAT, Set.of());
    private final Ingredient ingredient1 =
            new Ingredient(UUID.randomUUID(), "ingredient 1", Set.of());

    private final List<Recipe> recipes = List.of(recipe1, recipe2);

    @Test
    @DisplayName("Gets all recipes")
    void getAllRecipes() {
        doReturn(recipes).when(recipeRepository).findAll();
        assertEquals(recipes, recipeService.getAllRecipes());
    }

    @Nested
    @DisplayName("Get recipe by id")
    class GetRecipeByIdTest {
        @Test
        @DisplayName("Return recipe with given id that is in database")
        void getRecipeById_recipeWithIdInDatabase_returnsRecipe() {
            doReturn(Optional.of(recipe1)).when(recipeRepository).findById(uuid1);

            assertEquals(recipe1, recipeService.findById(uuid1));
        }

        @Test
        @DisplayName("Throw NotFoundException if no recipe with given id is in database")
        void getRecipeById_recipeWithIdNotInDatabase_throwsNotFoundException() {
            doReturn(Optional.empty()).when(recipeRepository).findById(uuid1);

            assertThrows(NotFoundException.class, () -> recipeService.findById(uuid1));
        }
    }

    @Test
    @DisplayName("Delete recipe by id calls correct repository method")
    void deleteRecipeById_whenMethodCalled_callsRepositoryMethod() {
        recipeService.deleteById(uuid1);
        verify(recipeRepository).deleteById(uuid1);
        verifyNoMoreInteractions(recipeRepository);
    }

    @Nested
    @DisplayName("Find all by id")
    class FindAllByIdTest{
        @Test
        @DisplayName("All valid ids returns corresponding recipes")
        void findAllById_allIdsInDb_returnsCorrespondingRecipes(){
            doReturn(Optional.of(recipe1)).when(recipeRepository).findById(recipe1.getId());
            doReturn(Optional.of(recipe2)).when(recipeRepository).findById(recipe2.getId());

            assertEquals(Set.of(recipe1,recipe2), recipeService.findAllById(Set.of(recipe1.getId(), recipe2.getId())));
        }

        @Test
        @DisplayName("An id not in db throws NotFoundException")
        void findAllById_idNotInDb_throwsNotFoundException(){
            Set<UUID> input = Set.of(UUID.randomUUID());
            assertThrows(NotFoundException.class, () -> recipeService.findAllById(input));
        }
    }

    @Nested
    @DisplayName("Add recipe")
    class AddRecipeTest {
        @Test
        @DisplayName("Saves valid recipe that has unique name")
        void addRecipe_givenRecipeAndUniqueName_savesIngredient() {
            doReturn(false).when(recipeRepository).existsByName(recipe1.getName());
            doReturn(recipe1).when(recipeRepository).save(recipe1);

            assertEquals(recipe1, recipeService.addRecipe(recipe1));
        }

        @Test
        @DisplayName(
                "Valid recipe that does not have a unique name throws UniqueConstrainViolationException")
        void saveIngredient_givenRecipeAndNameAlreadyExists_throwsUniqueConstraintViolationException() {
            doReturn(true).when(recipeRepository).existsByName(recipe1.getName());

            assertThrows(
                    UniqueConstraintViolationException.class, () -> recipeService.addRecipe(recipe1));
            verifyNoMoreInteractions(recipeRepository);
        }
    }

    @Nested
    @DisplayName("Edit recipe by Id")
    class EditRecipeByIdTest {
        private final Recipe recipe_NullId1 = new Recipe(null, name1, DietType.MEAT, Set.of());
        private final Recipe recipe_NullId2 = new Recipe(null, name2, DietType.MEAT, Set.of());

        @Test
        @DisplayName("Recipe with given id not in database throws NotFoundException")
        void editRecipeById_recipeWithIdNotInDatabase_throwsNotFoundException() {
            doReturn(Optional.empty()).when(recipeRepository).findById(uuid1);
            assertThrows(NotFoundException.class, () -> recipeService.editRecipeById(uuid1, recipe1));
        }

        @Test
        @DisplayName("All fields updated and returns recipe with correct id")
        void editRecipeById_validRecipeObjectAndIdInDb_savesAndReturnsUpdatedRecipe() {
            Recipe recipe1_allUpdated = new Recipe(uuid1, name2, DietType.VEGAN, Set.of(ingredient1));
            doReturn(Optional.of(recipe1)).when(recipeRepository).findById(uuid1);
            doReturn(recipe1_allUpdated).when(recipeRepository).save(recipe1_allUpdated);

            assertEquals(recipe1_allUpdated, recipeService.editRecipeById(uuid1, recipe_NullId2));
        }

        @Nested
        @DisplayName("Name changes")
        class EditRecipeById_NameChanges {
            @Test
            @DisplayName("Name is not changed, saves and returns updated recipe")
            void editRecipeById_nameNotChanged_savesAndReturnsUpdatedRecipe() {
                Recipe recipe_allUpdated = new Recipe(uuid1, name1, DietType.MEAT, Set.of(ingredient1));
                doReturn(Optional.of(recipe1)).when(recipeRepository).findById(uuid1);
                doReturn(recipe_allUpdated).when(recipeRepository).save(recipe_allUpdated);

                assertEquals(recipe_allUpdated, recipeService.editRecipeById(uuid1, recipe_NullId1));
            }

            @Test
            @DisplayName("Name is changed and is not unique, throws UniqueConstraintViolationException")
            void editRecipeById_nameIsChangedAndNotUnique_throwsUniqueConstraintViolationException() {
                doReturn(Optional.of(recipe1)).when(recipeRepository).findById(uuid1);
                doReturn(true).when(recipeRepository).existsByName(name2);

                assertThrows(
                        UniqueConstraintViolationException.class,
                        () -> recipeService.editRecipeById(uuid1, recipe_NullId2));
            }

            @Test
            @DisplayName("Name is changed and is unique, returns updated recipe")
            void editRecipeById_nameIsChangedAndUnique_savesAndReturnsUpdatedRecipe() {
                Recipe recipe_nameUpdated = new Recipe(uuid1, name2, DietType.MEAT, Set.of(ingredient1));
                doReturn(Optional.of(recipe1)).when(recipeRepository).findById(uuid1);
                doReturn(false).when(recipeRepository).existsByName(name2);
                doReturn(recipe_nameUpdated).when(recipeRepository).save(recipe_nameUpdated);

                assertEquals(recipe_nameUpdated, recipeService.editRecipeById(uuid1, recipe_NullId2));
            }
        }
    }

    @Nested
    @DisplayName("Get Recipes Not In Collection")
    class GetRecipesNotInCollectionTest {
        @Test
        @DisplayName("Collection is not empty")
        void getRecipesNotInCollection_collectionIsNotEmpty_returnsSubsetOfRecipes() {
            doReturn(Set.of(recipe1)).when(recipeRepository).findAllNotInCollection(Set.of(recipe2));

            assertEquals(Set.of(recipe1), recipeService.getRecipesNotInCollection(Set.of(recipe2)));
        }

        @Test
        @DisplayName("Collection is null or empty")
        void getRecipesNotInCollection_collectionIsNullOrEmpty_returnsAllRecipes() {
            doReturn(List.of(recipe1, recipe2)).when(recipeRepository).findAll();

            assertEquals(Set.of(recipe1, recipe2), recipeService.getRecipesNotInCollection(null));
            assertEquals(Set.of(recipe1, recipe2), recipeService.getRecipesNotInCollection(Set.of()));
        }
    }
}
