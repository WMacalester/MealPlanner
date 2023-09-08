import { FC } from "react";
import { IconButton } from "@mui/material";
import PlaylistRemoveIcon from "@mui/icons-material/PlaylistRemove";
import PlaylistAddIcon from "@mui/icons-material/PlaylistAdd";

interface SelectRecipeButtonProps {
  isSelected: boolean;
  name: string;
  handleClick: () => void;
}

const SelectRecipeButton: FC<SelectRecipeButtonProps> = ({
  isSelected,
  name,
  handleClick,
}) => {
  return (
    <div>
      <IconButton
        aria-label={`Select ${name} for menu button`}
        onClick={handleClick}
        sx={{
          color: isSelected ? "recipeCardText.selected" : "recipeCardText.main",
        }}
      >
        {isSelected ? <PlaylistRemoveIcon /> : <PlaylistAddIcon />}
      </IconButton>
    </div>
  );
};

export default SelectRecipeButton;
