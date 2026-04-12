export type MetricType =
  | "CTR"
  | "VIEW_RATE"
  | "LIKE_RATE"
  | "WATCH_START_RATE"
  | "WATCH_FINISH_RATE"
  | "WATCH_THROUGH_RATE";

export type ExperimentStatus = "DRAFT" | "RUNNING" | "FINISHED";

export type VariantState = {
  selected: Record<number, boolean>;
  weights: Record<number, string>;
  trafficPercent: string;
  externalCode: string;
};

export type VariantFeatureResponse = {
  featureId: number;
  featureCode: string;
  featureName: string;
  weight: number;
};

export type VariantResponse = {
  id: number;
  name: string;
  control: boolean;
  trafficPercent: number;
  externalCode: string;
  features: VariantFeatureResponse[];
};

export type FeatureWeightRequest = {
  featureId: number;
  weight: number;
};

export type VariantRequest = {
  name: string;
  control: boolean;
  trafficPercent: number;
  externalCode: string;
  features: FeatureWeightRequest[];
};

export type ExperimentRequest = {
  name: string;
  nullHypothesis: string;
  alternativeHypothesis: string;
  primaryMetric: MetricType;
  variants: VariantRequest[];
};

export type ExperimentResponse = {
  id: number;
  name: string;
  externalKey: string;
  status: ExperimentStatus;
  primaryMetric: MetricType;
};

export type ExperimentDetailsResponse = {
  id: number;
  name: string;
  externalKey: string;
  status: ExperimentStatus;
  nullHypothesis: string;
  alternativeHypothesis: string;
  primaryMetric: MetricType;
  variants: VariantResponse[];
};

export type ExperimentSearchParams = {
  name?: string;
  status?: ExperimentStatus;
  primaryMetric?: MetricType;
  createdFrom?: string;
  createdTo?: string;
};

export type VariantMetricResponse = {
  variantId: number;
  variantName: string;
  externalCode: string;
  control: boolean;

  assignedUsers: number;
  denominatorUsers: number;
  numeratorUsers: number;

  impressions: number;
  clicks: number;
  views: number;
  watchStarts: number;
  watchFinishes: number;
  likes: number;

  metricValue: number;
  metricName: string;

  upliftPercent: number | null;
  pValue: number | null;

  diffFromControl: number | null;
  ci95Lower: number | null;
  ci95Upper: number | null;
  statisticallySignificant: boolean;
};

export type ExperimentResultsResponse = {
  experimentId: number;
  experimentName: string;
  externalKey: string;
  status: ExperimentStatus;
  primaryMetric: MetricType;
  winnerVariantId: number | null;
  winnerVariantName: string | null;
  statisticallySignificant: boolean;
  variants: VariantMetricResponse[];
};
