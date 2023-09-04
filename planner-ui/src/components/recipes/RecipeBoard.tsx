import { FC, useState } from "react";
import { useGetAllRecipesQuery } from "../../api/recipes";
import RecipeCard from "./recipe-card/RecipeCard";
import { Box, Container, Grid, TextField } from "@mui/material";
import { BOARD_HEIGHT } from "../../constants";
import { isNameAlpha } from "../../utils";
import useDebounce from "../../hooks/useDebounce";

const RecipeBoard: FC = () => {
  const [filterValue, setFilterValue] = useState<string | undefined>(undefined);
  const [filterValueError, setFilterValueError] = useState(false);
  const debouncedSearchValue = useDebounce(filterValue);
  const { data: recipes } = useGetAllRecipesQuery({
    recipeName: debouncedSearchValue,
  });

  const onFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
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
      sx={{
        backgroundColor: "tertiary.main",
        overflowY: "auto",
        padding: "1rem",
        borderRadius: "1rem",
        width: "80%",
        height: BOARD_HEIGHT,
        alignItems: "start",
      }}
    >
      <Container>
        <TextField
          type="search"
          onChange={onFilterChange}
          label="Search by Name"
          error={filterValueError}
          helperText={filterValueError ? "Invalid character in search" : ""}
        />
      </Container>
      <Grid
        container
        rowSpacing={1}
        columnSpacing={{ xs: 1 }}
        sx={{ justifyContent: "space-evenly" }}
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
