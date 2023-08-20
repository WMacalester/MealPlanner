import { configureStore } from "@reduxjs/toolkit";
import { apiSlice } from "./api/api";
import selectedRecipesReducer from "./selectedRecipesReducer";
import authReducer from "./api/authSlice";

const store = configureStore({
  reducer: {
    [apiSlice.reducerPath]: apiSlice.reducer,
    auth: authReducer,
    selectedRecipeIds: selectedRecipesReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({ serializableCheck: false }).concat(
      apiSlice.middleware
    ),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;
