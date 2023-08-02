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

  return (
    <Card
      sx={{
        width: RECIPE_CARD_WIDTH + "px",
        m: 1,
        backgroundColor: isSelected ? "secondary.main" : "primary.main",
        border: 5,
        borderColor: isSelected ? "highlights.main" : "secondary.main",
        boxShadow: 5,
      }}
    >
      <Grid container alignItems={"center"} justifyContent={"center"}>
        <Grid item xs={10}>
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
        </Grid>
        <Grid item xs={1} alignItems={"center"}>
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
