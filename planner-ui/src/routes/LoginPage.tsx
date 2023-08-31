import { Grid, TextField, Typography } from "@mui/material";
import React, { FC, useState } from "react";
import { useAppDispatch } from "../hooks/redux-hooks";
import {
  useAuthenticateUserMutation,
  useRegisterUserMutation,
} from "../api/auth";
import { setCredentials } from "../api/authSlice";
import { useNavigate } from "react-router-dom";
import { isNameAlphaNumeric } from "../utils";
import LoginDialogButton from "../components/button/LoginDialogButton";

interface InputFieldProps {
  value: string;
  label: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  error: boolean;
  type: string;
}

const InputField: FC<InputFieldProps> = ({
  label,
  value,
  onChange,
  error,
  type,
}) => {
  return (
    <TextField
      required
      label={label}
      value={value}
      onChange={onChange}
      error={error}
      type={type}
      sx={{
        marginBottom: "1rem",
        ".MuiInputBase-label": { fontSize: "1.25rem" },
      }}
    />
  );
};

const LoginPage: FC = () => {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [usernameErrorMessage, setUsernameErrorMessage] = useState("");
  const [formErrorMessage, setFormErrorMessage] = useState("");
  const [passwordErrorMessage, setPasswordErrorMessage] = useState("");

  const [login] = useAuthenticateUserMutation();
  const [register] = useRegisterUserMutation();
  const dispatch = useAppDispatch();

  const handleUsernameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setFormErrorMessage("");
    setUsernameErrorMessage("");
    setUsername(event.target.value);
    if (!isNameAlphaNumeric(event.target.value.trim())) {
      setUsernameErrorMessage("Name can only contain letters and numbers");
    }
  };

  const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setFormErrorMessage("");
    setPasswordErrorMessage("");
    if (!RegExp(/^[A-Za-z0-9 !#$%&?@^_]*$/).test(event.target.value)) {
      setPasswordErrorMessage(
        "Invalid character used. Alphanumeric characters and the following symbols are allowed: !#$%&?@^_"
      );
    }
    setPassword(event.target.value);
  };

  const redirectAfterSuccess = () => {
    navigate("/");
  };

  const handleRegister = async (e: React.MouseEvent) => {
    e.preventDefault();

    await register({ username, password })
      .unwrap()
      .then((userData) => {
        dispatch(
          setCredentials({
            ...userData,
            username: userData.username,
            userRole: userData.userRole,
          })
        );
        redirectAfterSuccess();
      })
      .catch((error) => {
        if (error.status === 403) {
          setFormErrorMessage("A user with that name already exists");
        } else {
          setFormErrorMessage(
            "An unexpected error has occurred. Please try again."
          );
        }
      });
  };

  const handleSubmit = async (e: React.MouseEvent) => {
    e.preventDefault();

    await login({ username, password })
      .unwrap()
      .then((userData) => {
        dispatch(
          setCredentials({
            ...userData,
            username: userData.username,
            userRole: userData.userRole,
          })
        );
        redirectAfterSuccess();
      })
      .catch((error) => {
        if (error.status === 403) {
          setFormErrorMessage("Credentials were incorrect");
        } else {
          setFormErrorMessage(
            "An unexpected error has occurred. Please try again"
          );
        }
      });
  };

  return (
    <Grid
      container
      justifyContent="center"
      direction="column"
      alignItems="center"
      height="100%"
    >
      <InputField
        value={username}
        label="Username"
        onChange={handleUsernameChange}
        error={usernameErrorMessage !== ""}
        type="text"
      />
      <InputField
        value={password}
        label="Password"
        onChange={handlePasswordChange}
        error={passwordErrorMessage !== ""}
        type="password"
      />
      <Grid
        container
        spacing={3}
        justifyContent={"center"}
        marginBottom={"2rem"}
      >
        <Grid item>
          <LoginDialogButton handleClick={handleRegister} name={"Register"} />
        </Grid>
        <Grid item>
          <LoginDialogButton handleClick={handleSubmit} name={"Login"} />
        </Grid>
      </Grid>

      <Typography sx={{ color: "error.main" }}>{formErrorMessage}</Typography>
      <Typography sx={{ color: "error.main" }}>
        {usernameErrorMessage}
      </Typography>
      <Typography sx={{ color: "error.main" }}>
        {passwordErrorMessage}
      </Typography>
    </Grid>
  );
};

export default LoginPage;
