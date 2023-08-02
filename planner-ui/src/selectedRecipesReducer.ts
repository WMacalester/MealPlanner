import { createSlice } from "@reduxjs/toolkit";

interface NewState {
  ids: string[];
}

const initialState: NewState = {
  ids: [],
};

const newSlice = createSlice({
  name: "selectedRecipeIds",
  initialState,
  reducers: {
    toggleId: (state, action) => {
      if (!state.ids.includes(action.payload)) {
        return { ...state, ids: [...state.ids, action.payload] };
      } else {
        const updatedIds = state.ids.filter((e) => e !== action.payload);
        return { ...state, ids: updatedIds };
      }
    },
    resetSelectedRecipeIds: () => {
      return initialState;
    },
  },
});

export const { toggleId, resetSelectedRecipeIds } = newSlice.actions;
export default newSlice.reducer;
