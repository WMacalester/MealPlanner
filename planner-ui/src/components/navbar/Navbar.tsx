import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import IngredientAddModal from "../ingredients/IngredientAddModal";
import RecipeAddModal from "../recipes/RecipeAddModal";
import AddButton from "../button/AddButton";
import SelectedRecipeResetButton from "../button/SelectedRecipeResetButton";
import useIsUserPermittedHook from "../../hooks/useIsUserPermittedHook";
import { UserRole } from "../../interfaces/UserRole";
import LogoutButton from "../button/LogoutButton";
import Link from "@mui/material/Link";
import { Box } from "@mui/material";

export default function Navbar() {
  const canUserView = useIsUserPermittedHook([UserRole.ROLE_ADMIN]);
  return (
    <>
      <AppBar position="static" sx={{ height: "4rem" }}>
        <Toolbar
          sx={{
            display: "flex",
            justifyContent: "space-between",
          }}
        >
          <Link
            variant="h4"
            href="/"
            underline="none"
            sx={{
              color: "highlights.main",
            }}
          >
            Meal Planner
          </Link>

          <Box display={"flex"}>
            <SelectedRecipeResetButton />
            {canUserView && (
              <>
                <AddButton
                  Modal={IngredientAddModal}
                  label={"Add Ingredient"}
                />
                <AddButton Modal={RecipeAddModal} label={"Add Recipe"} />
              </>
            )}
            <LogoutButton />
          </Box>
        </Toolbar>
      </AppBar>
    </>
  );
}
