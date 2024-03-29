package com.macalester.mealplanner.recipes;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe findById(UUID id) {
        return recipeRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Recipe with id %s was not found", id)));
    }

    public Set<Recipe> findAllById(Set<UUID> ids) {
        Set<Recipe> output = new HashSet<>(ids.size());
        ids.forEach(id -> output.add(findById(id)));
        return output;
    }

    public Recipe addRecipe(Recipe recipe) {
        if (recipeRepository.existsByName(recipe.getName())) {
            throw new UniqueConstraintViolationException(
                    String.format("Recipe with name %s already exists", recipe.getName()));
        }
        return recipeRepository.save(recipe);
    }

    public void deleteById(UUID id) {
        recipeRepository.deleteById(id);
    }

    public Recipe editRecipeById(UUID id, Recipe updatedRecipe) {
        Recipe storedRecipe = findById(id);

        if (!storedRecipe.getName().equals(updatedRecipe.getName())
                && recipeRepository.existsByName(updatedRecipe.getName())) {
            throw new UniqueConstraintViolationException(
                    String.format("Recipe with name %s already exists", updatedRecipe.getName()));
        }

        updatedRecipe.setId(storedRecipe.getId());
        return recipeRepository.save(updatedRecipe);
    }

    public Set<Recipe> getRecipesNotInCollection(Collection<Recipe> recipeCollection) {
        if (recipeCollection == null || recipeCollection.isEmpty()) {
            return new HashSet<>(recipeRepository.findAll());
        }

        return recipeRepository.findAllNotInCollection(recipeCollection);
    }
}
