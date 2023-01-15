import { rest } from "msw";
import { BASE_URL } from "../../constants";
import recipeDetails from "./data/recipeDetails.json";

export const handlers = [
  rest.get(`${BASE_URL}/recipes/`, async (req, res, ctx) => {
    return res(ctx.status(200), ctx.json(recipeDetails));
  }),
];
