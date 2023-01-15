import { Provider } from "react-redux";
import { render, screen } from "@testing-library/react";
import store from "../../store";
import RecipeCard from "./RecipeCard";

test("Recipe card renders recipe info correctly", async () => {
  render(
    <Provider store={store}>
      <RecipeCard {...{ id: "uuid1", name: "test recipe name 1" }} />
    </Provider>
  );
  expect(screen.getByText("test recipe name 1")).toBeInTheDocument();
  expect(screen.queryByText("uuid1")).not.toBeInTheDocument();
});
