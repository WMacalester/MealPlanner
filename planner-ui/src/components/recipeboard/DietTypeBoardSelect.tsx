import { Checkbox, FormControl, FormControlLabel } from "@mui/material";
import {
  DietType,
  getDietTypeDisplayEmoji,
  getDietTypeDisplayText,
} from "../../interfaces/DietType";

interface DietTypeSelectProps {
  selectedDietType: DietType | undefined;
  handleDietTypeSelect: (value: DietType | undefined) => void;
}

const values = Object.values(DietType);

const DietTypeBoardSelect: React.FC<DietTypeSelectProps> = ({
  selectedDietType,
  handleDietTypeSelect,
}) => {
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value as DietType;
    selectedDietType === value
      ? handleDietTypeSelect(undefined)
      : handleDietTypeSelect(value);
  };

  return (
    <FormControl sx={{ display: "flex", flexDirection: "row" }}>
      {values.map((e) => {
        return (
          <FormControlLabel
            key={e}
            control={
              <Checkbox
                checked={selectedDietType === e}
                onChange={handleChange}
                value={e}
                sx={{
                  "&.Mui-checked": {
                    color: "base.main",
                  },
                }}
              />
            }
            label={getDietTypeDisplayEmoji(e) + " " + getDietTypeDisplayText(e)}
          />
        );
      })}
    </FormControl>
  );
};

export default DietTypeBoardSelect;
