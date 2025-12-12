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
import static com.exedio.cope.console.SchemaGetApi.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.console.SchemaGetApi.ColumnResponse;
import com.exedio.cope.console.SchemaGetApi.TableResponse;
import com.exedio.cope.util.Sources;
import com.exedio.dsmf.Table;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaGetApiTest {

  @Test
  void testOk() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "tables" : [ {
          "name" : "MyType",
          "columns" : [ {
            "name" : "this",
            "type" : "INTEGER not null",
            "constraints" : [ {
              "name" : "MyType_PK",
              "type" : "PrimaryKey"
            }, {
              "name" : "MyType_this_MN",
              "type" : "Check",
              "clause" : "\\"this\\">=0"
            }, {
              "name" : "MyType_this_MX",
              "type" : "Check",
              "clause" : "\\"this\\"<=2147483647"
            } ]
          }, {
            "name" : "myString",
            "type" : "VARCHAR(80) not null",
            "constraints" : [ {
              "name" : "MyType_myString_MN",
              "type" : "Check",
              "clause" : "CHAR_LENGTH(\\"myString\\")>=1"
            }, {
              "name" : "MyType_myString_MX",
              "type" : "Check",
              "clause" : "CHAR_LENGTH(\\"myString\\")<=80"
            } ]
          }, {
            "name" : "myString2",
            "type" : "VARCHAR(80) not null",
            "constraints" : [ {
              "name" : "MyType_myString2_MN",
              "type" : "Check",
              "clause" : "CHAR_LENGTH(\\"myString2\\")>=1"
            }, {
              "name" : "MyType_myString2_MX",
              "type" : "Check",
              "clause" : "CHAR_LENGTH(\\"myString2\\")<=80"
            } ]
          }, {
            "name" : "myInt",
            "type" : "INTEGER not null",
            "constraints" : [ {
              "name" : "MyType_myInt_MN",
              "type" : "Check",
              "clause" : "\\"myInt\\">=-2147483648"
            }, {
              "name" : "MyType_myInt_MX",
              "type" : "Check",
              "clause" : "\\"myInt\\"<=2147483647"
            } ]
          }, {
            "name" : "myTarget",
            "type" : "INTEGER not null",
            "constraints" : [ {
              "name" : "MyType_myTarget_MN",
              "type" : "Check",
              "clause" : "\\"myTarget\\">=0"
            }, {
              "name" : "MyType_myTarget_MX",
              "type" : "Check",
              "clause" : "\\"myTarget\\"<=2147483647"
            }, {
              "name" : "MyType_myTarget_Fk",
              "type" : "ForeignKey",
              "clause" : "myTarget->MyTarget.this"
            } ]
          } ],
          "constraints" : [ {
            "name" : "MyType_unique",
            "type" : "Unique",
            "clause" : "(\\"myString\\",\\"myString2\\")"
          } ]
        }, {
          "name" : "MyTarget",
          "columns" : [ {
            "name" : "this",
            "type" : "INTEGER not null",
            "constraints" : [ {
              "name" : "MyTarget_PK",
              "type" : "PrimaryKey"
            }, {
              "name" : "MyTarget_this_MN",
              "type" : "Check",
              "clause" : "\\"this\\">=0"
            }, {
              "name" : "MyTarget_this_MX",
              "type" : "Check",
              "clause" : "\\"this\\"<=2147483647"
            } ]
          } ]
        } ],
        "sequences" : [ {
          "name" : "MyType_myInt_Seq",
          "type" : "bit31",
          "start" : 77
        } ]
      }""",
      writeJson(get(MODEL))
    );
  }

  @Test
  void testTableMissing() throws IOException, ApiTextException {
    MODEL.getSchema().getTable(SchemaInfo.getTableName(MyType.TYPE)).drop();
    assertEquals(
      """
      {
        "name" : "MyType",
        "existence" : "missing",
        "columns" : [ {
          "name" : "this",
          "type" : "INTEGER not null",
          "constraints" : [ {
            "name" : "MyType_PK",
            "type" : "PrimaryKey"
          }, {
            "name" : "MyType_this_MN",
            "type" : "Check",
            "clause" : "\\"this\\">=0"
          }, {
            "name" : "MyType_this_MX",
            "type" : "Check",
            "clause" : "\\"this\\"<=2147483647"
          } ]
        }, {
          "name" : "myString",
          "type" : "VARCHAR(80) not null",
          "constraints" : [ {
            "name" : "MyType_myString_MN",
            "type" : "Check",
            "clause" : "CHAR_LENGTH(\\"myString\\")>=1"
          }, {
            "name" : "MyType_myString_MX",
            "type" : "Check",
            "clause" : "CHAR_LENGTH(\\"myString\\")<=80"
          } ]
        }, {
          "name" : "myString2",
          "type" : "VARCHAR(80) not null",
          "constraints" : [ {
            "name" : "MyType_myString2_MN",
            "type" : "Check",
            "clause" : "CHAR_LENGTH(\\"myString2\\")>=1"
          }, {
            "name" : "MyType_myString2_MX",
            "type" : "Check",
            "clause" : "CHAR_LENGTH(\\"myString2\\")<=80"
          } ]
        }, {
          "name" : "myInt",
          "type" : "INTEGER not null",
          "constraints" : [ {
            "name" : "MyType_myInt_MN",
            "type" : "Check",
            "clause" : "\\"myInt\\">=-2147483648"
          }, {
            "name" : "MyType_myInt_MX",
            "type" : "Check",
            "clause" : "\\"myInt\\"<=2147483647"
          } ]
        }, {
          "name" : "myTarget",
          "type" : "INTEGER not null",
          "constraints" : [ {
            "name" : "MyType_myTarget_MN",
            "type" : "Check",
            "clause" : "\\"myTarget\\">=0"
          }, {
            "name" : "MyType_myTarget_MX",
            "type" : "Check",
            "clause" : "\\"myTarget\\"<=2147483647"
          }, {
            "name" : "MyType_myTarget_Fk",
            "type" : "ForeignKey",
            "clause" : "myTarget->MyTarget.this"
          } ]
        } ],
        "constraints" : [ {
          "name" : "MyType_unique",
          "type" : "Unique",
          "clause" : "(\\"myString\\",\\"myString2\\")"
        } ]
      }""",
      writeJson(myTypeTable())
    );
  }

  private static TableResponse myTypeTable() throws ApiTextException {
    return get(MODEL).tables().get(0);
  }

  @Test
  void testTableUnused() throws IOException, ApiTextException {
    MODEL.getSchema()
      .getTable(SchemaInfo.getTableName(MyType.TYPE))
      .renameTo("MyTypeZ");
    assertEquals(
      """
      {
        "name" : "MyTypeZ",
        "existence" : "unused",
        "columns" : [ {
          "name" : "this",
          "type" : "INTEGER not null"
        }, {
          "name" : "myString",
          "type" : "VARCHAR(80) not null"
        }, {
          "name" : "myString2",
          "type" : "VARCHAR(80) not null"
        }, {
          "name" : "myInt",
          "type" : "INTEGER not null"
        }, {
          "name" : "myTarget",
          "type" : "INTEGER not null",
          "constraints" : [ {
            "name" : "MyType_myTarget_Fk",
            "type" : "ForeignKey",
            "clause" : "myTarget->MyTarget.this"
          } ]
        } ],
        "constraints" : [ {
          "name" : "MyType_PK",
          "type" : "PrimaryKey"
        }, {
          "name" : "MyType_this_MN",
          "type" : "Check",
          "clause" : "\\"this\\">=0"
        }, {
          "name" : "MyType_this_MX",
          "type" : "Check",
          "clause" : "\\"this\\"<=2147483647"
        }, {
          "name" : "MyType_myString_MN",
          "type" : "Check",
          "clause" : "CHAR_LENGTH(\\"myString\\")>=1"
        }, {
          "name" : "MyType_myString_MX",
          "type" : "Check",
          "clause" : "CHAR_LENGTH(\\"myString\\")<=80"
        }, {
          "name" : "MyType_myString2_MN",
          "type" : "Check",
          "clause" : "CHAR_LENGTH(\\"myString2\\")>=1"
        }, {
          "name" : "MyType_myString2_MX",
          "type" : "Check",
          "clause" : "CHAR_LENGTH(\\"myString2\\")<=80"
        }, {
          "name" : "MyType_myInt_MN",
          "type" : "Check",
          "clause" : "\\"myInt\\">=-2147483648"
        }, {
          "name" : "MyType_myInt_MX",
          "type" : "Check",
          "clause" : "\\"myInt\\"<=2147483647"
        }, {
          "name" : "MyType_myTarget_MN",
          "type" : "Check",
          "clause" : "\\"myTarget\\">=0"
        }, {
          "name" : "MyType_myTarget_MX",
          "type" : "Check",
          "clause" : "\\"myTarget\\"<=2147483647"
        }, {
          "name" : "MyType_unique",
          "type" : "Unique",
          "clause" : "(\\"myString\\",\\"myString2\\")"
        } ]
      }""",
      writeJson(get(MODEL).tables().get(2))
    );
  }

  @Test
  void testColumnMissing() throws IOException, ApiTextException {
    final Table table = MODEL.getSchema().getTable(
      SchemaInfo.getTableName(MyType.TYPE)
    );
    table.getConstraint("MyType_unique").drop();
    table.getColumn("myString").drop();
    assertEquals(
      """
      {
        "name" : "myString",
        "type" : "VARCHAR(80) not null",
        "existence" : "missing",
        "constraints" : [ {
          "name" : "MyType_myString_MN",
          "type" : "Check",
          "clause" : "CHAR_LENGTH(\\"myString\\")>=1",
          "error" : {
            "existence" : "missing"
          }
        }, {
          "name" : "MyType_myString_MX",
          "type" : "Check",
          "clause" : "CHAR_LENGTH(\\"myString\\")<=80",
          "error" : {
            "existence" : "missing"
          }
        } ]
      }""",
      writeJson(myStringColumn())
    );
  }

  @Test
  void testColumnUnexpectedType()
    throws IOException, SQLException, ApiTextException {
    final Table table = MODEL.getSchema().getTable(
      SchemaInfo.getTableName(MyType.TYPE)
    );
    table.getConstraint("MyType_unique").drop();
    table.getColumn("myString").drop();
    execute(
      "ALTER TABLE \"MyType\" ADD COLUMN \"myString\" VARCHAR(44) not null"
    );
    execute(
      "ALTER TABLE \"MyType\" ADD CONSTRAINT \"MyType_myString_MN\" CHECK (CHAR_LENGTH(\"myString\")>=1)"
    );
    execute(
      "ALTER TABLE \"MyType\" ADD CONSTRAINT \"MyType_myString_MX\" CHECK (CHAR_LENGTH(\"myString\")<=80)"
    );
    assertEquals(
      """
      {
        "name" : "myString",
        "type" : "VARCHAR(80) not null",
        "errorType" : "VARCHAR(44) not null",
        "constraints" : [ {
          "name" : "MyType_myString_MN",
          "type" : "Check",
          "clause" : "CHAR_LENGTH(\\"myString\\")>=1"
        }, {
          "name" : "MyType_myString_MX",
          "type" : "Check",
          "clause" : "CHAR_LENGTH(\\"myString\\")<=80"
        } ]
      }""",
      writeJson(myStringColumn())
    );
  }

  private static ColumnResponse myStringColumn() throws ApiTextException {
    return myTypeTable().columns().get(1);
  }

  @Test
  void testColumnUnused() throws IOException, SQLException, ApiTextException {
    execute(
      "ALTER TABLE \"MyType\" ADD COLUMN \"myStringZ\" VARCHAR(80) not null"
    );
    assertEquals(
      """
      {
        "name" : "myStringZ",
        "type" : "VARCHAR(80) not null",
        "existence" : "unused"
      }""",
      writeJson(myTypeTable().columns().get(5))
    );
  }

  @Test
  void testColumnUnusedOptional()
    throws IOException, SQLException, ApiTextException {
    execute("ALTER TABLE \"MyType\" ADD COLUMN \"myStringZ\" VARCHAR(80)");
    assertEquals(
      """
      {
        "name" : "myStringZ",
        "type" : "VARCHAR(80)",
        "existence" : "unused",
        "toleratesInsertIfUnused" : true
      }""",
      writeJson(myTypeTable().columns().get(5))
    );
  }

  @Test
  void testConstraintMissing() throws IOException, ApiTextException {
    MODEL.getSchema()
      .getTable(SchemaInfo.getTableName(MyType.TYPE))
      .getConstraint("MyType_this_MN")
      .drop();
    assertEquals(
      """
      {
        "name" : "MyType_this_MN",
        "type" : "Check",
        "clause" : "\\"this\\">=0",
        "error" : {
          "existence" : "missing"
        }
      }""",
      writeJson(thisMaxConstraint())
    );
  }

  @Test
  void testConstraintUnexpectedType()
    throws IOException, SQLException, ApiTextException {
    final Table table = MODEL.getSchema().getTable(
      SchemaInfo.getTableName(MyType.TYPE)
    );
    table.getConstraint("MyType_this_MN").drop();
    table.getConstraint("MyType_PK").drop();
    execute(
      "ALTER TABLE \"MyType\" ADD CONSTRAINT \"MyType_this_MN\" UNIQUE (\"this\")"
    );
    // TODO should complain explicitly about non-matching type
    assertEquals(
      """
      {
        "name" : "MyType_this_MN",
        "type" : "Check",
        "clause" : "\\"this\\">=0",
        "error" : {
          "clause" : "(\\"this\\")"
        }
      }""",
      writeJson(thisMaxConstraint())
    );
  }

  @Test
  void testConstraintUnexpectedClause()
    throws IOException, SQLException, ApiTextException {
    MODEL.getSchema()
      .getTable(SchemaInfo.getTableName(MyType.TYPE))
      .getConstraint("MyType_this_MN")
      .drop();
    execute(
      "ALTER TABLE \"MyType\" ADD CONSTRAINT \"MyType_this_MN\" CHECK (\"this\">=44)"
    );
    assertEquals(
      """
      {
        "name" : "MyType_this_MN",
        "type" : "Check",
        "clause" : "\\"this\\">=0",
        "error" : {
          "clause" : "\\"this\\">=44"
        }
      }""",
      writeJson(thisMaxConstraint())
    );
  }

  @Test
  void testConstraintUnexpectedClauseWithRaw()
    throws IOException, SQLException, ApiTextException {
    MODEL.getSchema()
      .getTable(SchemaInfo.getTableName(MyType.TYPE))
      .getConstraint("MyType_this_MN")
      .drop();
    execute(
      "ALTER TABLE \"MyType\" ADD CONSTRAINT \"MyType_this_MN\" CHECK (\"this\"<>44)"
    );
    assertEquals(
      """
      {
        "name" : "MyType_this_MN",
        "type" : "Check",
        "clause" : "\\"this\\">=0",
        "error" : {
          "clause" : "\\"this\\"<>44",
          "clauseRaw" : "\\"this\\"!=44"
        }
      }""",
      writeJson(thisMaxConstraint())
    );
  }

  private static Object thisMaxConstraint() throws ApiTextException {
    return myTypeTable().columns().get(0).constraints().get(1);
  }

  @Test
  void testConstraintRemainingError()
    throws IOException, SQLException, ApiTextException {
    MODEL.getSchema()
      .getTable(SchemaInfo.getTableName(MyType.TYPE))
      .getConstraint("MyType_myTarget_Fk")
      .drop();
    execute(
      "ALTER TABLE \"MyType\" " +
      "ADD CONSTRAINT \"MyType_myTarget_Fk\" " +
      "FOREIGN KEY (\"myTarget\") REFERENCES \"MyTarget\" " +
      "ON DELETE CASCADE"
    );
    assertEquals(
      """
      {
        "name" : "MyType_myTarget_Fk",
        "type" : "ForeignKey",
        "clause" : "myTarget->MyTarget.this",
        "error" : {
          "remainder" : [ "unexpected delete rule CASCADE" ]
        }
      }""",
      writeJson(myTypeTable().columns().get(4).constraints().get(2))
    );
  }

  @Test
  void testConstraintRemainingErrorMultiple()
    throws IOException, SQLException, ApiTextException {
    MODEL.getSchema()
      .getTable(SchemaInfo.getTableName(MyType.TYPE))
      .getConstraint("MyType_myTarget_Fk")
      .drop();
    execute(
      "ALTER TABLE \"MyType\" " +
      "ADD CONSTRAINT \"MyType_myTarget_Fk\" " +
      "FOREIGN KEY (\"myTarget\") REFERENCES \"MyTarget\" " +
      "ON DELETE CASCADE ON UPDATE CASCADE"
    );
    assertEquals(
      """
      {
        "name" : "MyType_myTarget_Fk",
        "type" : "ForeignKey",
        "clause" : "myTarget->MyTarget.this",
        "error" : {
          "remainder" : [ "unexpected delete rule CASCADE", "unexpected update rule CASCADE" ]
        }
      }""",
      writeJson(myTypeTable().columns().get(4).constraints().get(2))
    );
  }

  @Test
  void testConstraintRemainingErrorAndClause()
    throws IOException, SQLException, ApiTextException {
    MODEL.getSchema()
      .getTable(SchemaInfo.getTableName(MyType.TYPE))
      .getConstraint("MyType_myTarget_Fk")
      .drop();
    execute(
      "ALTER TABLE \"MyType\" " +
      "ADD CONSTRAINT \"MyType_myTarget_Fk\" " +
      "FOREIGN KEY (\"myInt\") REFERENCES \"MyTarget\" " +
      "ON DELETE CASCADE ON UPDATE CASCADE"
    );
    assertEquals(
      """
      {
        "name" : "MyType_myTarget_Fk",
        "type" : "ForeignKey",
        "clause" : "myTarget->MyTarget.this",
        "error" : {
          "clause" : "myInt->MyTarget.this",
          "remainder" : [ "unexpected delete rule CASCADE", "unexpected update rule CASCADE" ]
        }
      }""",
      writeJson(myTypeTable().columns().get(4).constraints().get(2))
    );
  }

  @Test
  void testConstraintUnused()
    throws IOException, SQLException, ApiTextException {
    execute(
      "ALTER TABLE \"MyType\" ADD CONSTRAINT \"MyType_this_MZ\" CHECK (\"this\">=44)"
    );
    assertEquals(
      """
      {
        "name" : "MyType_this_MZ",
        "type" : "Check",
        "clause" : "\\"this\\">=44",
        "error" : {
          "existence" : "unused"
        }
      }""",
      writeJson(myTypeTable().constraints().get(1))
    );
  }

  @Test
  void testSequenceMissing() throws IOException, ApiTextException {
    MODEL.getSchema()
      .getSequence(SchemaInfo.getDefaultToNextSequenceName(MyType.myInt))
      .drop();
    assertEquals(
      """
      {
        "name" : "MyType_myInt_Seq",
        "type" : "bit31",
        "start" : 77,
        "error" : {
          "existence" : "missing"
        }
      }""",
      writeJson(myIntSequence())
    );
  }

  @Test
  void testSequenceUnexpectedType()
    throws IOException, SQLException, ApiTextException {
    MODEL.getSchema()
      .getSequence(SchemaInfo.getDefaultToNextSequenceName(MyType.myInt))
      .drop();
    execute(
      "CREATE SEQUENCE \"MyType_myInt_Seq\" AS BIGINT START WITH 77 INCREMENT BY 1"
    );
    assertEquals(
      """
      {
        "name" : "MyType_myInt_Seq",
        "type" : "bit31",
        "start" : 77,
        "error" : {
          "type" : "bit63"
        }
      }""",
      writeJson(myIntSequence())
    );
  }

  @Test
  void testSequenceUnexpectedStart()
    throws IOException, SQLException, ApiTextException {
    MODEL.getSchema()
      .getSequence(SchemaInfo.getDefaultToNextSequenceName(MyType.myInt))
      .drop();
    execute(
      "CREATE SEQUENCE \"MyType_myInt_Seq\" AS INTEGER START WITH 88 INCREMENT BY 1"
    );
    assertEquals(
      """
      {
        "name" : "MyType_myInt_Seq",
        "type" : "bit31",
        "start" : 77,
        "error" : {
          "start" : 88
        }
      }""",
      writeJson(myIntSequence())
    );
  }

  @Test
  void testSequenceUnexpectedTypeAndStart()
    throws IOException, SQLException, ApiTextException {
    MODEL.getSchema()
      .getSequence(SchemaInfo.getDefaultToNextSequenceName(MyType.myInt))
      .drop();
    execute(
      "CREATE SEQUENCE \"MyType_myInt_Seq\" AS BIGINT START WITH 88 INCREMENT BY 1"
    );
    assertEquals(
      """
      {
        "name" : "MyType_myInt_Seq",
        "type" : "bit31",
        "start" : 77,
        "error" : {
          "type" : "bit63",
          "start" : 88
        }
      }""",
      writeJson(myIntSequence())
    );
  }

  private static Object myIntSequence() throws ApiTextException {
    return get(MODEL).sequences().get(0);
  }

  @Test
  void testSequenceUnused() throws IOException, SQLException, ApiTextException {
    execute(
      "CREATE SEQUENCE \"MyType_myIntZ_Seq\" AS INTEGER START WITH 77 INCREMENT BY 1"
    );
    assertEquals(
      """
      {
        "name" : "MyType_myIntZ_Seq",
        "type" : "bit31",
        "start" : 77,
        "error" : {
          "existence" : "unused"
        }
      }""",
      writeJson(get(MODEL).sequences().get(1))
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
  void tearDownSchema() throws SQLException {
    MODEL.rollbackIfNotCommitted();
    execute("DROP TABLE IF EXISTS \"MyTypeZ\"");
    MODEL.tearDownSchema();
  }

  private static void execute(final String sql) throws SQLException {
    //noinspection UnnecessarySemicolon OK: prettier
    try (
      Connection con = SchemaInfo.newConnection(MODEL);
      Statement stmt = con.createStatement();
    ) {
      stmt.execute(sql);
    }
  }
}
