import { expect } from "vitest";
import { encodePatch, workOnFixes } from "@/SchemaFix";

describe("EncodePatch", () => {
  it("should handle empty", async () => {
    expect(encodePatch(true, "")).toBe('"",');
  });
  it("should handle something", async () => {
    expect(encodePatch(true, "something")).toBe('"something",');
  });
  it("should handle something with a double quote", async () => {
    expect(encodePatch(true, 'some"thing')).toBe('"some\\"thing",');
  });
  it("should handle something with a two double quotes", async () => {
    expect(encodePatch(true, 'some"thing"else')).toBe('"some\\"thing\\"else",');
  });
  it("should handle non-java", async () => {
    expect(encodePatch(false, 'some"thing"else')).toBe('some"thing"else;');
  });
});
