import { Provider } from "react-redux";
import { render, screen } from "@testing-library/react";
import store from "../../store";
import Recipes from "./Recipes";

test("Recipes component renders with recipes from server", async () => {
  render(
    <Provider store={store}>
      <Recipes />
    </Provider>
  );
  const recipes = await screen.findAllByText(
    new RegExp("test recipe name [12]")
  );
  expect(recipes).toHaveLength(2);
  expect(screen.getByText("test recipe name 1")).toBeInTheDocument();
  expect(screen.getByText("test recipe name 2")).toBeInTheDocument();
});
