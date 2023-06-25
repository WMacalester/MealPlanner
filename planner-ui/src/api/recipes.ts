import { Recipe, RecipeCreateDto } from "../interfaces/RecipeInterface";
import { apiSlice } from "./api";

const BASE_URL = "/recipes";

export const recipesApi = apiSlice.injectEndpoints({
    endpoints: (builder) => ({
        getAllRecipes: builder.query<Recipe[], void>({
            query: () => ({
                url: BASE_URL,
            }),
            providesTags: ["Recipe"],
        }),
        createNewRecipe: builder.mutation<Recipe, RecipeCreateDto>({
            query: (payload: RecipeCreateDto) => ({
                url: BASE_URL,
                method: "POST",
                body: payload,
            }),
            invalidatesTags: ["Recipe"],
        }),
    }),
});

export const { useGetAllRecipesQuery, useCreateNewRecipeMutation } = recipesApi;
