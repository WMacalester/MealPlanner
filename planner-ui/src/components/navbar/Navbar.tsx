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
import { Stack } from "@mui/material";
import { FC } from "react";
import ThemeToggle from "../button/ThemeToggle";

interface NavbarProps {
  themeChecked: boolean;
  handleThemeToggle: () => void;
}

const AdminOnlyButtons = () => {
  return (
    <>
      <AddButton Modal={IngredientAddModal} label={"Add Ingredient"} />
      <AddButton Modal={RecipeAddModal} label={"Add Recipe"} />
    </>
  );
};

const Navbar: FC<NavbarProps> = ({ themeChecked, handleThemeToggle }) => {
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
            aria-label="Link to home page"
          >
            Meal Planner
          </Link>

          <ThemeToggle
            checked={themeChecked}
            handleThemeToggle={handleThemeToggle}
          />

          <Stack direction="row" spacing={1}>
            <SelectedRecipeResetButton />
            {canUserView && <AdminOnlyButtons />}
            <LogoutButton />
          </Stack>
        </Toolbar>
      </AppBar>
    </>
  );
};

export default Navbar;
