import { DietType } from "../interfaces/DietType";
import {
  Recipe,
  RecipeCreateDto,
  RecipeEditDto,
} from "../interfaces/RecipeInterface";
import { apiSlice } from "./api";

const BASE_URL = "/recipes";

interface getAllRecipesQueryArgs {
  recipeName?: string;
  dietType?: DietType;
}

export const recipesApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getAllRecipes: builder.query<Recipe[], getAllRecipesQueryArgs>({
      query: (params) => ({
        url: BASE_URL,
        params,
      }),
      providesTags: ["Recipe"],
    }),
    getAllRecipesCsv: builder.query<Blob, void>({
      query: () => ({
        url: BASE_URL,
        headers: {
          Accept: "text/csv",
        },
        responseHandler: (res) => {
          return res.blob();
        },
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
  useGetAllRecipesCsvQuery,
  useCreateNewRecipeMutation,
  useEditRecipeMutation,
  useDeleteRecipeMutation,
} = recipesApi;
