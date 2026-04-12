import { Navigate } from "react-router-dom";
import { useContext, type JSX } from "react";
import { AuthContext } from "./AuthContext";

function ABRouteWrapper({ children }: { children: JSX.Element }) {
  const auth = useContext(AuthContext);

  if (!auth) {
    return null;
  }

  const { user } = auth;

  if (!user) {
    return <Navigate to="/auth" replace />;
  }

  return children;
}

export default ABRouteWrapper;
