import { FC, useState } from "react";
import Modal from "@mui/material/Modal";
import Fade from "@mui/material/Fade";
import {
  FormControl,
  SelectChangeEvent,
  Stack,
  Typography,
} from "@mui/material";
import { useCreateNewRecipeMutation } from "../../api/recipes";
import { RecipeCreateDto } from "../../interfaces/RecipeInterface";
import { useGetAllIngredientsQuery } from "../../api/ingredients";
import { IngredientSelect } from "./IngredientSelect";
import { AddModalProps } from "../../interfaces/AddModalProps";
import NameInputField from "../NameInputField";
import SubmitAndCancelButtons from "../button/SubmitAndCancelButtons";
import { DietType } from "../../interfaces/DietType";
import DietTypeSelect from "./DietTypeSelect";
import ModalStyle from "../styles/ModalStyle";

const recipeNameAlreadyExistsRegex = new RegExp(/^Recipe.*already exists/);

const recipeNameAlreadyExistsMessage = "A recipe with that name already exists";

const RecipeAddModal: FC<AddModalProps> = ({ open, handleClose }) => {
  const [recipeName, setRecipeName] = useState("");
  const [recipeNameErrorMessage, setRecipeNameErrorMessage] = useState("");
  const [formErrorMessage, setFormErrorMessage] = useState("");
  const { data: availableIngredients } = useGetAllIngredientsQuery();
  const [selectedDietType, setSelectedDietType] = useState<
    DietType | undefined
  >(undefined);
  const [selectedIngredientIds, setSelectedIngredientIds] = useState<string[]>(
    []
  );
  const [submit] = useCreateNewRecipeMutation();

  const handleDietTypeSelect = (value: DietType) => {
    setSelectedDietType(value);
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

  const handleEnterPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSubmit();
    }
  };

  const handleSubmit = () => {
    if (!isSubmitDisabled && selectedDietType) {
      const newRecipe: RecipeCreateDto = {
        name: recipeName.trim(),
        dietType: selectedDietType,
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

  const handleSubmitClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    handleSubmit();
  };
  const handleCancelClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    onClose();
  };

  const isRecipeNameError: boolean = recipeNameErrorMessage.length !== 0;
  const isSubmitDisabled: boolean =
    recipeName.trim().length === 0 ||
    isRecipeNameError ||
    selectedDietType === undefined;

  return (
    <div>
      <Modal
        aria-label="Form for creating a recipe"
        open={open}
        onClose={onClose}
        closeAfterTransition
      >
        <Fade in={open}>
          <FormControl sx={ModalStyle} onKeyDown={handleEnterPress}>
            <Typography variant="h5" alignSelf={"center"} paddingBottom={1.5}>
              Add a New Recipe
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
                selectedDietType={undefined}
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

export default RecipeAddModal;
