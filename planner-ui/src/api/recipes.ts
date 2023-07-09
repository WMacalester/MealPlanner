import {
  Recipe,
  RecipeCreateDto,
  RecipeEditDto,
} from "../interfaces/RecipeInterface";
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
    editRecipe: builder.mutation<
      Recipe,
      { payload: RecipeEditDto; id: string }
    >({
      query: (arg) => ({
        url: BASE_URL + "/" + arg.id,
        method: "PUT",
        body: arg.payload,
      }),
      invalidatesTags: ["Recipe"],
    }),
    deleteRecipe: builder.mutation<void, String>({
      query: (id: string) => ({
        url: BASE_URL + "/" + id,
        method: "DELETE",
      }),
      invalidatesTags: ["Recipe"],
    }),
  }),
});

export const {
  useGetAllRecipesQuery,
  useCreateNewRecipeMutation,
  useEditRecipeMutation,
  useDeleteRecipeMutation,
} = recipesApi;
