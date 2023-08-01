package com.macalester.mealplanner.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.macalester.mealplanner.BasePostgresContainer;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RecipeRepositoryTest extends BasePostgresContainer {

    @Autowired private RecipeRepository recipeRepository;

    private static Recipe recipeA = new Recipe(null, "recipe a", Set.of());
    private static Recipe recipeB = new Recipe(null, "recipe b", Set.of());
    private static Recipe recipeC = new Recipe(null, "recipe c", Set.of());
    private static Recipe recipeD = new Recipe(null, "recipe d", Set.of());
    private static Recipe recipeE = new Recipe(null, "recipe e", Set.of());

    @BeforeAll
    static void init(@Autowired RecipeRepository recipeRepository){
        recipeA = recipeRepository.save(recipeA);
        recipeB = recipeRepository.save(recipeB);
        recipeC = recipeRepository.save(recipeC);
        recipeD = recipeRepository.save(recipeD);
        recipeE = recipeRepository.save(recipeE);
    }

    @AfterAll
    static void teardown(@Autowired RecipeRepository recipeRepository){
        recipeRepository.deleteAll();
    }

    @Test
    void findAllNotInCollection_givenCollection_returnsSubsetOfAllRecipesWithoutRecipesInCollection(){
        Set<Recipe> ids = Set.of(recipeA, recipeC);

        assertEquals(Set.of(recipeB, recipeD, recipeE), recipeRepository.findAllNotInCollection(ids));
    }
}
