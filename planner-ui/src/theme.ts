import { createTheme } from "@mui/material/styles";

const highlightsMain = "#ffffff";

const themeLight = createTheme({
  palette: {
    contrastThreshold: 4.5,
    primary: {
      main: "#508CA4",
    },
    secondary: {
      main: "#064663",
    },
    tertiary: {
      main: "#ff6150",
    },
    highlights: {
      main: highlightsMain,
    },
    diet: {
      MEAT: "#8E3B46",
      VEGETARIAN: "#0A8754",
      VEGAN: "#004F2D",
    },
    base: {
      main: "#041C32",
    },
  },
  typography: {
    button: {
      fontSize: "1.3rem",
      textTransform: "none",
    },
  },
});

const themeDark = createTheme({
  palette: {
    contrastThreshold: 4.5,
    primary: {
      main: "#508CA4",
    },
    secondary: {
      main: "#91AEC1",
    },
    tertiary: {
      main: "#ff6150",
    },
    highlights: {
      main: highlightsMain,
    },
    diet: {
      MEAT: "#8E3B46",
      VEGETARIAN: "#0A8754",
      VEGAN: "#004F2D",
    },
    base: {
      main: "#ffffff",
    },
  },
  typography: {
    button: {
      fontSize: "1.3rem",
      textTransform: "none",
    },
  },
});

declare module "@mui/material/styles" {
  interface Palette {
    highlights: { main: string };
    base: { main: string };
    tertiary: { main: string };
    diet: PaletteOptions["primary"];
  }

  interface PaletteOptions {
    highlights: { main: string };
    base?: { main: string };
    tertiary?: { main: string };
    diet?: { MEAT: string; VEGETARIAN: string; VEGAN: string };
  }
}

export { themeLight, themeDark };
