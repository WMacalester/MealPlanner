package com.macalester.mealplanner.menu;

import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.dto.RecipeDto;
import com.macalester.mealplanner.recipes.dto.RecipeDtoMapper;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final RecipeDtoMapper recipeDtoMapper;

    @GetMapping
    public List<RecipeDto> getRandomUniqueRecipes(@RequestParam int number){
        try {
            Set<Recipe> recipes = menuService.getRandomUniqueRecipes(number);

            return recipes.stream().map(recipeDtoMapper).sorted(Comparator.comparing(RecipeDto::name)).toList();
        } catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
