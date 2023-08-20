import {
  createApi,
  fetchBaseQuery,
  FetchArgs,
  BaseQueryApi,
} from "@reduxjs/toolkit/query/react";
import { BASE_URL } from "../constants";

const baseQuery = fetchBaseQuery({
  baseUrl: BASE_URL,
  credentials: "include",
});

const refreshAccessToken = async (api: BaseQueryApi, extraOptions: {}) => {
  const refreshResult = await baseQuery(
    {
      credentials: "include",
      url: BASE_URL + "/auth/refresh-token",
      method: "post",
      headers: {
        refresh: "true",
      },
    },
    api,
    extraOptions
  );
  return refreshResult.error?.status === 403;
};

const baseQueryWithReauth = async (
  args: FetchArgs,
  api: BaseQueryApi,
  extraOptions: {}
) => {
  let result = await baseQuery(args, api, extraOptions);

  if (result?.error?.status === 403) {
    const isTokenRefreshed = await refreshAccessToken(api, extraOptions);
    if (isTokenRefreshed) {
      result = await baseQuery(args, api, extraOptions);
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
