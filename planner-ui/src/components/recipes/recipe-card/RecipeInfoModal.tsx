import { FC } from "react";
import { Recipe } from "../../../interfaces/RecipeInterface";
import Typography from "@mui/material/Typography";
import { Box, Fade, List, ListItem, Modal } from "@mui/material";
import { capitalise } from "../../../utils";

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
  const onClose = () => {
    handleClose();
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
            <Typography variant="h4">{capitalise(recipe.name)} </Typography>
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
    </>
  );
};

export default RecipeInfoModal;
