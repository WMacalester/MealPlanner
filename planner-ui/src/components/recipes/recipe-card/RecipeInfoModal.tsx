import { FC } from "react";
import { Recipe } from "../../../interfaces/RecipeInterface";
import Typography from "@mui/material/Typography";
import { Box, Fade, IconButton, List, ListItem, Modal } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import { capitalise } from "../../../utils";
import RecipeEditModal from "../RecipeEditModal";
import React from "react";

interface RecipeInfoModalProps {
  recipe: Recipe;
  open: boolean;
  handleClose: () => void;
}
const modalStyle = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 400,
  bgcolor: "background.paper",
  border: "2px solid #000",
  boxShadow: 24,
  p: 4,
  maxHeight: "50%",
};

const RecipeInfoModal: FC<RecipeInfoModalProps> = ({
  recipe,
  open,
  handleClose,
}) => {
  const [editModalOpen, setEditModalOpen] = React.useState(false);
  const onClose = () => {
    handleClose();
  };

  const handleEditModalClose = () => {
    setEditModalOpen(false);
  };

  return (
    <>
      <Modal
        aria-label="Form for creating a ingredient"
        open={open}
        onClose={onClose}
        closeAfterTransition
      >
        <Fade in={open}>
          <Box sx={modalStyle}>
            <Box justifyContent={"space-between"} display={"flex"}>
              <Typography variant="h4">{capitalise(recipe.name)} </Typography>{" "}
              <IconButton onClick={() => setEditModalOpen(true)}>
                <EditIcon />
              </IconButton>
            </Box>
            {recipe.ingredients?.length > 0 ? (
              <List>
                {recipe.ingredients.map((ingredient) => (
                  <ListItem key={ingredient.id}>
                    <Typography variant="h5">{ingredient.name}</Typography>
                  </ListItem>
                ))}
              </List>
            ) : (
              <Typography variant="h5" sx={{ padding: "1rem" }}>
                No ingredients available
              </Typography>
            )}
          </Box>
        </Fade>
      </Modal>
      <RecipeEditModal
        open={editModalOpen}
        data={recipe}
        handleClose={handleEditModalClose}
      />
    </>
  );
};

export default RecipeInfoModal;
