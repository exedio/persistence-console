export class PromiseTracker<E> {
  private _promise: Promise<E>;
  private _pending = $state(true);

  constructor(private readonly factory: () => Promise<E>) {
    this._promise = $state(factory().finally(() => (this._pending = false)));
  }

  promise(): Promise<E> {
    return this._promise;
  }

  pending(): boolean {
    return this._pending;
  }

  reload(): void {
    this._pending = true;
    this.factory()
      .then((e) => (this._promise = Promise.resolve(e)))
      .finally(() => (this._pending = false));
  }
}
