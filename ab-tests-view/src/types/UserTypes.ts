export interface UserResponse {
  id: number;
  username: string;
  name: string;
}

export interface UserWithRoles {
  id: number;
  username: string;
  name: string;
  roles: string[];
}
