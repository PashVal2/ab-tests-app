import { authFetch } from "./AuthApi";
import type {
  CreateProjectRequest,
  ProjectResponse
} from "../types/ProjectTypes";

const uri = "http://localhost:8081/api/v1/project";

export const getMyProjects = async (): Promise<ProjectResponse[]> => {
  const res = await authFetch(uri);

  if (!res.ok) {
    throw new Error("Failed to load projects");
  }

  return res.json();
};

export const getProjectById = async (
  projectId: string | number
): Promise<ProjectResponse> => {
  const res = await authFetch(`${uri}/${projectId}`);

  if (!res.ok) {
    throw new Error("Failed to load project");
  }

  return res.json();
};

export const getProjectByCode = async (
  projectCode: string
): Promise<ProjectResponse> => {
  const res = await authFetch(`${uri}/by-code/${projectCode}`);

  if (!res.ok) {
    throw new Error("Failed to load project");
  }

  return res.json();
};

export const createProject = async (
  request: CreateProjectRequest
): Promise<ProjectResponse> => {
  const res = await authFetch(uri, {
    method: "POST",
    body: JSON.stringify(request),
  });

  if (!res.ok) {
    throw new Error("Failed to create project");
  }

  return res.json();
};
