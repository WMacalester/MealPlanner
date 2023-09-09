import { FC, useState } from "react";
import { useGetAllRecipesQuery } from "../../api/recipes";
import RecipeCard from "../recipes/recipe-card/RecipeCard";
import { Box, Divider, Grid } from "@mui/material";
import { BOARD_HEIGHT } from "../../constants";
import { isNameAlpha } from "../../utils";
import useDebounce from "../../hooks/useDebounce";
import { DietType } from "../../interfaces/DietType";
import FilterBar from "./FilterBar";

const RecipeBoard: FC = () => {
  const [filterValue, setFilterValue] = useState<string | undefined>(undefined);
  const [nameFilterError, setFilterValueError] = useState(false);
  const debouncedSearchValue = useDebounce(filterValue);
  const [selectedDietType, setSelectedDietType] = useState<
    DietType | undefined
  >(undefined);
  const { data: recipes } = useGetAllRecipesQuery({
    recipeName: debouncedSearchValue,
    dietType: selectedDietType,
  });

  const handleDietTypeSelect = (value: DietType | undefined) => {
    setSelectedDietType(value);
  };

  const onNameFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    if (!value) {
      setFilterValueError(false);
      setFilterValue(undefined);
    } else if (isNameAlpha(value.trim())) {
      setFilterValueError(false);
      setFilterValue(value);
    } else setFilterValueError(true);
  };

  return (
    <Box
      border={4}
      sx={{
        backgroundColor: "primary.main",
        overflowY: "auto",
        padding: "1rem",
        borderRadius: "1rem",
        borderColor: "secondary.main",
        borderWidth: "1rem",
        width: "80%",
        height: BOARD_HEIGHT,
        alignItems: "start",
        boxShadow: 20,
      }}
    >
      <FilterBar
        onNameFilterChange={onNameFilterChange}
        nameFilterError={nameFilterError}
        selectedDietType={selectedDietType}
        handleDietTypeSelect={handleDietTypeSelect}
      />

      <Divider variant="middle" sx={{ marginY: "0.5rem" }} />

      <Grid
        container
        rowSpacing={1}
        columnSpacing={{ xs: 1 }}
        sx={{ justifyContent: "space-evenly" }}
        aria-label="Board of recipes"
      >
        {recipes?.map((recipe) => (
          <Grid item key={recipe.id}>
            <RecipeCard {...recipe} />
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default RecipeBoard;
