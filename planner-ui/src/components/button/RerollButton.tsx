import { Button } from "@mui/material";
import { FC } from "react";
import CasinoIcon from "@mui/icons-material/Casino";
import MainPageButtonStyle from "../styles/MainPageButtonStyle";

interface RerollButtonProps {
  handleClick: () => void;
}

const RerollButton: FC<RerollButtonProps> = ({ handleClick }) => {
  return (
    <Button
      aria-label={`Generate menu button`}
      onClick={handleClick}
      endIcon={<CasinoIcon />}
      variant="contained"
      sx={{ ...MainPageButtonStyle, border: 2, borderColor: "highlights.main" }}
    >
      Menu
    </Button>
  );
};

export default RerollButton;
