import { FC } from "react";
import { useGetAllRecipesQuery } from "../../api/recipes";
import RecipeCard from "./RecipeCard";
import { Box } from "@mui/material";
import AddButton from "../AddButton";
import RecipeAddModal from "./RecipeAddModal";
import IngredientAddModal from "../ingredients/IngredientAddModal";

const RecipeBoard: FC = () => {
  const { data: recipes } = useGetAllRecipesQuery();

  return (
    <Box>
      <AddButton Modal={IngredientAddModal} label={"Add Ingredient"} />
      <AddButton Modal={RecipeAddModal} label={"Add Recipe"} />
      {recipes?.map((recipe) => (
        <RecipeCard key={recipe.id} {...recipe} />
      ))}
    </Box>
  );
};

export default RecipeBoard;
