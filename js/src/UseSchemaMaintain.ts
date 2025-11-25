import type {
  SchemaMaintainOperation,
  SchemaMaintainResponse,
} from "@/api/types";

export function successMessage(
  operation: SchemaMaintainOperation,
  response: SchemaMaintainResponse,
): string {
  return (
    operation + " succeeded after " + response.elapsedNanos / 1000000 + "ms"
  );
}
