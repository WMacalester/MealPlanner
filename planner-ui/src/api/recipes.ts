import { Recipe } from "../interfaces/RecipeInterface";
import { apiSlice } from "./api";

export const recipesApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getAllRecipes: builder.query<Recipe[], void>({
      query: () => ({
        url: "/recipes/",
      }),
      providesTags: ["Recipe"],
    }),
  }),
});

export const { useGetAllRecipesQuery } = recipesApi;
