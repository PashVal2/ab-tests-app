import { createContext } from "react";
import type { UserWithRoles } from "../types/UserTypes";

type AuthContextType = {
  user: UserWithRoles | null;
  setUser: (user: UserWithRoles | null) => void;
  authLoading: boolean;
};

export const AuthContext = createContext<AuthContextType | undefined>(undefined);
