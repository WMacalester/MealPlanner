import { Button } from "@mui/material";
import { useState, FC, ComponentType } from "react";
import PostAddIcon from "@mui/icons-material/PostAdd";
import { AddModalProps } from "../interfaces/AddModalProps";

interface AddButtonProps {
  label: string;
  Modal: ComponentType<AddModalProps>;
}

const AddButton: FC<AddButtonProps> = ({ label, Modal }) => {
  const [openModal, setOpenModal] = useState(false);
  const handleOpen = () => setOpenModal(true);
  const handleClose = () => {
    setOpenModal(false);
  };

  return (
    <div>
      <Button
        aria-label={`${label} button`}
        onClick={handleOpen}
        endIcon={<PostAddIcon />}
        variant="outlined"
        sx={{
          color: "highlights.main",
          marginX: "1rem",
        }}
      >
        {label}
      </Button>
      <Modal open={openModal} handleClose={handleClose} />
    </div>
  );
};

export default AddButton;
