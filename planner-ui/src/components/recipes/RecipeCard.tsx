import { Card, CardContent } from "@mui/material";
import Typography from "@mui/material/Typography";
import { FC } from "react";
import { Recipe } from "../../interfaces/RecipeInterface";

const RecipeCard: FC<Recipe> = (recipe) => {
  return (
    <Card
      sx={{
        maxWidth: "25%",
        m: 1,
        backgroundColor: "#000000",
        boxShadow: 5,
      }}
    >
      <CardContent>
        <Typography color="#FFFFFF" gutterBottom>
          {recipe.name}
        </Typography>
      </CardContent>
    </Card>
  );
};

export default RecipeCard;
