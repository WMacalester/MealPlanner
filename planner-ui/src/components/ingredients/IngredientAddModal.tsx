import { FC, useState } from "react";
import Modal from "@mui/material/Modal";
import Fade from "@mui/material/Fade";
import Button from "@mui/material/Button";
import { FormControl, TextField, Typography } from "@mui/material";
import { useCreateNewIngredientMutation } from "../../api/ingredients";
import { isNameAlpha } from "../../utils";
import { IngredientCreateDto } from "../../interfaces/IngredientInterface";
import { AddModalProps } from "../../interfaces/AddModalProps";

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

const ingredientNameAlreadyExistsRegex = new RegExp(
  /^Ingredient.*already exists/
);

const ingredientNameInvalidMessage = "Name can only contain letters and spaces";
const ingredientNameAlreadyExistsMessage =
  "An ingredient with that name already exists";

const IngredientAddModal: FC<AddModalProps> = ({ open, handleClose }) => {
  const [ingredientName, setIngredientName] = useState("");
  const [ingredientNameErrorMessage, setIngredientNameErrorMessage] =
    useState("");
  const [formErrorMessage, setFormErrorMessage] = useState("");
  const [submit] = useCreateNewIngredientMutation();

  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIngredientNameErrorMessage("");
    setIngredientName(event.target.value);
    if (!isNameAlpha(event.target.value.trim())) {
      setIngredientNameErrorMessage(ingredientNameInvalidMessage);
    }
  };

  const onClose = () => {
    handleClose();
    setIngredientName("");
    setIngredientNameErrorMessage("");
    setFormErrorMessage("");
  };

  const handleSubmit = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    if (!isSubmitDisabled) {
      const newIngredient: IngredientCreateDto = {
        name: ingredientName.trim(),
      };
      submit(newIngredient)
        .unwrap()
        .then((e) => {
          onClose();
        })
        .catch((error) => {
          if (ingredientNameAlreadyExistsRegex.test(error.data.message)) {
            setIngredientNameErrorMessage(ingredientNameAlreadyExistsMessage);
          } else {
            setFormErrorMessage(
              "An unexpected error has occurred. Please try again."
            );
          }
        });
    }
  };

  const isIngredientNameError: boolean =
    ingredientNameErrorMessage.length !== 0;
  const isSubmitDisabled: boolean =
    ingredientName.length === 0 || isIngredientNameError;

  return (
    <div>
      <Modal
        aria-label="Form for creating a ingredient"
        open={open}
        onClose={onClose}
        closeAfterTransition
      >
        <Fade in={open}>
          <FormControl sx={modalStyle}>
            <Typography variant="h5" alignSelf={"center"} paddingBottom={1.5}>
              Add a New Ingredient
            </Typography>
            <TextField
              required
              id="Ingredient name"
              label="Ingredient name"
              name={ingredientName}
              onChange={handleNameChange}
              error={isIngredientNameError}
              helperText={`${ingredientNameErrorMessage}`}
              sx={{
                ".MuiInputBase-label": { fontSize: "1.25rem" },
              }}
            />
            <Button
              onClick={handleSubmit}
              disabled={isSubmitDisabled}
              variant="outlined"
              type="submit"
              sx={{
                width: "30%",
                marginTop: "1rem",
                color: "primary.main",
                ":hover": {
                  color: "highlights.main",
                  bgcolor: "primary.main",
                },
              }}
            >
              Submit
            </Button>
            <Typography>{formErrorMessage}</Typography>
          </FormControl>
        </Fade>
      </Modal>
    </div>
  );
};

export default IngredientAddModal;
