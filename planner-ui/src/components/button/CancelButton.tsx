import { Button } from "@mui/material";
import { FC } from "react";

interface SubmitButtonProps {
  handleClick: (e: React.MouseEvent<HTMLButtonElement>) => void;
}

const CancelButton: FC<SubmitButtonProps> = ({ handleClick }) => {
  return (
    <Button
      aria-label={`Cancel button`}
      onClick={handleClick}
      variant="outlined"
      sx={{
        borderColor: "red",
        color: "red",
        ":hover": {
          color: "highlights.main",
          backgroundColor: "red",
          borderColor: "red",
        },
      }}
    >
      Cancel
    </Button>
  );
};

export default CancelButton;
