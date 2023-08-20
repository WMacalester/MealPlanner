import { TokenInterface, User, UserLogin } from "../interfaces/AuthInterface";
import { apiSlice } from "./api";

const BASE_URL = "/auth";

export const authApiSlice = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    registerUser: builder.mutation<TokenInterface, UserLogin>({
      query: (payload: UserLogin) => ({
        url: BASE_URL + "/register",
        method: "post",
        body: payload,
      }),
    }),
    authenticateUser: builder.mutation<User, UserLogin>({
      query: (payload: UserLogin) => ({
        url: BASE_URL + "/authenticate",
        method: "post",
        body: payload,
      }),
    }),
  }),
});

export const { useRegisterUserMutation, useAuthenticateUserMutation } =
  authApiSlice;
