// noinspection SqlNoDataSourceInspection

import { expect } from "vitest";
import { encodePatch } from "@/SchemaFix";

describe("EncodePatch", () => {
  it("should handle empty", async () => {
    expect(encodePatch(undefined, true, "")).toBe('"",');
  });
  it("should handle something", async () => {
    expect(encodePatch(undefined, true, "something")).toBe('"something",');
  });
  it("should handle something with a double quote", async () => {
    expect(encodePatch(undefined, true, 'some"thing')).toBe('"some\\"thing",');
  });
  it("should handle something with a two double quotes", async () => {
    expect(encodePatch(undefined, true, 'some"thing"else')).toBe(
      '"some\\"thing\\"else",',
    );
  });
  it("should handle non-java", async () => {
    expect(encodePatch(undefined, false, 'some"thing"else')).toBe(
      'some"thing"else;',
    );
  });
  it("should handle joined non-java ", async () => {
    const s = "ALTER TABLE `AnItem` ADD COLUMN `col1` int";
    expect(encodePatch(undefined, false, s)).toBe(
      "ALTER TABLE `AnItem` ADD COLUMN `col1` int;",
    );
    expect(encodePatch("head", false, s)).toBe(
      "ALTER TABLE `AnItem` ADD COLUMN `col1` int,",
    );
    expect(encodePatch("middle", false, s)).toBe("ADD COLUMN `col1` int,");
    expect(encodePatch("tail", false, s)).toBe("ADD COLUMN `col1` int;");
  });
  it("should handle joined java", async () => {
    const s = "ALTER TABLE `AnItem` ADD COLUMN `col1` int";
    expect(encodePatch(undefined, true, s)).toBe(
      '"ALTER TABLE `AnItem` ADD COLUMN `col1` int",',
    );
    expect(encodePatch("head", true, s)).toBe(
      '"ALTER TABLE `AnItem` ADD COLUMN `col1` int," +',
    );
    expect(encodePatch("middle", true, s)).toBe('"ADD COLUMN `col1` int," +');
    expect(encodePatch("tail", true, s)).toBe('"ADD COLUMN `col1` int",');
  });
  it("should handle joined with many spaces", async () => {
    expect(
      encodePatch(
        "middle",
        false,
        "ALTER   TABLE   `AnItem`   ADD   COLUMN   `col1`   int  ",
      ),
    ).toBe("ADD   COLUMN   `col1`   int  ,");
  });
  it("should handle joined with minimal spaces", async () => {
    expect(
      encodePatch("middle", false, "ALTER TABLE`AnItem`ADD COLUMN`col1`int"),
    ).toBe("ADD COLUMN`col1`int,");
  });
  it("should handle joined with various characters", async () => {
    expect(
      encodePatch(
        "middle",
        false,
        "ALTER TABLE `AnItem_09AZaz` ADD COLUMN `col1` int",
      ),
    ).toBe("ADD COLUMN `col1` int,");
  });
  it("should handle joined with double quotes", async () => {
    expect(
      encodePatch(
        "middle",
        false,
        'ALTER TABLE "AnItem" ADD COLUMN "col1" int',
      ),
    ).toBe('ADD COLUMN "col1" int,');
  });
  it("should handle joined with double quotes in java", async () => {
    expect(
      encodePatch("middle", true, 'ALTER TABLE "AnItem" ADD COLUMN "col1" int'),
    ).toBe('"ADD COLUMN \\"col1\\" int," +');
  });
  it("should handle joined with inconsistent quotes", async () => {
    expect(
      encodePatch(
        "middle",
        false,
        'ALTER TABLE "AnItem` ADD COLUMN `col1` int',
      ),
    ).toBe('ALTER TABLE "AnItem` ADD COLUMN `col1` int,');
  });
});
