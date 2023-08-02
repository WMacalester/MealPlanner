import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import IngredientAddModal from "../ingredients/IngredientAddModal";
import RecipeAddModal from "../recipes/RecipeAddModal";
import AddButton from "../button/AddButton";
import { NAVBAR_HEIGHT } from "../../constants";
import SelectedRecipeResetButton from "../button/SelectedRecipeResetButton";

export default function Navbar() {
  return (
    <Box>
      <AppBar position="absolute" sx={{ height: NAVBAR_HEIGHT }}>
        <Toolbar>
          <Typography
            variant="h4"
            sx={{ flexGrow: 1, color: "highlights.main" }}
          >
            Meal Planner
          </Typography>

          <>
            <SelectedRecipeResetButton />
            <AddButton Modal={IngredientAddModal} label={"Add Ingredient"} />
            <AddButton Modal={RecipeAddModal} label={"Add Recipe"} />
          </>
        </Toolbar>
      </AppBar>
    </Box>
  );
}
