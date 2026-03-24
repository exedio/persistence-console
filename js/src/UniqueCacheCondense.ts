import type { UniqueConstraintMetric as Metric } from "@/api/types";

export type UniqueConstraintFeature = {
  readonly feature: string;
  hitDescription?: string;
  hit?: number;
  missDescription?: string;
  miss?: number;
};

export function condense(source: Metric[]): UniqueConstraintFeature[] {
  const result: UniqueConstraintFeature[] = [];
  const map = new Map<string, UniqueConstraintFeature>();
  source.forEach((s) => {
    const feature = s.tags.feature;
    let value = map.get(feature);
    if (!value) {
      value = {
        feature,
      };
      map.set(feature, value);
    }
    switch (s.tags.result) {
      case "hit":
        value.hitDescription = s.description;
        value.hit = s.count;
        break;
      case "miss":
        value.missDescription = s.description;
        value.miss = s.count;
        break;
    }
  });
  map.values().forEach((value) => {
    result.push(value);
  });
  return result;
}
