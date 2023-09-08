import { FormControlLabel, FormGroup, Switch, styled } from "@mui/material";
import { FC } from "react";

interface ThemeToggleProps {
  checked: boolean;
  handleThemeToggle: () => void;
}

const StyledSwitch = styled(Switch)(({ theme }) => ({
  "& .MuiSwitch-track": {
    backgroundColor: theme.palette.base.main,
    color: theme.palette.base.main,
  },
  "& .MuiSwitch-switchBase": {
    color: theme.palette.highlights.main,
    "&.Mui-checked": {
      color: theme.palette.highlights.main,
    },
    "&.Mui-checked + .MuiSwitch-track": {
      backgroundColor: theme.palette.base.main,
    },
  },
}));

const ThemeToggle: FC<ThemeToggleProps> = ({ checked, handleThemeToggle }) => {
  return (
    <FormGroup>
      <FormControlLabel
        control={
          <StyledSwitch
            checked={checked}
            onChange={handleThemeToggle}
            inputProps={{ "aria-label": "theme toggle" }}
          />
        }
        label="Toggle theme"
        sx={{
          color: "highlights.main",
        }}
      />
    </FormGroup>
  );
};
export default ThemeToggle;
