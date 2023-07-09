import { useDeleteRecipeMutation } from "../api/recipes";
import { Box, Button, Fade, Modal, Typography } from "@mui/material";
import CancelButton from "./button/CancelButton";
import { Recipe } from "../interfaces/RecipeInterface";
import { FC } from "react";
import { MutationModalProps } from "../interfaces/AddModalProps";

const modalStyle = {
  position: "absolute" as "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 400,
  bgcolor: "background.paper",
  border: "2px solid #000",
  boxShadow: 24,
  p: 4,
};

const RecipeDeleteModal: FC<MutationModalProps<Recipe>> = ({
  data: recipe,
  open,
  handleClose,
}) => {
  const [deleteRecipe] = useDeleteRecipeMutation();
  const handleDeleteClick = () => {
    deleteRecipe(recipe.id);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleDeleteClick();
    } else if (e.key === "Escape") {
      handleClose();
    }
  };

  return (
    <Modal
      aria-label="Form for editing a recipe"
      open={open}
      onClose={handleClose}
      closeAfterTransition
    >
      <Fade in={open}>
        <Box sx={modalStyle} onKeyDown={handleKeyDown}>
          <Typography variant="h5">
            Are you sure you want to delete {recipe.name}?
          </Typography>
          <Box
            display={"flex"}
            justifyContent={"space-evenly"}
            sx={{
              marginTop: "1rem",
            }}
          >
            <CancelButton handleClick={handleClose} />
            <Button
              onClick={handleDeleteClick}
              variant="outlined"
              sx={{
                color: "primary.main",
                ":hover": {
                  color: "highlights.main",
                  backgroundColor: "primary.main",
                },
              }}
            >
              Delete
            </Button>
          </Box>
        </Box>
      </Fade>
    </Modal>
  );
};

export default RecipeDeleteModal;
