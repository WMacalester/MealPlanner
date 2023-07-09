import { Button } from "@mui/material";
import { FC } from "react";

interface SubmitButtonProps {
  isDisabled: boolean;
  handleClick: (e: React.MouseEvent<HTMLButtonElement>) => void;
}

const SubmitButton: FC<SubmitButtonProps> = ({ isDisabled, handleClick }) => {
  return (
    <>
      <Button
        aria-label={`Submit button`}
        disabled={isDisabled}
        onClick={handleClick}
        variant="outlined"
        sx={{
          ":hover": {
            color: "highlights.main",
            backgroundColor: "primary.main",
          },
        }}
      >
        Submit
      </Button>
    </>
  );
};

export default SubmitButton;
