import { FC, useState } from "react";
import { Box, Grid, TextField } from "@mui/material";
import { BOARD_HEIGHT, RECIPE_CARD_WIDTH } from "../../constants";
import { useLazyGetRandomMenuQuery } from "../../api/menu";
import RecipeCard from "../recipes/recipe-card/RecipeCard";
import RerollButton from "../button/RerollButton";
import { useGetAllRecipesQuery } from "../../api/recipes";

const Menuboard: FC = () => {
  const [numberRequestedRecipes, setNumberRequestedRecipes] =
    useState<number>(1);
  const [trigger, result] = useLazyGetRandomMenuQuery();
  const { data: recipes } = useGetAllRecipesQuery();

  const handleRerollClick = () => {
    trigger(numberRequestedRecipes, false);
  };

  const handleNumberFieldChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    value.toString().match(/[0-9]*/) &&
      setNumberRequestedRecipes(parseInt(value));
  };

  return (
    <Box
      sx={{
        backgroundColor: "tertiary.main",
        padding: "1rem",
        borderRadius: "1rem",
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
              min: 1,
              max: recipes?.length,
            },
          }}
          sx={{ width: "7rem" }}
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
