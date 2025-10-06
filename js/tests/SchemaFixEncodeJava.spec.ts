import { expect } from "vitest";
import { encodeJava, workOnFixes } from "@/SchemaFix";

describe("EncodeJava", () => {
  it("should handle empty", async () => {
    expect(encodeJava("")).toBe('"",');
  });
  it("should handle something", async () => {
    expect(encodeJava("something")).toBe('"something",');
  });
  it("should handle something with a double quote", async () => {
    expect(encodeJava('some"thing')).toBe('"some\\"thing",');
  });
  it("should handle something with a two double quotes", async () => {
    expect(encodeJava('some"thing"else')).toBe('"some\\"thing\\"else",');
  });
});
