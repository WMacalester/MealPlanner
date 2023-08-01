import { MenuCreateDto } from "../interfaces/MenuInterface";
import { Recipe } from "../interfaces/RecipeInterface";
import { apiSlice } from "./api";

const BASE_URL = "/menu";

export const menuApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    createRandomMenu: builder.mutation<
      Recipe[],
      { payload?: MenuCreateDto; number: number }
    >({
      query: ({ number, payload }) => ({
        url: BASE_URL,
        method: "POST",
        params: { number },
        body: payload,
      }),
      invalidatesTags: ["Menu"],
    }),
  }),
});

export const { useCreateRandomMenuMutation } = menuApi;
