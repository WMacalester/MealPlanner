import { FC, useState } from "react";
import Modal from "@mui/material/Modal";
import Fade from "@mui/material/Fade";
import {
  FormControl,
  SelectChangeEvent,
  Stack,
  Typography,
} from "@mui/material";
import { useEditRecipeMutation } from "../../api/recipes";
import { Recipe, RecipeEditDto } from "../../interfaces/RecipeInterface";
import { useGetAllIngredientsQuery } from "../../api/ingredients";
import { IngredientSelect } from "./IngredientSelect";
import { MutationModalProps } from "../../interfaces/AddModalProps";
import NameInputField from "../NameInputField";
import SubmitAndCancelButtons from "../button/SubmitAndCancelButtons";
import { capitalise } from "../../utils";
import { DietType } from "../../interfaces/DietType";
import DietTypeSelect from "./DietTypeSelect";
import ModalStyle from "../styles/ModalStyle";

const recipeNameAlreadyExistsRegex = new RegExp(/^Recipe.*already exists/);
const recipeNameAlreadyExistsMessage = "A recipe with that name already exists";

const RecipeEditModal: FC<MutationModalProps<Recipe>> = ({
  open,
  handleClose,
  data: recipe,
}) => {
  const [recipeName, setRecipeName] = useState(recipe.name);
  const [recipeDietType, setRecipeDietType] = useState<DietType>(
    recipe.dietType
  );
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

  const handleDietTypeSelect = (value: DietType) => {
    setRecipeDietType(value);
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
    setRecipeName(recipe.name);
    setSelectedIngredientIds(recipe.ingredients.map((e) => JSON.stringify(e)));
    setRecipeNameErrorMessage("");
    setFormErrorMessage("");
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSubmit();
    } else if (e.key === "Escape") {
      onClose();
    }
  };

  const handleSubmit = () => {
    if (!isSubmitDisabled) {
      const newRecipe: RecipeEditDto = {
        name: recipeName.trim(),
        dietType: recipeDietType,
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
          <FormControl sx={ModalStyle} onKeyDown={handleKeyDown}>
            <Typography variant="h5" alignSelf={"center"} paddingBottom={1.5}>
              Edit {capitalise(recipe.name)}
            </Typography>

            <Stack spacing={2}>
              <NameInputField
                name={recipeName}
                isError={isRecipeNameError}
                helperText={recipeNameErrorMessage}
                setName={setRecipeName}
                setErrorMessage={setRecipeNameErrorMessage}
              />

              <DietTypeSelect
                selectedDietType={recipeDietType}
                handleDietTypeSelect={handleDietTypeSelect}
              />
              <IngredientSelect
                availableIngredients={availableIngredients}
                selectedIngredientIds={selectedIngredientIds}
                handleIngredientSelect={handleIngredientSelect}
              />
            </Stack>

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
