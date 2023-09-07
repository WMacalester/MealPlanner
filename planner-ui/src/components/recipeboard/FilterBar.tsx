import { Stack, TextField } from "@mui/material";
import DietTypeBoardSelect from "./DietTypeBoardSelect";
import { FC } from "react";
import { DietType } from "../../interfaces/DietType";

interface FilterBarProps {
  onNameFilterChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  nameFilterError: boolean;
  selectedDietType: DietType | undefined;
  handleDietTypeSelect: (value: DietType | undefined) => void;
}

const FilterBar: FC<FilterBarProps> = ({
  onNameFilterChange: onFilterChange,
  nameFilterError: filterValueError,
  selectedDietType,
  handleDietTypeSelect,
}) => {
  return (
    <Stack
      sx={{ display: "flex", spacing: 2 }}
      justifyContent={"center"}
      alignItems={"center"}
    >
      <TextField
        type="search"
        onChange={onFilterChange}
        label="Search by Name"
        error={filterValueError}
        helperText={filterValueError ? "Invalid character in search" : ""}
        sx={{ width: "80%" }}
      />
      <DietTypeBoardSelect
        selectedDietType={selectedDietType}
        handleDietTypeSelect={handleDietTypeSelect}
      />
    </Stack>
  );
};

export default FilterBar;
