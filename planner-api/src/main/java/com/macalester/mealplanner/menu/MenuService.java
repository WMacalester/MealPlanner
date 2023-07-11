package com.macalester.mealplanner.menu;

import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.RecipeRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final RecipeRepository recipeRepository;
    private final Random random = new Random();

    public Set<Recipe> getRandomUniqueRecipes(int numberOfRecipes){
        if (numberOfRecipes < 1){
            throw new IllegalArgumentException("Number of requested recipes must be greater than 0");
        }

        List<Recipe> allRecipes = recipeRepository.findAll();
        int recipesCount = allRecipes.size();

        if (numberOfRecipes > recipesCount){
            throw new IllegalArgumentException("Too many recipes requested");
        }

        Set<Recipe> output = new HashSet<>();

        while (output.size() < numberOfRecipes){
            output.add(allRecipes.get(random.nextInt(0, recipesCount)));
        }

        return output;
    }
}
