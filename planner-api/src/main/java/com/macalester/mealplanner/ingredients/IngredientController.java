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

  @GetMapping
  public List<IngredientDto> getAllIngredients() {
    return ingredientService.findAll().stream().map(ingredientDtoMapper).sorted(Comparator.comparing(IngredientDto::name)).toList();
  }

  @GetMapping(produces = "text/csv")
  public ResponseEntity<String> exportAllIngredientsAsCsv(@RequestHeader(value = "Accept", defaultValue = "text/csv") String acceptheader){
      String filename = INGREDIENT_FILENAME+"_"+formatCurrentDate()+".csv";
      String csv = dataExporter.exportIngredients(ingredientService.findAll());
      return ResponseEntity.ok()
              .header("Content-Disposition", "attachment; filename="+filename)
              .contentType(MediaType.parseMediaType("text/csv"))
              .body(csv);
  }

  @GetMapping("/{id}")
  public IngredientDto getIngredientById(@PathVariable UUID id) {
    try {
      return ingredientDtoMapper.apply(ingredientService.findById(id));
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping
  public IngredientDto addIngredient(@Valid @RequestBody IngredientCreateDto ingredientCreateDTO) {
    try {
      Ingredient newIngredient = ingredientCreateDtoMapper.apply(ingredientCreateDTO);
      return ingredientDtoMapper.apply(ingredientService.save(newIngredient));
    } catch (UniqueConstraintViolationException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteIngredientById(@PathVariable UUID id) {
    ingredientService.deleteById(id);
  }

  @PatchMapping("/{id}")
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
