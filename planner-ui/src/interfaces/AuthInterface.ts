import { UserRole } from "./UserRole";

interface BaseUser {
  username: string;
}

export interface UserLogin extends BaseUser {
  password: string;
}

export interface User extends BaseUser {
  role: UserRole;
}

export interface AuthState {
  username: string | null;
  userRole: UserRole | null;
}
