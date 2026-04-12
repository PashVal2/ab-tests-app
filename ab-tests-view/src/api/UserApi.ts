import type { UserWithRoles } from "../types/UserTypes";
import { authFetch } from "./AuthApi";

const uri: string = 'http://localhost:8081/api/v1/users';

export async function me(): Promise<UserWithRoles | null> {
  try {
    const res = await authFetch(`${uri}/me`);
    if (!res.ok) return null;
    return res.json();
  } catch {
    return null;
  }
}
