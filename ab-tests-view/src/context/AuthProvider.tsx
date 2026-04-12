import { useEffect, useState, type ReactNode } from "react";
import type { UserWithRoles } from "../types/UserTypes";
import { AuthContext } from "./AuthContext";
import { me } from "../api/UserApi";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserWithRoles | null>(null);
  const [authLoading, setAuthLoading] = useState(true);

  useEffect(() => {
    const initUser = async () => {
      const token = localStorage.getItem("access");

      if (!token) {
        setAuthLoading(false);
        return;
      }

      try {
        const userData = await me();
        setUser(userData);
      } catch (err) {
        console.log(err);
        localStorage.removeItem("access");
        setUser(null);
      } finally {
        setAuthLoading(false);
      }
    };

    initUser();
  }, []);

  if (authLoading) {
    return null;
  }

  return (
    <AuthContext.Provider value={{ user, setUser, authLoading }}>
      {children}
    </AuthContext.Provider>
  );
}
