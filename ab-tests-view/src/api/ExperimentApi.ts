import { authFetch } from "./AuthApi";
import type {
  ExperimentRequest,
  ExperimentResponse,
  ExperimentDetailsResponse,
  ExperimentResultsResponse,
  ExperimentSearchParams,
} from "../types/ExperimentTypes";

const uri = "http://localhost:8081/api/v1/experiment";

function buildProjectHeaders(projectId: number) {
  return {
    "X-Project-Id": String(projectId),
  };
}

export const createExperiment = async (
  projectId: number,
  request: ExperimentRequest
): Promise<void> => {
  const res = await authFetch(uri, {
    method: "POST",
    headers: buildProjectHeaders(projectId),
    body: JSON.stringify(request),
  });

  if (res.status !== 201) {
    throw new Error("Failed to create experiment");
  }
};

export const getExperiments = async (
  projectId: number,
  params: ExperimentSearchParams = {}
): Promise<ExperimentResponse[]> => {
  const search = new URLSearchParams();

  if (params.name) search.set("name", params.name);
  if (params.status) search.set("status", params.status);
  if (params.primaryMetric) search.set("primaryMetric", params.primaryMetric);
  if (params.createdFrom) search.set("createdFrom", params.createdFrom);
  if (params.createdTo) search.set("createdTo", params.createdTo);

  const res = await authFetch(`${uri}?${search.toString()}`, {
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to load experiments");
  }

  return res.json();
};

export const getExperimentById = async (
  projectId: number,
  id: string | number
): Promise<ExperimentDetailsResponse> => {
  const res = await authFetch(`${uri}/${id}`, {
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to load experiment");
  }

  return res.json();
};

export const startExperiment = async (
  projectId: number,
  id: string | number
): Promise<void> => {
  const res = await authFetch(`${uri}/${id}/start`, {
    method: "POST",
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to start experiment");
  }
};

export const finishExperiment = async (
  projectId: number,
  id: string | number
): Promise<void> => {
  const res = await authFetch(`${uri}/${id}/finish`, {
    method: "POST",
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to finish experiment");
  }
};

export const getExperimentResults = async (
  projectId: number,
  id: string | number
): Promise<ExperimentResultsResponse> => {
  const res = await authFetch(`${uri}/${id}/results`, {
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to load experiment results");
  }

  return res.json();
};

export const getExperimentByKey = async (
  projectId: number,
  externalKey: string
): Promise<ExperimentDetailsResponse> => {
  const res = await authFetch(
    `${uri}/by-key/${externalKey}`,
    {
      headers: {
        "X-Project-Id": String(projectId),
      },
    }
  );

  if (!res.ok) {
    throw new Error("Failed to load experiment");
  }

  return res.json();
};
