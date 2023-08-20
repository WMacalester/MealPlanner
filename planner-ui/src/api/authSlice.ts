import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { RootState } from "../store";
import {
  AuthPayload,
  AuthState,
  TokenInterface,
} from "../interfaces/AuthInterface";

export const authSlice = createSlice({
  name: "auth",
  initialState: { user: null, accessToken: null } as AuthState,
  reducers: {
    setCredentials: (state, action: PayloadAction<AuthPayload>) => {
      const { user, accessToken, refreshToken } = action.payload;
      state.user = user;
      state.accessToken = accessToken;
      state.refreshToken = refreshToken;
    },
    logOut: (state) => {
      state.user = null;
      state.accessToken = null;
      state.refreshToken = null;
    },
    resetAccessToken: (state) => {
      state.accessToken = null;
    },
    setTokens: (state, action: PayloadAction<TokenInterface>) => {
      const { accessToken, refreshToken } = action.payload;
      state.accessToken = accessToken;
      state.refreshToken = refreshToken;
    },
  },
});

export const { setCredentials, logOut, resetAccessToken, setTokens } =
  authSlice.actions;

export default authSlice.reducer;

export const selectCurrentUser = (state: RootState) => {
  return state.auth.user;
};

export const selectCurrentAccessToken = (state: RootState) => {
  return state.auth.accessToken;
};
