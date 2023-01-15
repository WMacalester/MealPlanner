import { FC } from "react";
import { useGetAllRecipesQuery } from "../../api/recipes";
import RecipeCard from "./RecipeCard";

const Recipes: FC = () => {
  const { data: recipes } = useGetAllRecipesQuery();

  return (
    <>
      {recipes?.map((recipe) => (
        <RecipeCard key={recipe.id} {...recipe} />
      ))}
    </>
  );
};

export default Recipes;
