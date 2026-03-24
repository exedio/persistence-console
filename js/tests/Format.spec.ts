import { expect } from "vitest";
import { format, ratioLog10 } from "@/utils";

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
  it("should compute ratio", async () => {
    expect(ratioLog10(undefined, undefined)).toBeUndefined();
    expect(ratioLog10(0, undefined)).toBeUndefined();
    expect(ratioLog10(undefined, 0)).toBeUndefined();
    expect(ratioLog10(7, undefined)).toBeUndefined();
    expect(ratioLog10(undefined, 7)).toBeUndefined();
    expect(ratioLog10(1, 1)).toBe(0);
    expect(ratioLog10(10, 1)).toBe(1);
    expect(ratioLog10(100, 1)).toBe(2);
    expect(ratioLog10(1000, 10)).toBe(2);
    expect(ratioLog10(-7, 7)).toBeNaN();
  });
});
