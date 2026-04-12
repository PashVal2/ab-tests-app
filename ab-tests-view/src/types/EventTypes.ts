export type EventType =
  | "IMPRESSION"
  | "CLICK"
  | "VIEW"
  | "WATCH_START"
  | "WATCH_FINISH"
  | "LIKE";

export type UserEventResponse = {
  id: number;
  experimentName: string;
  experimentExternalKey: string;
  variantName: string;
  externalUserId: string;
  contentId: string | null;
  eventType: EventType;
  createdAt: string;
};

export type EventSummaryResponse = {
  total: number;
  impressions: number;
  clicks: number;
  views: number;
  watchStarts: number;
  watchFinishes: number;
  likes: number;
};

export type EventPageResponse = {
  content: UserEventResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};
