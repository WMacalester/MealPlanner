package com.macalester.mealplanner.recipes;

import static com.macalester.mealplanner.Utils.formatCurrentDate;

import com.macalester.mealplanner.dataexporter.DataExporter;
import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.filter.FilterRequest;
import com.macalester.mealplanner.filter.FilterService;
import com.macalester.mealplanner.recipes.dto.RecipeCreateDto;
import com.macalester.mealplanner.recipes.dto.RecipeCreateDtoMapper;
import com.macalester.mealplanner.recipes.dto.RecipeDto;
import com.macalester.mealplanner.recipes.dto.RecipeDtoMapper;
import jakarta.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeDtoMapper recipeDtoMapper;
    private final RecipeCreateDtoMapper recipeCreateDtoMapper;
    private final DataExporter dataExporter;
    private final FilterService filterService;

    private static final String RECIPE_FILENAME = "dataRecipesExport";

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<RecipeDto> getAllRecipes(@RequestParam(required = false) String recipeName) {
        try {
            FilterRequest filterRequest = new FilterRequest();

            if (recipeName != null && !recipeName.isBlank()) {
                filterRequest.setName(recipeName);
            }

            List<Recipe> recipes = filterService.findAllRecipesWithCriteria(filterRequest);
            return recipes.stream().sorted(Comparator.comparing(Recipe::getName)).map(recipeDtoMapper).toList();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(produces = {"text/csv"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> exportAllRecipesCsv(@RequestHeader(value = "Accept", defaultValue = "text/csv") String acceptHeader) {
        String filename = RECIPE_FILENAME + "_" + formatCurrentDate() + ".csv";
        String csv = dataExporter.exportRecipes(recipeService.getAllRecipes());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public RecipeDto getRecipeById(@PathVariable UUID id) {
        try {
            return recipeDtoMapper.apply(recipeService.findById(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RecipeDto addRecipe(@Valid @RequestBody RecipeCreateDto recipeCreateDto) {
        try {
            Recipe newRecipe = recipeCreateDtoMapper.apply(recipeCreateDto);
            return recipeDtoMapper.apply(recipeService.addRecipe(newRecipe));
        } catch (UniqueConstraintViolationException | NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipeById(@PathVariable UUID id) {
        recipeService.deleteById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RecipeDto editRecipeById(
            @PathVariable UUID id, @RequestBody @Valid RecipeCreateDto recipeCreateDto) {
        try {
            return recipeDtoMapper.apply(
                    recipeService.editRecipeById(id, recipeCreateDtoMapper.apply(recipeCreateDto)));
        } catch (UniqueConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            if (e.getMessage().toLowerCase().contains("recipe")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
