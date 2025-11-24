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

import static com.exedio.cope.console.ApiTest.writeJson;
import static com.exedio.cope.console.SchemaAlterApi.alter;
import static com.exedio.cope.junit.CopeAssert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.util.Sources;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaAlterApiTest {

  @Test
  void testTableAdd() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "CREATE TABLE \\"MyType\\"(\\"this\\" INTEGER not null,\\"myString\\" VARCHAR(80) not null,\\"myString2\\" VARCHAR(80) not null,\\"myInt\\" INTEGER not null,\\"myTarget\\" INTEGER not null,CONSTRAINT \\"MyType_PK\\" PRIMARY KEY(\\"this\\"),CONSTRAINT \\"MyType_this_MN\\" CHECK(\\"this\\">=0),CONSTRAINT \\"MyType_this_MX\\" CHECK(\\"this\\"<=2147483647),CONSTRAINT \\"MyType_myString_MN\\" CHECK(CHAR_LENGTH(\\"myString\\")>=1),CONSTRAINT \\"MyType_myString_MX\\" CHECK(CHAR_LENGTH(\\"myString\\")<=80),CONSTRAINT \\"MyType_myString2_MN\\" CHECK(CHAR_LENGTH(\\"myString2\\")>=1),CONSTRAINT \\"MyType_myString2_MX\\" CHECK(CHAR_LENGTH(\\"myString2\\")<=80),CONSTRAINT \\"MyType_myInt_MN\\" CHECK(\\"myInt\\">=-2147483648),CONSTRAINT \\"MyType_myInt_MX\\" CHECK(\\"myInt\\"<=2147483647),CONSTRAINT \\"MyType_myTarget_MN\\" CHECK(\\"myTarget\\">=0),CONSTRAINT \\"MyType_myTarget_MX\\" CHECK(\\"myTarget\\"<=2147483647),CONSTRAINT \\"MyType_unique\\" UNIQUE(\\"myString\\",\\"myString2\\"))"
      }""",
      writeJson(alter(MODEL, request("table", "", "MyType", "add")))
    );
  }

  @Test
  void testTableDrop() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "DROP TABLE \\"MyType\\""
      }""",
      writeJson(alter(MODEL, request("table", "", "MyType", "drop")))
    );
  }

  @Test
  void testTableRename() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "ALTER TABLE \\"MyType\\" RENAME TO \\"MyTypeX\\""
      }""",
      writeJson(
        alter(MODEL, request("table", "", "MyType", "rename", "MyTypeX"))
      )
    );
  }

  @Test
  void testTableAddNotExists() {
    assertFails(
      () -> alter(MODEL, request("table", "", "MyTypex", "add")),
      ApiTextException.class,
      "404 table not found within " + MODEL
    );
  }

  @Test
  void testColumnAdd() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "ALTER TABLE \\"MyType\\" ADD COLUMN \\"this\\" INTEGER not null"
      }""",
      writeJson(alter(MODEL, request("column", "MyType", "this", "add")))
    );
  }

  @Test
  void testColumnDrop() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "ALTER TABLE \\"MyType\\" DROP COLUMN \\"this\\""
      }""",
      writeJson(alter(MODEL, request("column", "MyType", "this", "drop")))
    );
  }

  @Test
  void testColumnRename() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "ALTER TABLE \\"MyType\\" ALTER COLUMN \\"this\\" RENAME TO \\"thisX\\""
      }""",
      writeJson(
        alter(
          MODEL,
          request("column", "MyType", "this", "rename", "thisX")
        )
      )
    );
  }

  @Test
  void testColumnModify() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "ALTER TABLE \\"MyType\\" ALTER \\"this\\" SET DATA TYPE INTEGER not null"
      }""",
      writeJson(
        alter(MODEL, request("column", "MyType", "this", "modify"))
      )
    );
  }

  @Test
  void testColumnAddNotExistsTable() {
    assertFails(
      () -> alter(MODEL, request("column", "MyTypex", "this", "add")),
      ApiTextException.class,
      "404 table not found within " + MODEL
    );
  }

  @Test
  void testColumnAddNotExistsColumn() {
    assertFails(
      () -> alter(MODEL, request("column", "MyType", "thisx", "add")),
      ApiTextException.class,
      "404 column not found within " + MODEL
    );
  }

  @Test
  void testConstraintAdd() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "ALTER TABLE \\"MyType\\" ADD CONSTRAINT \\"MyType_this_MN\\" CHECK(\\"this\\">=0)"
      }""",
      writeJson(
        alter(
          MODEL,
          request("constraint", "MyType", "MyType_this_MN", "add")
        )
      )
    );
  }

  @Test
  void testConstraintDrop() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "ALTER TABLE \\"MyType\\" DROP CONSTRAINT \\"MyType_this_MN\\""
      }""",
      writeJson(
        alter(
          MODEL,
          request("constraint", "MyType", "MyType_this_MN", "drop")
        )
      )
    );
  }

  @Test
  void testConstraintAddNotExistsTable() {
    assertFails(
      () ->
        alter(
          MODEL,
          request("constraint", "MyTypex", "MyType_this_MN", "add")
        ),
      ApiTextException.class,
      "404 table not found within " + MODEL
    );
  }

  @Test
  void testConstraintAddNotExistsConstraint() {
    assertFails(
      () ->
        alter(
          MODEL,
          request("constraint", "MyType", "MyType_this_MNx", "add")
        ),
      ApiTextException.class,
      "404 constraint not found within " + MODEL
    );
  }

  @Test
  void testSequenceAdd() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "CREATE SEQUENCE \\"MyType_myInt_Seq\\" AS INTEGER START WITH 77 INCREMENT BY 1"
      }""",
      writeJson(
        alter(MODEL, request("sequence", "", "MyType_myInt_Seq", "add"))
      )
    );
  }

  @Test
  void testSequenceDrop() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "sql" : "DROP SEQUENCE \\"MyType_myInt_Seq\\""
      }""",
      writeJson(
        alter(MODEL, request("sequence", "", "MyType_myInt_Seq", "drop"))
      )
    );
  }

  @Test
  void testSequenceAddNotExists() {
    assertFails(
      () ->
        alter(MODEL, request("sequence", "", "MyType_myInt_Seqx", "add")),
      ApiTextException.class,
      "404 sequence not found within " + MODEL
    );
  }

  @Test
  void testParameterRequired() {
    assertFails(
      () -> alter(MODEL, request("sequence", "", "", "add")),
      ApiTextException.class,
      "404 parameter name must be set"
    );
  }

  private static ParameterRequest request(
    final String subject,
    final String table,
    final String name,
    final String method
  ) {
    return new ParameterRequest(
      Map.of("subject", subject, "table", table, "name", name, "method", method)
    );
  }

  private static ParameterRequest request(
    final String subject,
    final String table,
    final String name,
    final String method,
    final String value
  ) {
    return new ParameterRequest(
      Map.of(
        "subject",
        subject,
        "table",
        table,
        "name",
        name,
        "method",
        method,
        "value",
        value
      )
    );
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final StringField myString = new StringField().toFinal();

    @UsageEntryPoint
    static final StringField myString2 = new StringField().toFinal();

    @UsageEntryPoint
    static final IntegerField myInt = new IntegerField()
      .toFinal()
      .defaultToNext(77);

    @UsageEntryPoint
    static final UniqueConstraint unique = UniqueConstraint.create(
      myString,
      myString2
    );

    @UsageEntryPoint
    static final ItemField<?> myTarget = ItemField.create(
      MyTarget.class
    ).toFinal();

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(MyType.class, MyType::new);

    private MyType(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final class MyTarget extends Item {

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(
      MyTarget.class,
      MyTarget::new
    );

    private MyTarget(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final Model MODEL = new Model(MyType.TYPE, MyTarget.TYPE);

  @BeforeAll
  static void connect() {
    final java.util.Properties props = new java.util.Properties();
    props.setProperty("connection.url", "jdbc:hsqldb:mem:copeconsoletest");
    props.setProperty("connection.username", "sa");
    props.setProperty("connection.password", "");
    MODEL.connect(ConnectProperties.create(Sources.view(props, "DESC")));
  }

  @AfterAll
  static void disconnect() {
    MODEL.disconnect();
  }

  @BeforeEach
  void createSchema() {
    MODEL.createSchema();
  }

  @AfterEach
  void tearDownSchema() {
    MODEL.rollbackIfNotCommitted();
    MODEL.tearDownSchema();
  }
}
