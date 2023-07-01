import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import AddButton from "../AddButton";
import IngredientAddModal from "../ingredients/IngredientAddModal";
import RecipeAddModal from "../recipes/RecipeAddModal";

export default function Navbar() {
  return (
    <Box>
      <AppBar position="static" sx={{ height: "5%" }}>
        <Toolbar>
          <Typography
            variant="h4"
            sx={{ flexGrow: 1, color: "highlights.main" }}
          >
            Meal Planner
          </Typography>

          <>
            <AddButton Modal={IngredientAddModal} label={"Add Ingredient"} />
            <AddButton Modal={RecipeAddModal} label={"Add Recipe"} />
          </>
        </Toolbar>
      </AppBar>
    </Box>
  );
}
