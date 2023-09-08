import { Button } from "@mui/material";
import { FC } from "react";
import CasinoIcon from "@mui/icons-material/Casino";

interface RerollButtonProps {
  handleClick: () => void;
}

const RerollButton: FC<RerollButtonProps> = ({ handleClick }) => {
  return (
    <div>
      <Button
        aria-label={`Generate menu button`}
        onClick={handleClick}
        endIcon={<CasinoIcon />}
        variant="outlined"
        sx={{
          borderColor: "black",
          color: "black",
          marginX: "1rem",
          ":hover": {
            color: "highlights.main",
            backgroundColor: "base.main",
            borderColor: "secondary.main",
          },
        }}
      >
        Menu
      </Button>
    </div>
  );
};

export default RerollButton;
