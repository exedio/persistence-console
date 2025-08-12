import type { HashesResponse } from "@/api/types";

export class UseHashes {
  private api: HashesResponse;
  readonly type: string;
  readonly name: string;
  readonly plainTextLimit: number;
  readonly plainTextValidator: string | undefined;
  readonly algorithmID: string;
  readonly algorithmDescription: string;

  toggled = $state(false);
  plainText = $state("");
  plainTextHashed: string | undefined = $state(undefined);

  constructor(api: HashesResponse) {
    this.api = $state(api);
    this.type = api.type;
    this.name = api.name;
    this.plainTextLimit = $derived(this.api.plainTextLimit);
    this.plainTextValidator = $derived(api.plainTextValidator);
    this.algorithmID = $derived(api.algorithmID);
    this.algorithmDescription = $derived(api.algorithmDescription);
  }

  update(api: HashesResponse) {
    this.api = api;
  }
}
