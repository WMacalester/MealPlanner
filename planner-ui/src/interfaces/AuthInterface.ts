export interface TokenInterface {
  accessToken: string;
  refreshToken: string;
}

export interface UserLogin {
  username: string;
  password: string;
}

export interface User extends UserLogin, TokenInterface {}

export interface AuthState {
  user: UserLogin | null;
  accessToken: string | null;
  refreshToken: string | null;
}

export interface AuthPayload extends TokenInterface {
  user: UserLogin | null;
}
