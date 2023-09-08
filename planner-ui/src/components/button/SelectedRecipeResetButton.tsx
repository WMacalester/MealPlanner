import { FC } from "react";
import { Button } from "@mui/material";
import { useAppDispatch } from "../../hooks/redux-hooks";
import { resetSelectedRecipeIds } from "../../selectedRecipesReducer";
import RestartAltIcon from "@mui/icons-material/RestartAlt";
import MainPageButtonStyle from "../styles/MainPageButtonStyle";

const SelectedRecipeResetButton: FC = () => {
  const dispatch = useAppDispatch();

  const handleClick = () => {
    dispatch(resetSelectedRecipeIds());
  };

  return (
    <Button
      aria-label={`Reset selected recipes button`}
      onClick={handleClick}
      sx={MainPageButtonStyle}
      endIcon={<RestartAltIcon />}
    >
      {"Reset Selection"}
    </Button>
  );
};

export default SelectedRecipeResetButton;
