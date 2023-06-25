import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    contrastThreshold: 4.5,
    primary: {
      light: "#0e79b2",
      main: "#0E79B2",
    },
    secondary: {
      main: "#BF1363",
      contrastText: "#ffcc00",
    },
    tertiary: {
      main: "#F39237",
    },
    highlights: {
      main: "#FBFEF9",
      contrastText: "#fff",
    },
    base: {
      main: "#191923",
    },
  },

  typography: {
    button: {
      fontSize: "1.3rem",
    },
  },
});

export default theme;

declare module "@mui/material/styles" {
  interface Palette {
    highlights: PaletteOptions["primary"];
    base: PaletteOptions["primary"];
    tertiary: PaletteOptions["primary"];
  }

  interface PaletteOptions {
    highlights?: PaletteOptions["primary"];
    base?: PaletteOptions["primary"];
    tertiary?: PaletteOptions["primary"];
  }
}
