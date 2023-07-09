import { FC } from "react";
import { useGetAllRecipesQuery } from "../../api/recipes";
import RecipeCard from "./recipe-card/RecipeCard";
import { Box, Grid } from "@mui/material";

const RecipeBoard: FC = () => {
  const { data: recipes } = useGetAllRecipesQuery();

  return (
    <Box
      sx={{
        backgroundColor: "tertiary.main",
        overflowY: "auto",
        padding: "1rem",
        borderRadius: "1rem",
        width: "80%",
        height: "80%",
        alignItems: "start",
      }}
    >
      <Grid
        container
        rowSpacing={1}
        columnSpacing={{ xs: 1 }}
        sx={{ justifyContent: "space-evenly" }}
      >
        {recipes?.map((recipe) => (
          <Grid item key={recipe.id}>
            <RecipeCard {...recipe} />
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default RecipeBoard;
