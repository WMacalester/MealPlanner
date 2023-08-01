package com.macalester.mealplanner.menu;

import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.RecipeRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final RecipeRepository recipeRepository;
    private final Random random = new Random();

    public Set<Recipe> getRandomUniqueRecipes(int numberOfRecipes, Collection<Recipe> recipesToSelectFrom) {
        if (recipesToSelectFrom.isEmpty()){
            throw new IllegalArgumentException("Number of recipes to select from must be not be empty");
        } else if (numberOfRecipes < 1) {
            throw new IllegalArgumentException("Number of requested recipes must be greater than 0");
        } else if (numberOfRecipes > recipesToSelectFrom.size()){
            throw new IllegalArgumentException("Number of requested recipes must be less than or equal to the number of available, non-selected recipes");
        }

        Recipe[] recipes = recipesToSelectFrom.toArray(Recipe[]::new);
        int recipesCount = recipes.length;

        Set<Recipe> output = new HashSet<>(numberOfRecipes);

        while (output.size() < numberOfRecipes) {
            output.add(recipes[random.nextInt(0, recipesCount)]);
        }

        return output;
    }
}
