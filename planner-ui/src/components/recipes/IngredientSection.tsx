import { Ingredient } from "../../interfaces/IngredientInterface";
import {
  CardActions,
  CardContent,
  List,
  ListItem,
  ListItemText,
} from "@mui/material";
import { Typography } from "@mui/material";
import Collapse from "@mui/material/Collapse";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import IconButton, { IconButtonProps } from "@mui/material/IconButton";
import { styled } from "@mui/material/styles";
import { useState } from "react";

interface Ingredients {
  ingredients: Ingredient[];
}

interface ExpandMoreProps extends IconButtonProps {
  expand: boolean;
}

const ExpandMore = styled((props: ExpandMoreProps) => {
  const { expand, ...other } = props;
  return <IconButton {...other} />;
})(({ theme, expand }) => ({
  transform: !expand ? "rotate(0deg)" : "rotate(180deg)",
  marginLeft: "auto",
  transition: theme.transitions.create("transform", {
    duration: theme.transitions.duration.shortest,
  }),
}));

const IngredientSection = (props: Ingredients) => {
  const [expanded, setExpanded] = useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  return (
    <>
      <CardActions disableSpacing>
        <Typography variant="h5">Ingredients</Typography>
        <ExpandMore
          expand={expanded}
          onClick={handleExpandClick}
          aria-expanded={expanded}
          aria-label="Show ingredients"
          sx={{
            backgroundColor: "secondary.main",
            "&:hover": { backgroundColor: "highlights.main" },
          }}
        >
          <ExpandMoreIcon sx={{ color: "primary.main" }} />
        </ExpandMore>
      </CardActions>

      <Collapse in={expanded} timeout="auto" unmountOnExit>
        <CardContent sx={{ py: "0" }}>
          <List dense={true} sx={{ py: "0" }}>
            {props.ingredients.map((ingredients) => (
              <ListItem key={ingredients.id} sx={{ padding: "0" }}>
                <ListItemText
                  primary={ingredients.name}
                  primaryTypographyProps={{
                    variant: "h6",
                    padding: "0.1rem",
                  }}
                />
              </ListItem>
            ))}
          </List>
        </CardContent>
      </Collapse>
    </>
  );
};

export default IngredientSection;
