import { IconButton } from "@mui/material";
import { useState, FC } from "react";
import NewRecipeModal from "./NewRecipeModal";
import PostAddIcon from "@mui/icons-material/PostAdd";

const NewRecipeButton: FC = () => {
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
      <NewRecipeModal open={openNewRecipeModal} handleClose={handleClose} />
    </div>
  );
};

export default NewRecipeButton;
