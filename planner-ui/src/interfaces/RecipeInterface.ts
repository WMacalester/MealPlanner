import { DietType } from "./DietType";
import { Ingredient } from "./IngredientInterface";

export interface Recipe {
  id: string;
  name: string;
  dietType: DietType;
  ingredients: Ingredient[];
}

export interface RecipeCreateDto {
  name: string;
  dietType: DietType;
  ingredientIds: string[];
}

export interface RecipeEditDto {
  name: string;
  dietType: DietType;
  ingredientIds: string[];
}
