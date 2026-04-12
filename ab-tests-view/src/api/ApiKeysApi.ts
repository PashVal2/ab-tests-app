import { authFetch } from "./AuthApi";
import type {
  ApiKeyResponse,
  CreateApiKeyRequest,
  CreateApiKeyResponse,
  ApiKeySearchParams,
} from "../types/ApiKeysTypes";

const uri = "http://localhost:8081/api/v1/api-keys";

function buildProjectHeaders(projectId: number) {
  return {
    "X-Project-Id": String(projectId),
  };
}

export const getApiKeys = async (
  projectId: number,
  params: ApiKeySearchParams = {}
): Promise<ApiKeyResponse[]> => {
  const search = new URLSearchParams();

  if (params.name) search.set("name", params.name);
  if (params.keyPrefix) search.set("keyPrefix", params.keyPrefix);
  if (params.status) search.set("status", params.status);
  if (params.createdFrom) search.set("createdFrom", params.createdFrom);
  if (params.createdTo) search.set("createdTo", params.createdTo);

  const res = await authFetch(`${uri}?${search.toString()}`, {
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to load api keys");
  }

  return res.json();
};

export const createApiKey = async (
  projectId: number,
  request: CreateApiKeyRequest
): Promise<CreateApiKeyResponse> => {
  const res = await authFetch(uri, {
    method: "POST",
    headers: buildProjectHeaders(projectId),
    body: JSON.stringify(request),
  });

  if (!res.ok) {
    throw new Error("Failed to create api key");
  }

  return res.json();
};

export const revokeApiKey = async (
  projectId: number,
  apiKeyId: number
): Promise<ApiKeyResponse> => {
  const res = await authFetch(`${uri}/${apiKeyId}/revoke`, {
    method: "POST",
    headers: {
      "X-Project-Id": String(projectId),
    },
  });

  if (!res.ok) {
    throw new Error("Failed to revoke api key");
  }

  return res.json();
};
