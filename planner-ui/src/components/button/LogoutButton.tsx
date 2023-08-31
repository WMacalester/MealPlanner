import { Button } from "@mui/material";
import { useAppDispatch } from "../../hooks/redux-hooks";
import { logOut } from "../../api/authSlice";
import LogoutIcon from "@mui/icons-material/Logout";

const LogoutButton = () => {
  const dispatch = useAppDispatch();

  const handleLogout = () => {
    dispatch(logOut());
  };

  return (
    <Button
      aria-label={`logout button`}
      onClick={handleLogout}
      variant="outlined"
      sx={{
        color: "highlights.main",
        marginX: "1rem",
      }}
      endIcon={<LogoutIcon />}
    >
      Log out
    </Button>
  );
};

export default LogoutButton;
