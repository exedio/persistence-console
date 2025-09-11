package com.exedio.cope.console;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ColoredTableCellTest {

  @Test
  void forStrings() {
    assertEquals(
      List.of(new ColoredTableCell("x", 1, true)),
      ColoredTableCell.forStrings(new String[] { "x" })
    );
    assertEquals(
      List.of(
        new ColoredTableCell("x", 1, true),
        new ColoredTableCell("y", 1, true)
      ),
      ColoredTableCell.forStrings(new String[] { "x", "y" })
    );
  }
}
