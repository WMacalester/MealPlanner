package com.macalester.mealplanner.ingredients;

import com.macalester.mealplanner.exceptions.NotFoundException;
import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.ingredients.dto.IngredientCreateDto;
import com.macalester.mealplanner.ingredients.dto.IngredientDto;
import com.macalester.mealplanner.ingredients.dto.IngredientEditDto;
import com.macalester.mealplanner.ingredients.dto.IngredientMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
public class IngredientController {

  private final IngredientService ingredientService;
  private final IngredientMapper mapper = Mappers.getMapper(IngredientMapper.class);

  @GetMapping
  public List<IngredientDto> getAllIngredients() {
    return mapper.ingredientToDto(ingredientService.findAll());
  }

  @GetMapping("/{id}")
  public IngredientDto getIngredientById(@PathVariable UUID id) {
    try {
      return mapper.ingredientToDto(ingredientService.findById(id));
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping
  public IngredientDto addIngredient(@Valid @RequestBody IngredientCreateDto ingredientCreateDTO) {
    try {
      Ingredient newIngredient = mapper.ingredientCreateDTOtoIngredient(ingredientCreateDTO);
      return mapper.ingredientToDto(ingredientService.save(newIngredient));
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
      return mapper.ingredientToDto(ingredientService.editIngredientById(id, editIngredientDto));
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (UniqueConstraintViolationException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
