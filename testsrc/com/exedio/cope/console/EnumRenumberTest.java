/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.console;

import static com.exedio.cope.SchemaInfo.getColumnValue;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.console.EnumRenumber.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeSchemaValue;
import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.dsmf.Schema;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConnectRule.class)
public class EnumRenumberTest {

  @Test
  void testNormal(final ConnectRule connect) throws SQLException {
    assertEquals(10, getColumnValue(Normal.a));
    assertEquals(15, getColumnValue(Normal.b));
    assertEquals(20, getColumnValue(Normal.c));
    connect.connect(MODEL);
    final var renumberSQL = """
      UPDATE "MyType" SET "normal" = CASE "normal" \
      WHEN 15 THEN 20 \
      WHEN 20 THEN 30 \
      ELSE "normal" END WHERE "normal" IN (15,20)""";
    assertEquals(List.of(renumberSQL), get(Normal.class, MODEL));

    try (var tx = MODEL.startTransactionTry("normal")) {
      new MyType(Normal.a);
      new MyType(Normal.b);
      new MyType(Normal.c);
      new MyType((Normal) null);
      tx.commit();
    }
    MODEL.getSchema()
      .getTable(getTableName(MyType.TYPE))
      .getConstraint("MyType_normal_EN")
      .drop();
    try (var con = newConnection(MODEL); var stmt = con.createStatement()) {
      final String querySQL =
        "SELECT \"normal\" FROM \"MyType\" ORDER BY \"this\"";
      assertEquals(Arrays.asList(10, 15, 20, null), fetch(con, querySQL));

      assertEquals(2, stmt.executeUpdate(renumberSQL));
      assertEquals(Arrays.asList(10, 20, 30, null), fetch(con, querySQL));

      assertEquals(1, stmt.executeUpdate(renumberSQL)); // BEWARE! renumberSQL is not meant to be applied twice!
      assertEquals(Arrays.asList(10, 30, 30, null), fetch(con, querySQL));

      assertEquals(0, stmt.executeUpdate(renumberSQL));
      assertEquals(Arrays.asList(10, 30, 30, null), fetch(con, querySQL));
    }
  }

  private enum Normal {
    a,

    @CopeSchemaValue(15)
    b,

    c,
  }

  @Test
  void testSingle(final ConnectRule connect) throws SQLException {
    assertEquals(10, getColumnValue(Single.a));
    assertEquals(20, getColumnValue(Single.b));
    assertEquals(25, getColumnValue(Single.c));
    connect.connect(MODEL);
    // TODO join UPDATEs on the same table
    final String renumberSQL = """
      UPDATE "MyType" SET "single" = CASE "single" \
      WHEN 25 THEN 30 \
      ELSE "single" END WHERE "single" IN (25)""";
    final String renumberSQL2 = """
      UPDATE "MyType" SET "single2" = CASE "single2" \
      WHEN 25 THEN 30 \
      ELSE "single2" END WHERE "single2" IN (25)""";
    final String renumberSQL3 = """
      UPDATE "MyType2" SET "single3" = CASE "single3" \
      WHEN 25 THEN 30 \
      ELSE "single3" END WHERE "single3" IN (25)""";
    assertEquals(
      List.of(renumberSQL, renumberSQL2, renumberSQL3),
      get(Single.class, MODEL)
    );

    try (var tx = MODEL.startTransactionTry("single")) {
      new MyType(Single.a, Single.c);
      new MyType(Single.b, null);
      new MyType(Single.c, Single.a);
      new MyType(null, Single.b);
      new MyType2(Single.b);
      new MyType2(Single.c);
      new MyType2(Single.a);
      new MyType2((Single) null);
      tx.commit();
    }
    final Schema schema = MODEL.getSchema();
    schema
      .getTable(getTableName(MyType.TYPE))
      .getConstraint("MyType_single_EN")
      .drop();
    schema
      .getTable(getTableName(MyType.TYPE))
      .getConstraint("MyType_single2_EN")
      .drop();
    schema
      .getTable(getTableName(MyType2.TYPE))
      .getConstraint("MyType2_single3_EN")
      .drop();
    try (var con = newConnection(MODEL); var stmt = con.createStatement()) {
      final String querySQL =
        "SELECT \"single\" FROM \"MyType\" ORDER BY \"this\"";
      final String querySQL2 =
        "SELECT \"single2\" FROM \"MyType\" ORDER BY \"this\"";
      final String querySQL3 =
        "SELECT \"single3\" FROM \"MyType2\" ORDER BY \"this\"";
      assertEquals(Arrays.asList(10, 20, 25, null), fetch(con, querySQL));
      assertEquals(Arrays.asList(25, null, 10, 20), fetch(con, querySQL2));
      assertEquals(Arrays.asList(20, 25, 10, null), fetch(con, querySQL3));

      assertEquals(1, stmt.executeUpdate(renumberSQL));
      assertEquals(Arrays.asList(10, 20, 30, null), fetch(con, querySQL)); // modified
      assertEquals(Arrays.asList(25, null, 10, 20), fetch(con, querySQL2));
      assertEquals(Arrays.asList(20, 25, 10, null), fetch(con, querySQL3));

      assertEquals(1, stmt.executeUpdate(renumberSQL2));
      assertEquals(Arrays.asList(10, 20, 30, null), fetch(con, querySQL));
      assertEquals(Arrays.asList(30, null, 10, 20), fetch(con, querySQL2)); // modified
      assertEquals(Arrays.asList(20, 25, 10, null), fetch(con, querySQL3));

      assertEquals(1, stmt.executeUpdate(renumberSQL3));
      assertEquals(Arrays.asList(10, 20, 30, null), fetch(con, querySQL));
      assertEquals(Arrays.asList(30, null, 10, 20), fetch(con, querySQL2));
      assertEquals(Arrays.asList(20, 30, 10, null), fetch(con, querySQL3)); // modified
    }
  }

  private enum Single {
    a,
    b,

    @CopeSchemaValue(25)
    c,
  }

  @Test
  void testNone(final ConnectRule connect) {
    assertEquals(10, getColumnValue(None.a));
    assertEquals(20, getColumnValue(None.b));
    assertEquals(30, getColumnValue(None.c));
    connect.connect(MODEL);
    assertEquals(null, get(None.class, MODEL));
  }

  private enum None {
    a,
    b,
    c,
  }

  @Test
  void testRedundant(final ConnectRule connect) {
    assertEquals(10, getColumnValue(Redundant.a));
    assertEquals(20, getColumnValue(Redundant.b));
    assertEquals(30, getColumnValue(Redundant.c));
    connect.connect(MODEL);
    assertEquals(null, get(Redundant.class, MODEL));
  }

  private enum Redundant {
    a,

    @CopeSchemaValue(20) // This is the value that would have been assigned without the annotation anyway
    b,

    c,
  }

  private static final class MyType extends Item {

    static final EnumField<Normal> normal = EnumField.create(
      Normal.class
    ).optional();

    static final EnumField<Single> single = EnumField.create(
      Single.class
    ).optional();

    static final EnumField<Single> single2 = EnumField.create(
      Single.class
    ).optional();

    @UsageEntryPoint
    static final EnumField<?> none = EnumField.create(None.class).optional();

    @UsageEntryPoint
    static final EnumField<?> redundant = EnumField.create(
      Redundant.class
    ).optional();

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(MyType.class, MyType::new);

    private MyType(final Normal normal) {
      super(SetValue.map(MyType.normal, normal));
    }

    private MyType(final Single single, final Single single2) {
      super(
        SetValue.map(MyType.single, single),
        SetValue.map(MyType.single2, single2)
      );
    }

    private MyType(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final class MyType2 extends Item {

    static final EnumField<Single> single3 = EnumField.create(
      Single.class
    ).optional();

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(MyType2.class, MyType2::new);

    private MyType2(final Single single3) {
      super(SetValue.map(MyType2.single3, single3));
    }

    private MyType2(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final Model MODEL = new Model(MyType.TYPE, MyType2.TYPE);

  private static List<Object> fetch(final Connection con, final String sql)
    throws SQLException {
    final ArrayList<Object> result = new ArrayList<>();
    try (var stmt = con.createStatement(); var rs = stmt.executeQuery(sql)) {
      while (rs.next()) result.add(rs.getObject(1));
    }
    return result;
  }
}
