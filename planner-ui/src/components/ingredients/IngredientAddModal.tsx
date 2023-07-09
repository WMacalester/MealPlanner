import { FC, useState } from "react";
import Modal from "@mui/material/Modal";
import Fade from "@mui/material/Fade";
import Button from "@mui/material/Button";
import { FormControl, Typography } from "@mui/material";
import { useCreateNewIngredientMutation } from "../../api/ingredients";
import { IngredientCreateDto } from "../../interfaces/IngredientInterface";
import { AddModalProps } from "../../interfaces/AddModalProps";
import NameInputField from "../NameInputField";
import SubmitAndCancelButtons from "../button/SubmitAndCancelButtons";

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

const ingredientNameAlreadyExistsMessage =
  "An ingredient with that name already exists";

const IngredientAddModal: FC<AddModalProps> = ({ open, handleClose }) => {
  const [ingredientName, setIngredientName] = useState("");
  const [ingredientNameErrorMessage, setIngredientNameErrorMessage] =
    useState("");
  const [formErrorMessage, setFormErrorMessage] = useState("");
  const [submit] = useCreateNewIngredientMutation();

  const onClose = () => {
    handleClose();
    setIngredientName("");
    setIngredientNameErrorMessage("");
    setFormErrorMessage("");
  };

  const handleEnterPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSubmit();
    }
  };

  const handleSubmit = () => {
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

  const handleSubmitClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    handleSubmit();
  };

  const handleCancelClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    onClose();
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
          <FormControl sx={modalStyle} onKeyDown={handleEnterPress}>
            <Typography variant="h5" alignSelf={"center"} paddingBottom={1.5}>
              Add a New Ingredient
            </Typography>

            <NameInputField
              name={ingredientName}
              setName={setIngredientName}
              setErrorMessage={setIngredientNameErrorMessage}
              isError={isIngredientNameError}
              helperText={ingredientNameErrorMessage}
            />

            <SubmitAndCancelButtons
              handleCancelClick={handleCancelClick}
              handleSubmitClick={handleSubmitClick}
              isSubmitDisabled={isSubmitDisabled}
            />

            <Typography>{formErrorMessage}</Typography>
          </FormControl>
        </Fade>
      </Modal>
    </div>
  );
};

export default IngredientAddModal;
