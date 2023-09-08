import { FC, useEffect, useState } from "react";
import { Box, Grid, TextField } from "@mui/material";
import { BOARD_HEIGHT, RECIPE_CARD_WIDTH } from "../../constants";
import { useCreateRandomMenuMutation } from "../../api/menu";
import RecipeCard from "../recipes/recipe-card/RecipeCard";
import RerollButton from "../button/RerollButton";
import { useGetAllRecipesQuery } from "../../api/recipes";
import { useAppSelector } from "../../hooks/redux-hooks";
import { TextFieldInputLabelProps } from "../CommonStyles";

const Menuboard: FC = () => {
  const [trigger, result] = useCreateRandomMenuMutation();
  const { data: recipes } = useGetAllRecipesQuery({});

  const selectedRecipeIds = useAppSelector(
    (state) => state.selectedRecipeIds.ids
  );
  const minNumberOfRecipes = Math.max(1, selectedRecipeIds.length);
  const [numberRequestedRecipes, setNumberRequestedRecipes] =
    useState<number>(minNumberOfRecipes);

  useEffect(() => {
    setNumberRequestedRecipes(
      Math.max(minNumberOfRecipes, numberRequestedRecipes)
    );
  }, [minNumberOfRecipes, numberRequestedRecipes]);

  const handleRerollClick = () => {
    trigger({
      number: numberRequestedRecipes,
      payload: { recipeIds: selectedRecipeIds },
    });
  };

  const handleNumberFieldChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    value.toString().match(/[0-9]*/) &&
      setNumberRequestedRecipes(parseInt(value));
  };

  return (
    <Box
      border={4}
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
        <TextField
          variant="standard"
          value={numberRequestedRecipes}
          type="number"
          label="Number of Recipes"
          onChange={handleNumberFieldChange}
          InputProps={{
            inputProps: {
              min: minNumberOfRecipes,
              max: recipes?.length,
              color: "highlights.main",
            },
          }}
          InputLabelProps={TextFieldInputLabelProps}
          sx={{
            width: "7rem",
            input: { color: "highlights.main" },
          }}
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
