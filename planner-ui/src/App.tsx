import RecipeBoard from "./components/recipes/RecipeBoard";
import { ThemeProvider } from "@mui/material/styles";
import theme from "./theme";
import { Box, Grid } from "@mui/material";
import Navbar from "./components/navbar/Navbar";
import Menuboard from "./components/menu/Menuboard";
import { NAVBAR_HEIGHT } from "./constants";

function App() {
  return (
    <ThemeProvider theme={theme}>
      <Navbar />
      <Box height={"100%"} bgcolor={"base.main"}>
        <Grid
          container
          direction={"row"}
          height={"100%"}
          justifyContent={"space-between"}
          paddingX={"5%"}
          paddingTop={NAVBAR_HEIGHT}
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
    </ThemeProvider>
  );
}

export default App;
