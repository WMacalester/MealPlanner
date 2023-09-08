import React from "react";
import TextField, { TextFieldVariants } from "@mui/material/TextField";
import { styled } from "@mui/system";

interface StyledTextFieldProps {
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  label: string;
}

const TextFieldStyling = styled(TextField)(({ theme }) => ({
  input: {
    color: theme.palette.highlights.main,
  },
  "& .MuiInputLabel-root": {
    color: theme.palette.highlights.main,
    "&.Mui-focused": {
      color: theme.palette.highlights.main,
    },
    "&.Mui-error": {
      color: theme.palette.error.main,
    },
  },
  "& .MuiInput-underline:after": {
    borderBottomColor: theme.palette.highlights.main,
  },
  "& .MuiOutlinedInput-root": {
    "& fieldset": {
      borderColor: theme.palette.secondary.main,
    },
    "&:hover fieldset": {
      borderColor: theme.palette.highlights.main,
    },
    "&.Mui-focused fieldset": {
      borderColor: theme.palette.highlights.main,
    },
    "&.Mui-error fieldset": {
      borderColor: theme.palette.error.main,
    },
  },
}));

const StyledTextField: React.FC<
  StyledTextFieldProps & {
    type?: React.HTMLInputTypeAttribute;
    value?: number;
    min?: number;
    max?: number;
    helperText?: string;
    error?: boolean;
    variant?: TextFieldVariants;
    width?: string;
  }
> = ({
  value,
  onChange,
  min,
  error,
  max,
  variant,
  helperText,
  label,
  type,
  width,
}) => {
  return (
    <TextFieldStyling
      variant={variant || "standard"}
      value={value}
      type={type}
      label={label}
      onChange={onChange}
      error={error}
      sx={{ width: width }}
      helperText={helperText}
      InputProps={{
        inputProps: {
          min: min,
          max: max,
        },
      }}
    />
  );
};

export default StyledTextField;
