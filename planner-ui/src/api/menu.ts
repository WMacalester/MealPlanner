import { Recipe } from "../interfaces/RecipeInterface";
import { apiSlice } from "./api";

const BASE_URL = "/menu";

export const menuApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getRandomMenu: builder.query<Recipe[], number>({
      query: (number) => ({
        url: BASE_URL,
        params: { number },
      }),
      providesTags: ["Menu"],
    }),
  }),
});

export const { useLazyGetRandomMenuQuery } = menuApi;
