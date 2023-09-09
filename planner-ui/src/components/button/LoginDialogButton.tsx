import { Button } from "@mui/material";
import { FC } from "react";

interface LoginDialogButtonProps {
  handleClick: (e: React.MouseEvent) => void;
  name: string;
}

const LoginDialogButton: FC<LoginDialogButtonProps> = ({
  handleClick,
  name,
}) => {
  return (
    <Button
      onClick={handleClick}
      sx={{
        backgroundColor: "primary.main",
        color: "highlights.main",
        ":hover": {
          color: "highlights.main",
          backgroundColor: "secondary.main",
          borderColor: "secondary.main",
        },
      }}
      aria-label="Log in button"
    >
      {name}
    </Button>
  );
};

export default LoginDialogButton;
