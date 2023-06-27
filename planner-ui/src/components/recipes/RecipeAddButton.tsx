import { IconButton } from "@mui/material";
import { useState, FC } from "react";
import PostAddIcon from "@mui/icons-material/PostAdd";
import RecipeAddModal from "./RecipeAddModal";

const RecipeAddButton: FC = () => {
  const [openNewRecipeModal, setOpenNewRecipeModal] = useState(false);
  const handleOpen = () => setOpenNewRecipeModal(true);
  const handleClose = () => {
    setOpenNewRecipeModal(false);
  };

  return (
    <div>
      <IconButton aria-label="Add new recipe" onClick={handleOpen}>
        <PostAddIcon />
      </IconButton>
      <RecipeAddModal open={openNewRecipeModal} handleClose={handleClose} />
    </div>
  );
};

export default RecipeAddButton;
