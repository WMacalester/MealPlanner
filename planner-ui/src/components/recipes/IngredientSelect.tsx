import {
  Checkbox,
  FormControl,
  InputLabel,
  ListItemText,
  MenuItem,
  Select,
  SelectChangeEvent,
} from "@mui/material";
import { Ingredient } from "../../interfaces/IngredientInterface";
import { FC } from "react";

interface IngredientSelectProps {
  availableIngredients: Ingredient[] | undefined;
  selectedIngredientIds: string[];
  handleIngredientSelect: (event: SelectChangeEvent<string[]>) => void;
}

export const IngredientSelect: FC<IngredientSelectProps> = ({
  availableIngredients,
  selectedIngredientIds,
  handleIngredientSelect,
}) => {
  return (
    <FormControl>
      <InputLabel>Ingredients</InputLabel>
      <Select
        multiple
        id="Ingredient name"
        label="Ingredients"
        aria-label="Select ingredients"
        value={selectedIngredientIds}
        onChange={handleIngredientSelect}
        renderValue={(selected) =>
          selected.map((e) => e.split(/"(.*?)"/)[7]).join(", ")
        }
        sx={{
          ".MuiInputLabel-root": {
            fontSize: "1.25rem",
            color: "primary.main",
          },
        }}
      >
        {availableIngredients?.map((ingredient: Ingredient) => {
          const stringified = JSON.stringify(ingredient);
          return (
            <MenuItem key={ingredient.id} value={stringified}>
              <Checkbox checked={selectedIngredientIds.includes(stringified)} />
              <ListItemText primary={ingredient.name} />
            </MenuItem>
          );
        })}
      </Select>
    </FormControl>
  );
};
