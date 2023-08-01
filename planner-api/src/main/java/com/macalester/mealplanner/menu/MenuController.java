package com.macalester.mealplanner.menu;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.RecipeService;
import com.macalester.mealplanner.recipes.dto.RecipeDto;
import com.macalester.mealplanner.recipes.dto.RecipeDtoMapper;
import jakarta.validation.Valid;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final RecipeService recipeService;
    private final RecipeDtoMapper recipeDtoMapper;
    private final MessageSource messageSource;

    @PostMapping
    public List<RecipeDto> createMenuWithRandomUniqueRecipes(@RequestParam int number){
        try {
            if (number < 1){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("menu.error.notEnoughRecipesRequested", null, Locale.ENGLISH));
            }
            Set<Recipe> recipes = menuService.getRandomUniqueRecipes(number, recipeService.getAllRecipes());

            return recipes.stream().map(recipeDtoMapper).sorted(Comparator.comparing(RecipeDto::name)).toList();
        } catch (IllegalArgumentException | NotFoundException e ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(consumes = "application/json")
    public List<RecipeDto> createMenuWithRandomUniqueRecipes(@RequestParam int number, @Valid @RequestBody MenuCreateDto menuCreateDto){
        try {
            int numRandomRecipes = number-menuCreateDto.recipeIds().size();
            if (number < 1 || numRandomRecipes < 0){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("menu.error.notEnoughRecipesRequested", null, Locale.ENGLISH));
            }

            Set<Recipe> selectedRecipes = recipeService.findAllById(menuCreateDto.recipeIds());
            Set<Recipe> recipesNotSelected = recipeService.getRecipesNotInCollection(selectedRecipes);
            Set<Recipe> recipes = numRandomRecipes > 0 ? menuService.getRandomUniqueRecipes(numRandomRecipes, recipesNotSelected) : new HashSet<>();
            recipes.addAll(selectedRecipes);

            return recipes.stream().map(recipeDtoMapper).sorted(Comparator.comparing(RecipeDto::name)).toList();
        } catch (IllegalArgumentException | NotFoundException e ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
