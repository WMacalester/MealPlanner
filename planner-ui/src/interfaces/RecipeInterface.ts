import { Ingredient } from "./IngredientInterface";

export interface Recipe {
  id: string;
  name: string;
  ingredients: Ingredient[];
}

export interface RecipeCreateDto {
  name: string;
  ingredientIds: string[];
}

export interface RecipeEditDto {
  name: string;
  ingredientIds: string[];
}
