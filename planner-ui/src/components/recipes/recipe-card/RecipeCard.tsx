import { Card, CardActionArea, CardContent, Grid } from "@mui/material";
import Typography from "@mui/material/Typography";
import { FC, useState } from "react";
import { Recipe } from "../../../interfaces/RecipeInterface";
import { capitalise } from "../../../utils";
import RecipeInfoModal from "./RecipeInfoModal";
import { RECIPE_CARD_WIDTH } from "../../../constants";
import { useAppDispatch, useAppSelector } from "../../../hooks/redux-hooks";
import { toggleId } from "../../../selectedRecipesReducer";
import SelectRecipeButton from "../../button/SelectRecipeButton";
import { getDietTypeDisplayEmoji } from "../../../interfaces/DietType";

const RecipeCard: FC<Recipe> = (recipe) => {
  const [openModal, setOpenModal] = useState(false);
  const handleModalClose = () => {
    setOpenModal(false);
  };
  const handleCardClick = () => {
    setOpenModal(true);
  };

  const selectedRecipeIds: string[] = useAppSelector(
    (state) => state.selectedRecipeIds.ids
  );
  const isSelected: boolean = selectedRecipeIds.includes(recipe.id);

  const dispatch = useAppDispatch();
  const handleRecipeSelectClick = () => {
    dispatch(toggleId(recipe.id));
  };

  const cardColour = isSelected ? "base.main" : "diet." + recipe.dietType;

  return (
    <Card
      sx={{
        width: RECIPE_CARD_WIDTH + "px",
        m: 1,
        backgroundColor: cardColour,
        border: 5,
        borderColor: "secondary.main",
        boxShadow: 5,
      }}
      aria-label="Card containing recipe information"
    >
      <Grid
        container
        alignItems={"center"}
        flexDirection={"row"}
        justifyContent={"center"}
      >
        <Grid item xs={10} alignItems={"center"} justifyContent={"center"}>
          <CardActionArea onClick={() => handleCardClick()}>
            <CardContent>
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                }}
              >
                <Typography
                  variant={"h5"}
                  marginRight={"1rem"}
                  aria-label="Diet type emoji"
                >
                  {getDietTypeDisplayEmoji(recipe.dietType)}
                </Typography>
                <Typography
                  variant={"h5"}
                  color={
                    !isSelected
                      ? "recipeCardText.main"
                      : "recipeCardText.selected"
                  }
                  sx={{
                    textOverflow: "ellipsis",
                    overflow: "hidden",
                    display: "-webkit-box",
                    WebkitLineClamp: "2",
                    WebkitBoxOrient: "vertical",
                  }}
                  aria-label="Recipe name"
                >
                  {capitalise(recipe.name)}
                </Typography>
              </div>
            </CardContent>
          </CardActionArea>
        </Grid>
        <Grid item xs={1} alignItems={"center"} marginRight={1}>
          <SelectRecipeButton
            isSelected={isSelected}
            name={recipe.name}
            handleClick={handleRecipeSelectClick}
          />
        </Grid>
      </Grid>

      <RecipeInfoModal
        recipe={recipe}
        open={openModal}
        handleClose={handleModalClose}
      />
    </Card>
  );
};

export default RecipeCard;
