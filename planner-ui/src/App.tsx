import { ThemeProvider } from "@mui/material/styles";
import theme from "./theme";
import Navbar from "./components/navbar/Navbar";
import { Route, Routes } from "react-router-dom";
import LoginPage from "./routes/LoginPage";
import RecipeBoardPage from "./routes/RecipeBoardPage";
import RequireAuth from "./components/auth/RequireAuth";
import { UserRole } from "./interfaces/UserRole";
import ForbiddenPage from "./routes/ForbiddenPage";
import MissingPage from "./routes/MissingPage";
import Layout from "./Layout";

function App() {
  return (
    <ThemeProvider theme={theme}>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/forbidden" element={<ForbiddenPage />} />

          <Route
            element={
              <>
                <Navbar />
                <RequireAuth
                  allowedRoles={[UserRole.ROLE_ADMIN, UserRole.ROLE_USER]}
                />
              </>
            }
          >
            <Route path="/" element={<RecipeBoardPage />} />
          </Route>

          <Route path="*" element={<MissingPage />} />
        </Route>
      </Routes>
    </ThemeProvider>
  );
}

export default App;
