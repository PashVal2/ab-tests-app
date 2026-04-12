import { authFetch } from "./AuthApi";
import type {
  EventPageResponse,
  EventSummaryResponse,
  EventType,
} from "../types/EventTypes";

const uri = "http://localhost:8081/api/v1/events";

type EventParams = {
  externalKey?: string;
  eventType?: EventType;
  externalUserId?: string;
  createdFrom?: string;
  createdTo?: string;
  page?: number;
  size?: number;
};

function buildQuery(params: EventParams): string {
  const searchParams = new URLSearchParams();

  if (params.externalKey) searchParams.set("externalKey", params.externalKey);
  if (params.eventType) searchParams.set("eventType", params.eventType);
  if (params.externalUserId) searchParams.set("externalUserId", params.externalUserId);
  if (params.createdFrom) searchParams.set("createdFrom", params.createdFrom);
  if (params.createdTo) searchParams.set("createdTo", params.createdTo);
  if (params.page !== undefined) searchParams.set("page", String(params.page));
  if (params.size !== undefined) searchParams.set("size", String(params.size));

  return searchParams.toString();
}

function buildProjectHeaders(projectId: number) {
  return {
    "X-Project-Id": String(projectId),
  };
}

export const getEvents = async (
  projectId: number,
  params: EventParams
): Promise<EventPageResponse> => {
  const res = await authFetch(`${uri}?${buildQuery(params)}`, {
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to load events");
  }

  return res.json();
};

export const getEventsSummary = async (
  projectId: number,
  params: Omit<EventParams, "page" | "size">
): Promise<EventSummaryResponse> => {
  const res = await authFetch(`${uri}/summary?${buildQuery(params)}`, {
    headers: buildProjectHeaders(projectId),
  });

  if (!res.ok) {
    throw new Error("Failed to load event summary");
  }

  return res.json();
};
