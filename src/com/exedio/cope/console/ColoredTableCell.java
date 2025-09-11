package com.exedio.cope.console;

import java.util.Arrays;
import java.util.List;

record ColoredTableCell(String value, int colspan, boolean nextColor) {
  static List<ColoredTableCell> forStrings(final String[] values) {
    return Arrays.stream(values)
      .map(v -> new ColoredTableCell(v, 1, true))
      .toList();
  }
}
