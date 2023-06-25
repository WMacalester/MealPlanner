import Recipes from "./components/recipes/RecipeBoard";
import { ThemeProvider } from "@mui/material/styles";
import theme from "./theme";

function App() {
  return (
    <ThemeProvider theme={theme}>
      <div className="App">
        <header className="App-header"></header>
        <Recipes />
      </div>
    </ThemeProvider>
  );
}

export default App;
