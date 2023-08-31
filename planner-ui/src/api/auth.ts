import { AuthState, UserLogin } from "../interfaces/AuthInterface";
import { apiSlice } from "./api";

const BASE_URL = "/auth";

export const authApiSlice = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    registerUser: builder.mutation<AuthState, UserLogin>({
      query: (payload: UserLogin) => ({
        url: BASE_URL + "/register",
        method: "post",
        body: payload,
      }),
    }),
    authenticateUser: builder.mutation<AuthState, UserLogin>({
      query: (payload: UserLogin) => ({
        url: BASE_URL + "/authenticate",
        method: "post",
        body: payload,
      }),
    }),
    logout: builder.mutation<void, void>({
      query: () => ({
        url: BASE_URL + "/logout",
        method: "post",
      }),
    }),
  }),
});

export const {
  useRegisterUserMutation,
  useAuthenticateUserMutation,
  useLogoutMutation,
} = authApiSlice;
