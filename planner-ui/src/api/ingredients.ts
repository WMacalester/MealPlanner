import {
  Ingredient,
  IngredientCreateDto,
} from "../interfaces/IngredientInterface";
import { apiSlice } from "./api";

const BASE_URL = "/ingredients";

export const ingredientsApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getAllIngredients: builder.query<Ingredient[], void>({
      query: () => ({
        url: BASE_URL,
        headers: {
          Accept: "application/json",
        },
      }),
      providesTags: ["Ingredient"],
    }),
    getAllIngredientsCsv: builder.query<Blob, void>({
      query: () => ({
        url: BASE_URL,
        headers: {
          Accept: "text/csv",
        },
        responseHandler: (res) => {
          return res.blob();
        },
      }),
      providesTags: ["Ingredient"],
    }),
    createNewIngredient: builder.mutation<Ingredient, IngredientCreateDto>({
      query: (payload: IngredientCreateDto) => ({
        url: BASE_URL,
        method: "POST",
        body: payload,
      }),
      invalidatesTags: ["Ingredient"],
    }),
  }),
});

export const {
  useGetAllIngredientsQuery,
  useGetAllIngredientsCsvQuery,
  useCreateNewIngredientMutation,
} = ingredientsApi;
