import { FC, useEffect, useState } from "react";
import { Box, Grid } from "@mui/material";
import { BOARD_HEIGHT, RECIPE_CARD_WIDTH } from "../../constants";
import { useCreateRandomMenuMutation } from "../../api/menu";
import RecipeCard from "../recipes/recipe-card/RecipeCard";
import RerollButton from "../button/RerollButton";
import { useGetAllRecipesQuery } from "../../api/recipes";
import { useAppSelector } from "../../hooks/redux-hooks";
import StyledTextField from "../styles/StyledTextField";

const Menuboard: FC = () => {
  const [trigger, result] = useCreateRandomMenuMutation();
  const { data: recipes } = useGetAllRecipesQuery({});

  const selectedRecipeIds = useAppSelector(
    (state) => state.selectedRecipeIds.ids
  );
  const minNumberOfRecipes = Math.max(1, selectedRecipeIds.length);
  const maxNumberOfRecipes = recipes ? recipes.length : 0;
  const [numberRequestedRecipes, setNumberRequestedRecipes] =
    useState<number>(0);

  useEffect(() => {
    const value = recipes?.length
      ? Math.max(minNumberOfRecipes, numberRequestedRecipes)
      : 0;
    setNumberRequestedRecipes(value);
  }, [minNumberOfRecipes, numberRequestedRecipes, recipes]);

  const handleRerollClick = () => {
    trigger({
      number: numberRequestedRecipes,
      payload: { recipeIds: selectedRecipeIds },
    });
  };

  const handleNumberFieldChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = parseInt(e.target.value);
    value <= maxNumberOfRecipes && setNumberRequestedRecipes(value);
  };

  return (
    <Box
      border={4}
      aria-label="Board for viewing and creating a menu"
      sx={{
        backgroundColor: "primary.main",
        padding: "1rem",
        borderRadius: "1rem",
        borderColor: "secondary.main",
        borderWidth: "1rem",
        width: RECIPE_CARD_WIDTH * 1.2 + "px",
        height: BOARD_HEIGHT,
        alignItems: "start",
      }}
    >
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          paddingY: "1rem",
          paddingX: "1rem",
        }}
      >
        <StyledTextField
          value={numberRequestedRecipes}
          type="number"
          onChange={handleNumberFieldChange}
          min={minNumberOfRecipes}
          max={maxNumberOfRecipes}
          label="Number of Recipes"
          width="40%"
          aria-label="Number of recipes to be included in generated menu"
        />
        <RerollButton handleClick={handleRerollClick} />
      </Box>
      <Grid
        container
        aria-label="The collection of recipes from the created menu"
        sx={{
          height: "85%",
          overflowY: "auto",
          overflowX: "hidden",
          alignContent: "flex-start",
          justifyContent: "center",
        }}
      >
        {result?.data?.map((recipe) => (
          <Grid item key={recipe.id} aria-label={recipe.name}>
            <RecipeCard {...recipe} />
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default Menuboard;
