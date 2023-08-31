import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { RootState } from "../store";
import { AuthState } from "../interfaces/AuthInterface";

export const authSlice = createSlice({
  name: "auth",
  initialState: {
    username: null,
    userRole: localStorage.getItem("role"),
  } as AuthState,
  reducers: {
    setCredentials: (state, action: PayloadAction<AuthState>) => {
      const { username, userRole } = action.payload;

      if (userRole) {
        localStorage.setItem("role", userRole);
      } else {
        localStorage.removeItem("role");
      }

      return { ...state, username, userRole };
    },
    logOut: (state) => {
      state.username = null;
      state.userRole = null;
      localStorage.removeItem("role");
    },
  },
});

export const { setCredentials, logOut } = authSlice.actions;

export default authSlice.reducer;

export const selectCurrentUser = (state: RootState) => {
  return state.auth.username;
};

export const selectCurrentRole = (state: RootState) => {
  return state.auth.userRole;
};
