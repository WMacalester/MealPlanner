import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import SelectedRecipeResetButton from "../button/SelectedRecipeResetButton";
import useIsUserPermittedHook from "../../hooks/useIsUserPermittedHook";
import { UserRole } from "../../interfaces/UserRole";
import LogoutButton from "../button/LogoutButton";
import Link from "@mui/material/Link";
import { Stack } from "@mui/material";
import { FC } from "react";
import ThemeToggle from "../button/ThemeToggle";
import AdminDrawer from "./AdminDrawer";

interface NavbarProps {
  themeChecked: boolean;
  handleThemeToggle: () => void;
}

const Navbar: FC<NavbarProps> = ({ themeChecked, handleThemeToggle }) => {
  const isUserAdmin = useIsUserPermittedHook([UserRole.ROLE_ADMIN]);

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

          {isUserAdmin && <AdminDrawer />}
          <Stack direction="row" spacing={1} alignItems={"center"}>
            <SelectedRecipeResetButton />
            <ThemeToggle
              checked={themeChecked}
              handleThemeToggle={handleThemeToggle}
            />
            <LogoutButton />
          </Stack>
        </Toolbar>
      </AppBar>
    </>
  );
};

export default Navbar;
