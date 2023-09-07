import { TextField } from "@mui/material";
import { FC } from "react";
import { capitalise, isNameAlpha } from "../utils";

interface NameInputFieldProps {
  name: string;
  setName: (name: string) => void;
  setErrorMessage: (message: string) => void;
  isError: boolean;
  helperText: string;
}

const nameInvalidMessage = "Name can only contain letters and spaces";

const NameInputField: FC<NameInputFieldProps> = ({
  name,
  setName,
  setErrorMessage,
  isError,
  helperText,
}) => {
  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setErrorMessage("");
    setName(event.target.value);
    if (!isNameAlpha(event.target.value.trim())) {
      setErrorMessage(nameInvalidMessage);
    }
  };

  return (
    <TextField
      required
      label="Name"
      name={name}
      defaultValue={capitalise(name)}
      onChange={handleNameChange}
      error={isError}
      helperText={`${helperText}`}
    />
  );
};

export default NameInputField;
