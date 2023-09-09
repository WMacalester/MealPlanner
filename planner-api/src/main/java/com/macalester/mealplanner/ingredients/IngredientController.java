package com.macalester.mealplanner.ingredients;

import static com.macalester.mealplanner.Utils.formatCurrentDate;

import com.macalester.mealplanner.dataexporter.DataExporter;
import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.ingredients.dto.IngredientCreateDto;
import com.macalester.mealplanner.ingredients.dto.IngredientCreateDtoMapper;
import com.macalester.mealplanner.ingredients.dto.IngredientDto;
import com.macalester.mealplanner.ingredients.dto.IngredientDtoMapper;
import com.macalester.mealplanner.ingredients.dto.IngredientEditDto;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;
    private final IngredientCreateDtoMapper ingredientCreateDtoMapper;
    private final IngredientDtoMapper ingredientDtoMapper;
    private final DataExporter dataExporter;

    private static final String INGREDIENT_FILENAME = "dataIngredientsExport";

    /**
     * Get all ingredients
     *
     * @return List of {@link com.macalester.mealplanner.ingredients.dto.IngredientDto}
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<IngredientDto> getAllIngredients() {
        return ingredientService.findAll().stream().map(ingredientDtoMapper).sorted(Comparator.comparing(IngredientDto::name)).toList();
    }

    /**
     * Get all {@link com.macalester.mealplanner.ingredients.Ingredient} as a csv. Requires "Accept" header to be "text/csv". Requires admin permission
     *
     * @return Csv of all {@link com.macalester.mealplanner.ingredients.Ingredient}
     */
    @GetMapping(produces = "text/csv")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> exportAllIngredientsAsCsv(@RequestHeader(value = "Accept", defaultValue = "text/csv") String acceptheader) {
        String filename = INGREDIENT_FILENAME + "_" + formatCurrentDate() + ".csv";
        String csv = dataExporter.exportIngredients(ingredientService.findAll());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    /**
     * Get an ingredient by id
     *
     * @param id UUID of ingredient
     * @return {@link com.macalester.mealplanner.ingredients.dto.IngredientDto} if ingredient is found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public IngredientDto getIngredientById(@PathVariable UUID id) {
        try {
            return ingredientDtoMapper.apply(ingredientService.findById(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Create a new ingredient. Requires admin permission
     *
     * @param ingredientCreateDTO {@link com.macalester.mealplanner.ingredients.dto.IngredientCreateDto}
     * @return {@link com.macalester.mealplanner.ingredients.dto.IngredientDto} for new ingredient
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public IngredientDto addIngredient(@Valid @RequestBody IngredientCreateDto ingredientCreateDTO) {
        try {
            Ingredient newIngredient = ingredientCreateDtoMapper.apply(ingredientCreateDTO);
            return ingredientDtoMapper.apply(ingredientService.save(newIngredient));
        } catch (UniqueConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Delete an ingredient by id
     *
     * @param id UUID of ingredient
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIngredientById(@PathVariable UUID id) {
        ingredientService.deleteById(id);
    }

    /**
     * Edit an ingredient by Id. Requires admin permission
     *
     * @param id                UUID
     * @param editIngredientDto {@link com.macalester.mealplanner.ingredients.dto.IngredientEditDto}
     * @return {@link com.macalester.mealplanner.ingredients.dto.IngredientDto} for edited ingredient if ingredient is found and requested changes are valid
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public IngredientDto editIngredientById(
            @PathVariable UUID id, @RequestBody @Valid IngredientEditDto editIngredientDto) {
        try {
            return ingredientDtoMapper.apply(ingredientService.editIngredientById(id, editIngredientDto));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UniqueConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
