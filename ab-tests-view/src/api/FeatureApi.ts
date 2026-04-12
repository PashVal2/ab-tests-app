import { authFetch } from "./AuthApi";
import type { CreateFeatureRequest, Feature } from "../types/FeatureTypes";

const uri = "http://localhost:8081/api/v1/features";

function buildProjectHeaders(projectId: number) {
  return {
    "X-Project-Id": String(projectId),
  };
}

export const getFeatures = async (projectId: number): Promise<Feature[]> => {
  const res = await authFetch(uri, {
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to load features");
  }

  return res.json();
};

export const getActiveFeatures = async (projectId: number): Promise<Feature[]> => {
  const res = await authFetch(`${uri}/active`, {
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to load active features");
  }

  return res.json();
};

export const createFeature = async (
  projectId: number,
  request: CreateFeatureRequest
): Promise<Feature> => {
  const res = await authFetch(uri, {
    method: "POST",
    headers: buildProjectHeaders(projectId),
    body: JSON.stringify(request),
  });

  if (!res.ok) {
    throw new Error("Failed to create feature");
  }

  return res.json();
};

export const updateFeature = async (
  projectId: number,
  featureId: number,
  request: CreateFeatureRequest
): Promise<Feature> => {
  const res = await authFetch(`${uri}/${featureId}`, {
    method: "PUT",
    headers: buildProjectHeaders(projectId),
    body: JSON.stringify(request),
  });

  if (!res.ok) {
    throw new Error("Failed to update feature");
  }

  return res.json();
};

export const activateFeature = async (
  projectId: number,
  featureId: number
): Promise<Feature> => {
  const res = await authFetch(`${uri}/${featureId}/activate`, {
    method: "POST",
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to activate feature");
  }

  return res.json();
};

export const deactivateFeature = async (
  projectId: number,
  featureId: number
): Promise<Feature> => {
  const res = await authFetch(`${uri}/${featureId}/deactivate`, {
    method: "POST",
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to deactivate feature");
  }

  return res.json();
};
