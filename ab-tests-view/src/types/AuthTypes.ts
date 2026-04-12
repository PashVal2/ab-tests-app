export interface LoginRequest {
  username: string,
  password: string,
}

export interface RegisterRequest {
  name: string,
  username: string,
  password: string,
  passwordConfirm: string,
}

export interface TokenResponse {
  access: string
}

export interface RegisterFormState {
  name: string;
  username: string;
  password: string;
  passwordConfirm: string;
}

export interface RegisterFormErrors {
  name?: string;
  username?: string;
  password?: string;
  passwordConfirm?: string;
}

export interface LoginFormErrors {
  username?: string;
  password?: string;
}

export interface LoginFormState {
  username: string;
  password: string;
}
