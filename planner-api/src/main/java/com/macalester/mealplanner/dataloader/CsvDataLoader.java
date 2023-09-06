package com.macalester.mealplanner.dataloader;

import static com.macalester.mealplanner.Utils.formatName;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.ingredients.IngredientRepository;
import com.macalester.mealplanner.recipes.DietType;
import com.macalester.mealplanner.recipes.Recipe;
import jakarta.validation.Validator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix = "data.loader", name = "filetype", havingValue = "csv")
@Component
@RequiredArgsConstructor
@Slf4j
public class CsvDataLoader implements DataLoader {
    private final IngredientRepository ingredientRepository;
    private final Validator validator;

    @Override
    public Set<Ingredient> loadIngredients(String fileName) {
        try {
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (!fileExtension.equals("csv")) {
                log.error(String.format("Incorrect filetype found. Expected \"csv\", found \"%s\".", fileExtension));
                return Set.of();
            }

            InputStream inputStream = new ClassPathResource(fileName).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            Set<Ingredient> ingredients = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                Ingredient newIngredient = new Ingredient();
                newIngredient.setName(formatName(line));

                if (!validator.validate(newIngredient).isEmpty()) {
                    log.error(String.format("Invalid ingredient found: %s", line));
                    continue;
                }

                ingredients.add(newIngredient);
            }

            return ingredients;
        } catch (IOException e) {
            log.error("Error loading data for ingredients", e);
        }

        return Set.of();
    }

    @Override
    public Set<Recipe> loadRecipes(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!fileExtension.equals("csv")) {
            log.error(String.format("Incorrect filetype found. Expected \"csv\", found \"%s\".", fileExtension));
            return Set.of();
        }

        try {
            InputStream inputStream = new ClassPathResource(fileName).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            Set<Recipe> recipes = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                Recipe recipe = computeRecipeFromString(line);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }

            return recipes;
        } catch (IOException e) {
            log.error("Error loading data for ingredients", e);
        }

        return Set.of();
    }

    private Recipe computeRecipeFromString(String input) {
        String[] nameIngredients = input.split(",");
        if (nameIngredients[0].isBlank() || nameIngredients.length < 2) {
            return null;
        }

        DietType dietType;

        try {
            dietType = DietType.valueOf(nameIngredients[1].trim());
        } catch (IllegalArgumentException e) {
            return null;
        }

        Set<Ingredient> ingredients = nameIngredients.length > 2 ? findIngredientsFromString(nameIngredients) : Set.of();

        Recipe newRecipe = new Recipe(null, formatName(nameIngredients[0]), dietType, ingredients);

        if (!validator.validate(newRecipe).isEmpty()) {
            log.error(String.format("Invalid ingredient found: %s", input));
            return null;
        }
        return newRecipe;
    }

    private Set<Ingredient> findIngredientsFromString(String[] input) {
        Set<Ingredient> ingredients = new HashSet<>(input.length - 1);
        for (int i = 2; i < input.length; i++) {
            String formattedName = formatName(input[i]);
            if (formattedName.isBlank()) {
                continue;
            }
            Optional<Ingredient> ingredient = ingredientRepository.findByName(formattedName);
            ingredient.ifPresentOrElse(ingredients::add,
                    () -> log.error(String.format("Ingredient with name %s was not found", formattedName)));
        }
        return ingredients;
    }
}
