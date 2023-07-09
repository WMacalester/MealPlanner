import React, { FC } from "react";
import CancelButton from "./CancelButton";
import SubmitButton from "./SubmitButton";
import { Box } from "@mui/material";

interface SubmitAndCancelButtonsProps {
  handleCancelClick: (e: React.MouseEvent<HTMLButtonElement>) => void;
  handleSubmitClick: (e: React.MouseEvent<HTMLButtonElement>) => void;
  isSubmitDisabled: boolean;
}

const SubmitAndCancelButtons: FC<SubmitAndCancelButtonsProps> = ({
  handleCancelClick,
  handleSubmitClick,
  isSubmitDisabled,
}) => {
  return (
    <Box
      display={"flex"}
      justifyContent={"space-evenly"}
      sx={{ marginTop: "1rem" }}
    >
      <SubmitButton
        handleClick={handleSubmitClick}
        isDisabled={isSubmitDisabled}
      />
      <CancelButton handleClick={handleCancelClick} />
    </Box>
  );
};

export default SubmitAndCancelButtons;
