// noinspection SqlNoDataSourceInspection

import { expect } from "vitest";
import { encodePatch } from "@/SchemaFix";

describe("EncodePatch", () => {
  describe("basic dummy tests", () => {
    it("should handle empty", async () => {
      expect(encodePatch(undefined, "Java", "")).toBe('"",');
    });
    it("should handle empty non-java", async () => {
      expect(encodePatch(undefined, "SQL Script", "")).toBe(";");
    });

    it("should handle something", async () => {
      expect(encodePatch(undefined, "Java", "something")).toBe('"something",');
    });
    it("should handle something non-java", async () => {
      expect(encodePatch(undefined, "SQL Script", "something")).toBe(
        "something;",
      );
    });

    it("should handle something with a double quote", async () => {
      expect(encodePatch(undefined, "Java", 'some"thing')).toBe(
        '"some\\"thing",',
      );
    });
    it("should handle something with a double quote non-java", async () => {
      expect(encodePatch(undefined, "SQL Script", 'some"thing')).toBe(
        'some"thing;',
      );
    });

    it("should handle something with two double quotes", async () => {
      expect(encodePatch(undefined, "Java", 'some"thing"else')).toBe(
        '"some\\"thing\\"else",',
      );
    });
    it("should handle non-java", async () => {
      expect(encodePatch(undefined, "SQL Script", 'some"thing"else')).toBe(
        'some"thing"else;',
      );
    });
  });
  describe("simple SQL statements", () => {
    const s = "ALTER TABLE `AnItem` ADD COLUMN `col1` int";
    describe("standalone", () => {
      it("should handle simple non-java encoding", () => {
        expect(encodePatch(undefined, "SQL Script", s)).toBe(
          "ALTER TABLE `AnItem` ADD COLUMN `col1` int;",
        );
      });
      it("should handle simple java encoding", () => {
        expect(encodePatch(undefined, "Java", s)).toBe(
          '"ALTER TABLE `AnItem` ADD COLUMN `col1` int",',
        );
      });
    });

    describe("joined", () => {
      it("should handle joined non-java ", async () => {
        expect(encodePatch("head", "SQL Script", s)).toBe(
          "ALTER TABLE `AnItem` ADD COLUMN `col1` int,",
        );
        expect(encodePatch("middle", "SQL Script", s)).toBe(
          "ADD COLUMN `col1` int,",
        );
        expect(encodePatch("tail", "SQL Script", s)).toBe(
          "ADD COLUMN `col1` int;",
        );
      });
      it("should handle joined java", async () => {
        const s = "ALTER TABLE `AnItem` ADD COLUMN `col1` int";
        expect(encodePatch("head", "Java", s)).toBe(
          '"ALTER TABLE `AnItem` ADD COLUMN `col1` int," +',
        );
        expect(encodePatch("middle", "Java", s)).toBe(
          '"ADD COLUMN `col1` int," +',
        );
        expect(encodePatch("tail", "Java", s)).toBe('"ADD COLUMN `col1` int",');
      });
      it("should handle joined with many spaces", async () => {
        expect(
          encodePatch(
            "middle",
            "SQL Script",
            "ALTER   TABLE   `AnItem`   ADD   COLUMN   `col1`   int  ",
          ),
        ).toBe("ADD   COLUMN   `col1`   int  ,");
      });
      it("should handle joined with many spaces java", async () => {
        expect(
          encodePatch(
            "middle",
            "Java",
            "ALTER   TABLE   `AnItem`   ADD   COLUMN   `col1`   int  ",
          ),
        ).toBe('"ADD   COLUMN   `col1`   int  ," +');
      });

      it("should handle joined with minimal spaces", async () => {
        expect(
          encodePatch(
            "middle",
            "SQL Script",
            "ALTER TABLE`AnItem`ADD COLUMN`col1`int",
          ),
        ).toBe("ADD COLUMN`col1`int,");
      });
      it("should handle joined with minimal spaces java", async () => {
        expect(
          encodePatch(
            "middle",
            "Java",
            "ALTER TABLE`AnItem`ADD COLUMN`col1`int",
          ),
        ).toBe('"ADD COLUMN`col1`int," +');
      });

      it("should handle joined with various characters", async () => {
        expect(
          encodePatch(
            "middle",
            "SQL Script",
            "ALTER TABLE `AnItem_09AZaz` ADD COLUMN `col1` int",
          ),
        ).toBe("ADD COLUMN `col1` int,");
      });
      it("should handle joined with various characters java", async () => {
        expect(
          encodePatch(
            "middle",
            "Java",
            "ALTER TABLE `AnItem_09AZaz` ADD COLUMN `col1` int",
          ),
        ).toBe('"ADD COLUMN `col1` int," +');
      });

      it("should handle joined with double quotes", async () => {
        expect(
          encodePatch(
            "middle",
            "SQL Script",
            'ALTER TABLE "AnItem" ADD COLUMN "col1" int',
          ),
        ).toBe('ADD COLUMN "col1" int,');
      });
      it("should handle joined with double quotes in java", async () => {
        expect(
          encodePatch(
            "middle",
            "Java",
            'ALTER TABLE "AnItem" ADD COLUMN "col1" int',
          ),
        ).toBe('"ADD COLUMN \\"col1\\" int," +');
      });
      it("should handle joined with inconsistent quotes", async () => {
        expect(
          encodePatch(
            "middle",
            "SQL Script",
            'ALTER TABLE "AnItem` ADD COLUMN `col1` int',
          ),
        ).toBe('ALTER TABLE "AnItem` ADD COLUMN `col1` int,');
      });

      it("should handle joined with inconsistent quotes java", async () => {
        expect(
          encodePatch(
            "middle",
            "Java",
            'ALTER TABLE "AnItem` ADD COLUMN `col1` int',
          ),
        ).toBe('"ALTER TABLE \\"AnItem` ADD COLUMN `col1` int," +');
      });
    });
  });

  describe("complex SQL statements", () => {
    const createTable =
      'CREATE TABLE "myTable1Name"(' +
      '"column" int not null,' +
      '"column2" int not null,' +
      'CONSTRAINT "myTable1Name_PK" PRIMARY KEY("column"),' +
      'CONSTRAINT "myTable1Name_column_MN" CHECK("column">=0),' +
      'CONSTRAINT "myTable1Name_column_MX" CHECK("column"<=55))';
    it("should split create table non-java", async () => {
      expect(encodePatch(undefined, "SQL Script", createTable)).toBe(
        'CREATE TABLE "myTable1Name"(' +
          '"column" int not null,' +
          '"column2" int not null,\n' +
          'CONSTRAINT "myTable1Name_PK" PRIMARY KEY("column"),\n' +
          'CONSTRAINT "myTable1Name_column_MN" CHECK("column">=0),\n' +
          'CONSTRAINT "myTable1Name_column_MX" CHECK("column"<=55));',
      );
    });
    it("should split create table java", async () => {
      expect(encodePatch(undefined, "Java", createTable)).toBe(
        '"CREATE TABLE \\"myTable1Name\\"(' +
          '\\"column\\" int not null,' +
          '\\"column2\\" int not null," +\n' +
          '"CONSTRAINT \\"myTable1Name_PK\\" PRIMARY KEY(\\"column\\")," +\n' +
          '"CONSTRAINT \\"myTable1Name_column_MN\\" CHECK(\\"column\\">=0)," +\n' +
          '"CONSTRAINT \\"myTable1Name_column_MX\\" CHECK(\\"column\\"<=55))",',
      );
    });
    it("should not split non-create table non-java", async () => {
      expect(encodePatch(undefined, "SQL Script", "X" + createTable)).toBe(
        "X" + createTable + ";",
      );
    });
    it("should not split non-create table java", async () => {
      expect(encodePatch(undefined, "Java", "X" + createTable)).toBe(
        '"X' + createTable.replaceAll('"', '\\"') + '",',
      );
    });
    it("should not split create table joinable non-java", async () => {
      expect(encodePatch("head", "SQL Script", createTable)).toBe(
        createTable + ",",
      );
    });
    it("should not split create table joinable java", async () => {
      expect(encodePatch("head", "Java", createTable)).toBe(
        '"' + createTable.replaceAll('"', '\\"') + '," +',
      );
    });
  });
});
