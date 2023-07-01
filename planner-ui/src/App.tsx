import RecipeBoard from "./components/recipes/RecipeBoard";
import { ThemeProvider } from "@mui/material/styles";
import theme from "./theme";
import { Box } from "@mui/material";
import Navbar from "./components/navbar/Navbar";

function App() {
  return (
    <ThemeProvider theme={theme}>
      <Box height={"100%"} bgcolor={"base.main"}>
        <Navbar />
        <Box
          height={"90%"}
          alignItems={"center"}
          justifyContent={"center"}
          display={"flex"}
        >
          <RecipeBoard />
        </Box>
      </Box>
    </ThemeProvider>
  );
}

export default App;
