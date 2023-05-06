package com.macalester.mealplanner.ingredients;

import com.macalester.mealplanner.exceptions.UniqueConstraintViolationException;
import com.macalester.mealplanner.ingredients.dto.IngredientCreateDto;
import com.macalester.mealplanner.ingredients.dto.IngredientDto;
import com.macalester.mealplanner.ingredients.dto.IngredientMapper;
import jakarta.validation.Valid;
import java.util.List;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

  private final IngredientService ingredientService;
  private final IngredientMapper mapper = Mappers.getMapper(IngredientMapper.class);

  public IngredientController(IngredientService ingredientService) {
    this.ingredientService = ingredientService;
  }

  @GetMapping
  public List<IngredientDto> getAllIngredients() {
    return mapper.ingredientToDto(ingredientService.findAll());
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
}
