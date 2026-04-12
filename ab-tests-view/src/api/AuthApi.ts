import type { LoginRequest, RegisterRequest, TokenResponse } from "../types/AuthTypes";

const uri: string = "http://localhost:8081/api/v1/auth";

export type ApiError = {
  code?: string;
  message?: string;
};

async function readApiError(res: Response): Promise<ApiError> {
  try {
    return (await res.json()) as ApiError;
  } catch {
    return {
      code: "UNKNOWN_ERROR",
      message: "Ошибка запроса",
    };
  }
}

export async function login(request: LoginRequest): Promise<void> {
  const res = await fetch(`${uri}/login`, {
    headers: { "Content-Type": "application/json" },
    method: "POST",
    body: JSON.stringify(request),
    credentials: "include",
  });

  if (!res.ok) {
    throw await readApiError(res);
  }

  const responseData: TokenResponse = await res.json();
  localStorage.setItem("access", responseData.access);
}

export async function register(request: RegisterRequest): Promise<void> {
  const res = await fetch(`${uri}/register`, {
    headers: { "Content-Type": "application/json" },
    method: "POST",
    body: JSON.stringify(request),
  });

  if (!res.ok) {
    throw await readApiError(res);
  }
}

export async function verifyEmail(token: string): Promise<boolean> {
  const res = await fetch(`${uri}/confirm-email?token=${encodeURIComponent(token)}`);
  return res.ok;
}

export async function logout(): Promise<void> {
  const response = await fetch(`${uri}/logout`, {
    method: "POST",
    credentials: "include",
  });

  if (!response.ok) {
    throw await readApiError(response);
  }

  localStorage.removeItem("access");
}

export async function refresh(): Promise<string> {
  const response = await fetch(`${uri}/refresh`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    localStorage.removeItem("access");
    throw await readApiError(response);
  }

  const data = await response.json();
  localStorage.setItem("access", data.access);
  return data.access;
}

export async function authFetch(url: string, options: RequestInit = {}) {
  let access = localStorage.getItem("access");
  if (!access) throw new Error("No access token");

  const makeRequest = async (token: string) => {
    return fetch(url, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        ...(options.headers || {}),
      },
    });
  };

  let response = await makeRequest(access);

  if (response.status === 401) {
    access = await refresh();
    response = await makeRequest(access);
  }

  return response;
}

export async function resendConfirmation(email: string): Promise<void> {
  const res = await fetch(
    `${uri}/resend-confirmation?email=${encodeURIComponent(email)}`,
    {
      method: "POST",
    }
  );

  if (!res.ok) {
    throw await readApiError(res);
  }
}
