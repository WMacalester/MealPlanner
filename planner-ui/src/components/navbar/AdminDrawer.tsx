import { Button, Drawer } from "@mui/material";
import React, { useState } from "react";
import { useGetAllIngredientsCsvQuery } from "../../api/ingredients";
import { useGetAllRecipesCsvQuery } from "../../api/recipes";
import AddButton from "../button/AddButton";
import BlobDownloadButton, { DownloadType } from "../button/BlobDownloadButton";
import IngredientAddModal from "../ingredients/IngredientAddModal";
import RecipeAddModal from "../recipes/RecipeAddModal";
import MainPageButtonStyle from "../styles/MainPageButtonStyle";

const AdminOnlyButtons = () => {
  const { data: recipes } = useGetAllRecipesCsvQuery();
  const { data: ingredients } = useGetAllIngredientsCsvQuery();
  return (
    <>
      <AddButton Modal={IngredientAddModal} label={"Add Ingredient"} />
      <AddButton Modal={RecipeAddModal} label={"Add Recipe"} />
      <BlobDownloadButton
        filename={"data-recipes"}
        label={"Recipe"}
        data={recipes}
        type={DownloadType.CSV}
      />
      <BlobDownloadButton
        filename={"data-ingredients"}
        label={"Ingredients"}
        data={ingredients}
        type={DownloadType.CSV}
      />
    </>
  );
};

const AdminDrawer = () => {
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);

  const toggleDrawer =
    (open: boolean) => (event: React.KeyboardEvent | React.MouseEvent) => {
      if (
        event &&
        event.type === "keydown" &&
        ((event as React.KeyboardEvent).key === "Tab" ||
          (event as React.KeyboardEvent).key === "Shift")
      ) {
        return;
      }

      setIsDrawerOpen(open);
    };

  return (
    <>
      <Button
        onClick={toggleDrawer(true)}
        sx={{
          ...MainPageButtonStyle,
          border: 2,
          borderColor: "highlights.main",
        }}
      >
        Admin Options
      </Button>
      <Drawer
        anchor={"top"}
        open={isDrawerOpen}
        onClose={toggleDrawer(false)}
        PaperProps={{
          sx: {
            backgroundColor: "primary.main",
          },
        }}
      >
        {<AdminOnlyButtons />}
      </Drawer>
    </>
  );
};

export default AdminDrawer;
