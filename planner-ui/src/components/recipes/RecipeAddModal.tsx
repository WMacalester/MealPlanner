import { FC, useState } from "react";
import Modal from "@mui/material/Modal";
import Fade from "@mui/material/Fade";
import Button from "@mui/material/Button";
import {
  FormControl,
  SelectChangeEvent,
  TextField,
  Typography,
} from "@mui/material";
import { useCreateNewRecipeMutation } from "../../api/recipes";
import { RecipeCreateDto } from "../../interfaces/RecipeInterface";
import { isNameAlpha } from "../../utils";
import { useGetAllIngredientsQuery } from "../../api/ingredients";
import { IngredientSelect } from "./IngredientSelect";
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

const recipeNameAlreadyExistsRegex = new RegExp(/^Recipe.*already exists/);

const recipeNameInvalidMessage = "Name can only contain letters and spaces";
const recipeNameAlreadyExistsMessage = "A recipe with that name already exists";

const RecipeAddModal: FC<AddModalProps> = ({ open, handleClose }) => {
  const [recipeName, setRecipeName] = useState("");
  const [recipeNameErrorMessage, setRecipeNameErrorMessage] = useState("");
  const [formErrorMessage, setFormErrorMessage] = useState("");
  const { data: availableIngredients } = useGetAllIngredientsQuery();
  const [selectedIngredientIds, setSelectedIngredientIds] = useState<string[]>(
    []
  );
  const [submit] = useCreateNewRecipeMutation();

  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRecipeNameErrorMessage("");
    setRecipeName(event.target.value);
    if (!isNameAlpha(event.target.value.trim())) {
      setRecipeNameErrorMessage(recipeNameInvalidMessage);
    }
  };

  const handleIngredientSelect = (
    event: SelectChangeEvent<typeof selectedIngredientIds>
  ) => {
    setSelectedIngredientIds(
      typeof event.target.value === "string"
        ? event.target.value.split(",")
        : event.target.value
    );
  };

  const onClose = () => {
    handleClose();
    setRecipeName("");
    setRecipeNameErrorMessage("");
    setFormErrorMessage("");
    setSelectedIngredientIds([]);
  };

  const handleSubmit = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    if (!isSubmitDisabled) {
      const newRecipe: RecipeCreateDto = {
        name: recipeName.trim(),
        ingredientIds: selectedIngredientIds.map((e) => e.split(/"(.*?)"/)[3]),
      };
      submit(newRecipe)
        .unwrap()
        .then(() => onClose())
        .catch((error) => {
          if (recipeNameAlreadyExistsRegex.test(error.data.message)) {
            setRecipeNameErrorMessage(recipeNameAlreadyExistsMessage);
          } else {
            setFormErrorMessage(
              "An unexpected error has occurred. Please try again."
            );
          }
        });
    }
  };

  const isRecipeNameError: boolean = recipeNameErrorMessage.length !== 0;
  const isSubmitDisabled: boolean =
    recipeName.trim().length === 0 || isRecipeNameError;

  return (
    <div>
      <Modal
        aria-label="Form for creating a recipe"
        open={open}
        onClose={onClose}
        closeAfterTransition
      >
        <Fade in={open}>
          <FormControl sx={modalStyle}>
            <Typography variant="h5" alignSelf={"center"} paddingBottom={1.5}>
              Add a New Recipe
            </Typography>
            <TextField
              required
              id="Recipe name"
              label="Recipe name"
              name={recipeName}
              onChange={handleNameChange}
              error={isRecipeNameError}
              helperText={`${recipeNameErrorMessage}`}
              sx={{
                marginBottom: "1rem",
                ".MuiInputBase-label": { fontSize: "1.25rem" },
              }}
            />

            <IngredientSelect
              availableIngredients={availableIngredients}
              selectedIngredientIds={selectedIngredientIds}
              handleIngredientSelect={handleIngredientSelect}
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

export default RecipeAddModal;