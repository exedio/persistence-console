export type HashID = {
  readonly type: string;
  readonly name: string;
};

export type HashesResponse = HashID & {
  readonly plainTextLimit: number;
  readonly plainTextValidator: string | undefined;
  readonly algorithmID: string;
  readonly algorithmDescription: string;
};

export type DoHashRequest = HashID & {
  readonly plainText: string;
};

export type DoHashResponse = {
  readonly hash: string;
  readonly elapsedNanos: number;
};

export type SuspicionsResponse = {
  readonly type: string;
  readonly name: string;
  readonly suspicions: readonly string[];
};

export function toId(response: HashesResponse): string {
  return response.type + "." + response.name;
}
