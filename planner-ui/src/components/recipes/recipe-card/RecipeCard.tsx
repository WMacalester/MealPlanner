import { Card, CardActionArea, CardContent } from "@mui/material";
import Typography from "@mui/material/Typography";
import { FC, useState } from "react";
import { Recipe } from "../../../interfaces/RecipeInterface";
import { capitalise } from "../../../utils";
import RecipeInfoModal from "./RecipeInfoModal";
import { RECIPE_CARD_WIDTH } from "../../../constants";

const RecipeCard: FC<Recipe> = (recipe) => {
  const [openModal, setOpenModal] = useState(false);
  const handleModalClose = () => {
    setOpenModal(false);
  };
  const handleCardClick = () => {
    setOpenModal(true);
  };

  return (
    <Card
      sx={{
        width: RECIPE_CARD_WIDTH + "px",
        m: 1,
        backgroundColor: "primary.main",
        border: 5,
        borderColor: "secondary.main",
        boxShadow: 5,
      }}
    >
      <CardActionArea onClick={() => handleCardClick()}>
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
        </CardContent>
      </CardActionArea>

      <RecipeInfoModal
        recipe={recipe}
        open={openModal}
        handleClose={handleModalClose}
      />
    </Card>
  );
};

export default RecipeCard;
