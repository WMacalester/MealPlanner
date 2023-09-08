import { Button } from "@mui/material";
import { useState, FC, ComponentType } from "react";
import PostAddIcon from "@mui/icons-material/PostAdd";
import { AddModalProps } from "../../interfaces/AddModalProps";
import MainPageButtonStyle from "../styles/MainPageButtonStyle";

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
    <>
      <Button
        aria-label={`${label} button`}
        onClick={handleOpen}
        endIcon={<PostAddIcon />}
        sx={MainPageButtonStyle}
      >
        {label}
      </Button>
      <Modal open={openModal} handleClose={handleClose} />
    </>
  );
};

export default AddButton;
