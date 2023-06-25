import { Card, CardContent } from "@mui/material";
import Typography from "@mui/material/Typography";
import { FC } from "react";
import { Recipe } from "../../interfaces/RecipeInterface";
import { capitalise } from "../../utils";
import IngredientSection from "./IngredientSection";

const RecipeCard: FC<Recipe> = (recipe) => {
  return (
    <Card
      sx={{
        maxWidth: "30%",
        minWidth: "300px",
        m: 1,
        backgroundColor: "primary.main",
        border: 5,
        borderColor: "secondary.main",
        boxShadow: 5,
      }}
    >
      <CardContent>
        <Typography
          variant={"h4"}
          color="highlights.main"
          sx={{
            textOverflow: "ellipsis",
            overflow: "hidden",
            display: "-webkit-box",
            WebkitLineClamp: "2",
            WebkitBoxOrient: "vertical",
          }}
        >
          {capitalise(recipe.name)}
        </Typography>
        <Typography></Typography>
      </CardContent>

      {recipe.ingredients.length > 0 && (
        <IngredientSection ingredients={recipe.ingredients} />
      )}
    </Card>
  );
};

export default RecipeCard;
