import { Box, Grid } from "@mui/material";
import { FC } from "react";
import RecipeBoard from "../components/recipes/RecipeBoard";
import Menuboard from "../components/menu/Menuboard";

const RecipeBoardPage: FC = () => {
  return (
    <Box height={"100%"} bgcolor={"base.main"}>
      <Grid
        container
        direction={"row"}
        height={"100%"}
        justifyContent={"space-between"}
        paddingX={"5%"}
      >
        <Grid
          item
          xs
          alignItems={"center"}
          justifyContent={"center"}
          display="flex"
        >
          <RecipeBoard />
        </Grid>
        <Grid
          item
          alignItems={"center"}
          justifyContent={"center"}
          display="flex"
        >
          <Menuboard />
        </Grid>
      </Grid>
    </Box>
  );
};

export default RecipeBoardPage;
