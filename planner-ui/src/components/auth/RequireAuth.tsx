import { Navigate, Outlet, useLocation } from "react-router-dom";
import { UserRole } from "../../interfaces/UserRole";
import { FC } from "react";
import { selectCurrentRole } from "../../api/authSlice";
import { useSelector } from "react-redux";

interface RequireAuthProps {
  allowedRoles: UserRole[];
}

const RequireAuth: FC<RequireAuthProps> = ({ allowedRoles }) => {
  const location = useLocation();
  const role = useSelector(selectCurrentRole);

  if (!role) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (!allowedRoles.includes(role)) {
    return <Navigate to="/forbidden" state={{ from: location }} replace />;
  }

  return <Outlet />;
};

export default RequireAuth;
