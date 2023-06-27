import { Button } from "@mui/material";
import { useState, FC } from "react";
import PostAddIcon from "@mui/icons-material/PostAdd";
import IngredientAddModal from "./IngredientAddModal";

const IngredientAddButton: FC = () => {
  const [openNewRecipeModal, setOpenNewRecipeModal] = useState(false);
  const handleOpen = () => setOpenNewRecipeModal(true);
  const handleClose = () => {
    setOpenNewRecipeModal(false);
  };

  return (
    <div>
      <Button
        aria-label="Add new ingredient"
        onClick={handleOpen}
        endIcon={<PostAddIcon />}
      >
        Add Ingredient
      </Button>
      <IngredientAddModal open={openNewRecipeModal} handleClose={handleClose} />
    </div>
  );
};

export default IngredientAddButton;
