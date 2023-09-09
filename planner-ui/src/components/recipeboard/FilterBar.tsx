import { Stack } from "@mui/material";
import DietTypeBoardSelect from "./DietTypeBoardSelect";
import { FC } from "react";
import { DietType } from "../../interfaces/DietType";
import StyledTextField from "../styles/StyledTextField";

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
      sx={{ display: "flex", spacing: 2, color: "highlights.main" }}
      justifyContent={"center"}
      alignItems={"center"}
    >
      <StyledTextField
        aria-label="Search by recipe or ingredient name"
        type="search"
        onChange={onFilterChange}
        label="Search by Name"
        variant="outlined"
        width="75%"
        error={filterValueError}
        helperText={filterValueError ? "Invalid character in search" : ""}
      />
      <DietTypeBoardSelect
        selectedDietType={selectedDietType}
        handleDietTypeSelect={handleDietTypeSelect}
      />
    </Stack>
  );
};

export default FilterBar;
