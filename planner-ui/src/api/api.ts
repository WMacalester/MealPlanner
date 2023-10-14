import {
  createApi,
  fetchBaseQuery,
  FetchArgs,
  BaseQueryApi,
} from "@reduxjs/toolkit/query/react";
import { logOut } from "./authSlice";

const baseQuery = fetchBaseQuery({
  baseUrl: process.env.REACT_APP_BASE_URL,
  credentials: "include",
});

const refreshTokenArgs: FetchArgs = {
  credentials: "include",
  url: process.env.REACT_APP_BASE_URL + "/auth/refresh-token",
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
