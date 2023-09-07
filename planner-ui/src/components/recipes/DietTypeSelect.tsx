import {
  FormControl,
  FormControlLabel,
  FormLabel,
  Radio,
  RadioGroup,
} from "@mui/material";
import {
  DietType,
  getDietTypeDisplayEmoji,
  getDietTypeDisplayText,
} from "../../interfaces/DietType";

interface DietTypeSelectProps {
  selectedDietType: DietType | undefined;
  handleDietTypeSelect: (value: DietType) => void;
}

const values = Object.values(DietType);

const DietTypeSelect: React.FC<DietTypeSelectProps> = ({
  selectedDietType,
  handleDietTypeSelect,
}) => {
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    handleDietTypeSelect(event.target.value as DietType);
  };

  return (
    <FormControl>
      <FormLabel required>Diet Type</FormLabel>
      <RadioGroup
        aria-label="Select Diet Type"
        name="Diet Type"
        value={selectedDietType}
        onChange={handleChange}
      >
        {values.map((e) => {
          return (
            <FormControlLabel
              key={e}
              value={e}
              control={<Radio />}
              label={
                getDietTypeDisplayEmoji(e) + " " + getDietTypeDisplayText(e)
              }
            />
          );
        })}
      </RadioGroup>
    </FormControl>
  );
};

export default DietTypeSelect;
