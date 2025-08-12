import type {
  DoHashRequest,
  DoHashResponse,
  HashesResponse,
} from "@/api/types";
import { post } from "@/api/api";

export class UseHashes {
  private api: HashesResponse;
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

  doHash(plainText: string): Promise<DoHashResponse> {
    return post<DoHashRequest, DoHashResponse>("doHash", {
      type: this.type,
      name: this.name,
      plainText,
    });
  }
}
