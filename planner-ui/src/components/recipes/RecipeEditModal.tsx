import { FC, useState } from "react";
import Modal from "@mui/material/Modal";
import Fade from "@mui/material/Fade";
import { Box, FormControl, SelectChangeEvent, Typography } from "@mui/material";
import { useEditRecipeMutation } from "../../api/recipes";
import { Recipe, RecipeEditDto } from "../../interfaces/RecipeInterface";
import { useGetAllIngredientsQuery } from "../../api/ingredients";
import { IngredientSelect } from "./IngredientSelect";
import { EditModalProps } from "../../interfaces/AddModalProps";
import NameInputField from "../NameInputField";
import SubmitAndCancelButtons from "../button/SubmitAndCancelButtons";
import { capitalise } from "../../utils";

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
const recipeNameAlreadyExistsMessage = "A recipe with that name already exists";

const RecipeEditModal: FC<EditModalProps<Recipe>> = ({
  open,
  handleClose,
  data: recipe,
}) => {
  const [recipeName, setRecipeName] = useState(recipe.name);
  const [recipeNameErrorMessage, setRecipeNameErrorMessage] = useState("");
  const [formErrorMessage, setFormErrorMessage] = useState("");
  const { data: availableIngredients } = useGetAllIngredientsQuery();
  const [selectedIngredientIds, setSelectedIngredientIds] = useState<string[]>(
    recipe.ingredients.map((e) => JSON.stringify(e))
  );
  const [submit] = useEditRecipeMutation();

  const isRecipeNameError: boolean = recipeNameErrorMessage.length !== 0;
  const isSubmitDisabled: boolean =
    recipeName.trim().length === 0 || isRecipeNameError;

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
    setRecipeNameErrorMessage("");
    setFormErrorMessage("");
  };

  const handleEnterPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSubmit();
    }
  };

  const handleSubmit = () => {
    if (!isSubmitDisabled) {
      const newRecipe: RecipeEditDto = {
        name: recipeName.trim(),
        ingredientIds: selectedIngredientIds.map((e) => e.split(/"(.*?)"/)[3]),
      };
      submit({ payload: newRecipe, id: recipe.id })
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

  const handleSubmitClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    handleSubmit();
  };

  const handleCancelClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    setRecipeName(recipe.name);
    setSelectedIngredientIds(recipe.ingredients.map((e) => JSON.stringify(e)));
    onClose();
  };

  return (
    <div>
      <Modal
        aria-label="Form for editing a recipe"
        open={open}
        onClose={onClose}
        closeAfterTransition
      >
        <Fade in={open}>
          <FormControl sx={modalStyle} onKeyDown={handleEnterPress}>
            <Typography variant="h5" alignSelf={"center"} paddingBottom={1.5}>
              Edit {capitalise(recipe.name)}
            </Typography>

            <NameInputField
              name={recipeName}
              isError={isRecipeNameError}
              helperText={recipeNameErrorMessage}
              setName={setRecipeName}
              setErrorMessage={setRecipeNameErrorMessage}
            />

            <IngredientSelect
              availableIngredients={availableIngredients}
              selectedIngredientIds={selectedIngredientIds}
              handleIngredientSelect={handleIngredientSelect}
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

export default RecipeEditModal;
