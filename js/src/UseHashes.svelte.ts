import type { HashRequest, HashResponse, Hash as ApiHash } from "@/api/types";
import { post } from "@/api/api";

export class Hash {
  private api: ApiHash;
  readonly type: string;
  readonly name: string;
  readonly plainTextLimit: number;
  readonly plainTextValidator: string | undefined;
  readonly algorithmID: string;
  readonly algorithmDescription: string;

  private toggled = $state(false);
  plainText = $state("");
  private plainTextHashed: string | undefined = $state(undefined);
  private measurement: number | undefined = $state(undefined);

  constructor(apiParameterForAssigmentOnly: ApiHash) {
    this.api = $state(apiParameterForAssigmentOnly);
    this.type = this.api.type;
    this.name = this.api.name;
    this.plainTextLimit = $derived(this.api.plainTextLimit);
    this.plainTextValidator = $derived(this.api.plainTextValidator);
    this.algorithmID = $derived(this.api.algorithmID);
    this.algorithmDescription = $derived(this.api.algorithmDescription);
  }

  update(api: ApiHash) {
    if (this.type !== api.type) throw new Error(this.type);
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
  }

  isToggled() {
    return this.toggled;
  }

  toggle() {
    this.toggled = !this.toggled;

    this.plainText = ""; // drop when hidden, because it may contain sensitive data
    this.plainTextHashed = undefined;
  }

  computeHash(errors: Error[]) {
    this.doHash(this.plainText)
      .then((r) => (this.plainTextHashed = r.hash))
      .catch((e) => errors.push(e));
  }

  getPlainTextHashed(): string | undefined {
    return this.plainTextHashed;
  }

  measure(errors: Error[]): Promise<void> {
    return this.doHash("example password")
      .then((r) => {
        this.measurement = r.elapsedNanos;
      })
      .catch((e) => {
        errors.push(e);
      });
  }

  getMeasurement(): number | undefined {
    return this.measurement;
  }

  private doHash(plainText: string): Promise<HashResponse> {
    return post<HashRequest, HashResponse>("hashes/hash", {
      type: this.type,
      name: this.name,
      plainText,
    });
  }
}
