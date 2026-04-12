export type ApiKeyStatus = "ACTIVE" | "REVOKED";

export type CreateApiKeyRequest = {
  name: string;
};

export type CreateApiKeyResponse = {
  id: number;
  name: string;
  status: ApiKeyStatus;
  rawKey: string;
};

export type ApiKeyResponse = {
  id: number;
  name: string;
  keyPrefix: string;
  status: ApiKeyStatus;
  createdAt: string;
};

export type ApiKeySearchParams = {
  name?: string;
  keyPrefix?: string;
  status?: ApiKeyStatus;
  createdFrom?: string;
  createdTo?: string;
};
