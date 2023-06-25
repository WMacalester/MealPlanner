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

export const { useGetAllIngredientsQuery, useCreateNewIngredientMutation } =
  ingredientsApi;
