import { expect } from "vitest";
import { format } from "@/utils";

describe("Format", () => {
  it("should format", async () => {
    expect(format(undefined)).toBe("");
    expect(format(NaN)).toBe("");
    expect(format(0)).toBe("");
    expect(format(1)).toBe("1");
    expect(format(-1)).toBe("-1");
    expect(format(123456789)).toBe("123'456'789");
    expect(format(-123456789)).toBe("-123'456'789");
    expect(format(123456789.9876)).toBe("123'456'789.988");
    expect(format(-123456789.9876)).toBe("-123'456'789.988");
  });
});
