export function delay<T>(
  call: Promise<T>,
  fail: boolean = false,
  timeout: number = 1000,
): Promise<T> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (fail) {
        reject(new Error("utils.ts delay failed"));
      } else {
        call.then((r) => resolve(r));
      }
    }, timeout);
  });
}

export function useWithStoreSingle<S, T>(
  store: Map<string, T>,
  createFunction: (source: S) => T,
  updateFunction: (target: T, source: S) => void,
  source: S,
): T {
  const result = useWithStore(
    store,
    (_source) => "theOneAndOnly",
    createFunction,
    updateFunction,
    [source],
  );
  if (result.length !== 1) throw new Error("" + result.length);
  return result[0];
}

export function useWithStore<S, T>(
  store: Map<string, T>,
  keyFunction: (source: S) => string,
  createFunction: (source: S) => T,
  updateFunction: (target: T, source: S) => void,
  sources: readonly S[],
): T[] {
  return sources.map((source) => {
    const key: string = keyFunction(source);
    const stored = store.get(key);
    if (stored) {
      updateFunction(stored, source);
      return stored;
    }
    const target = createFunction(source);
    store.set(key, target);
    return target;
  });
}
