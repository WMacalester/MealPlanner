import { FC } from "react";
import { Button } from "@mui/material";
import { useAppDispatch } from "../../hooks/redux-hooks";
import { resetSelectedRecipeIds } from "../../selectedRecipesReducer";
import RestartAltIcon from "@mui/icons-material/RestartAlt";

const SelectedRecipeResetButton: FC = () => {
  const dispatch = useAppDispatch();

  const handleClick = () => {
    dispatch(resetSelectedRecipeIds());
  };

  return (
    <div>
      <Button
        aria-label={`Reset selected recipes button`}
        onClick={handleClick}
        sx={{
          color: "highlights.main",
        }}
        endIcon={<RestartAltIcon />}
      >
        {"Reset Selection"}
      </Button>
    </div>
  );
};

export default SelectedRecipeResetButton;
