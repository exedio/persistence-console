export class PromiseTracker<E> {
  private _promise: Promise<E>;
  private _pending = $state(true);
  private _last: E | undefined = $state(undefined);

  constructor(private readonly factory: () => Promise<E>) {
    this._promise = $state(
      factory()
        .then((e) => (this._last = e))
        .finally(() => (this._pending = false)),
    );
  }

  promise(): Promise<E> {
    return this._promise;
  }

  pending(): boolean {
    return this._pending;
  }

  last(): E | undefined {
    return this._last;
  }

  reload(): void {
    this._pending = true;
    this.factory()
      .then((e) => {
        this._promise = Promise.resolve(e);
        this._last = e;
      })
      .finally(() => (this._pending = false));
  }
}
