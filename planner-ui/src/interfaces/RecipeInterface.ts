import { Ingredient } from "./IngredientInterface";

export interface Recipe {
  id: string;
  name: string;
  ingredients: Ingredient[];
}
