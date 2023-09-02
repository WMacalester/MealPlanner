import { selectCurrentRole } from "../api/authSlice";
import { UserRole } from "../interfaces/UserRole";
import { useAppSelector } from "./redux-hooks";

const useIsUserPermittedHook = (allowedRoles: UserRole[]) => {
  const role = useAppSelector(selectCurrentRole);
  return role ? allowedRoles.includes(role) : false;
};

export default useIsUserPermittedHook;
