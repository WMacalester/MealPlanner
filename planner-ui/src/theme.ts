import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    contrastThreshold: 4.5,
    primary: {
      light: "#0066aa",
      main: "#0044aa",
    },
    secondary: {
      main: "#aa0099",
      contrastText: "#ffcc00",
    },
  },
});

export default theme;
