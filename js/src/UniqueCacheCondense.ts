import type { UniqueConstraintMetric as Metric } from "@/api/types";

export type UniqueConstraintFeature = {
  readonly feature: string;
  readonly description: string;
  hit?: number;
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
        description: s.description,
        hit: s.tags.result == "hit" ? s.count : undefined,
        miss: s.tags.result == "miss" ? s.count : undefined,
      };
      map.set(feature, value);
    } else {
      switch (s.tags.result) {
        case "hit":
          value.hit = s.count;
          break;
        case "miss":
          value.miss = s.count;
          break;
      }
    }
  });
  map.values().forEach((value) => {
    result.push(value);
  });
  return result;
}
