import {
  createApi,
  fetchBaseQuery,
  FetchArgs,
  BaseQueryApi,
} from "@reduxjs/toolkit/query/react";
import { BASE_URL } from "../constants";
import { logOut } from "./authSlice";

const baseQuery = fetchBaseQuery({
  baseUrl: BASE_URL,
  credentials: "include",
});

const refreshTokenArgs: FetchArgs = {
  credentials: "include",
  url: BASE_URL + "/auth/refresh-token",
  method: "post",
  headers: {
    refresh: "true",
  },
};

const baseQueryWithReauth = async (
  args: FetchArgs,
  api: BaseQueryApi,
  extraOptions: {}
) => {
  let result = await baseQuery(args, api, extraOptions);

  if (result?.error?.status === 403) {
    const refreshResult = await baseQuery(refreshTokenArgs, api, extraOptions);
    if (refreshResult.data) {
      result = await baseQuery(args, api, extraOptions);
    } else {
      api.dispatch(logOut());
    }
  }

  return result;
};

export const apiSlice = createApi({
  reducerPath: "api",
  baseQuery: baseQueryWithReauth,
  tagTypes: ["Recipe", "Ingredient", "Menu"],
  endpoints: () => ({}),
});
