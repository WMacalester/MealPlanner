import { FC } from "react";
import { useGetAllRecipesQuery } from "../../api/recipes";
import RecipeCard from "./RecipeCard";
import { Box } from "@mui/material";
import RecipeAddButton from "./RecipeAddButton";

const RecipeBoard: FC = () => {
  const { data: recipes } = useGetAllRecipesQuery();

  return (
    <Box>
      <RecipeAddButton />
      {recipes?.map((recipe) => (
        <RecipeCard key={recipe.id} {...recipe} />
      ))}
    </Box>
  );
};

export default RecipeBoard;
