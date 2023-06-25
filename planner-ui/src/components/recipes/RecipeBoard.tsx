import { FC } from "react";
import { useGetAllRecipesQuery } from "../../api/recipes";
import RecipeCard from "./RecipeCard";
import NewRecipeButton from "./NewRecipeButton";
import { Box } from "@mui/material";

const RecipeBoard: FC = () => {
  const { data: recipes } = useGetAllRecipesQuery();

  return (
    <Box>
      <NewRecipeButton />
      {recipes?.map((recipe) => (
        <RecipeCard key={recipe.id} {...recipe} />
      ))}
    </Box>
  );
};

export default RecipeBoard;
