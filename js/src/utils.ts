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
