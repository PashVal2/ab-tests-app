export type Feature = {
  id: number;
  code: string;
  name: string;
  active: boolean;
};

export type CreateFeatureRequest = {
  code: string;
  name: string;
};
