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

import static com.exedio.cope.SetValue.map;
import static com.exedio.cope.console.ApiTest.writeJson;
import static com.exedio.cope.console.MetricsApi.get;
import static com.exedio.cope.console.SchemaGetApiTest.assertOrphaned;
import static com.exedio.cope.junit.CopeAssert.assertFails;
import static io.micrometer.core.instrument.Metrics.globalRegistry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.util.Sources;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MetricsApiTest {

  @Test
  void testItemCache() throws IOException, ApiTextException {
    assertEquals(
      """
      [ {
        "name" : "com.exedio.cope.ItemCache.concurrentLoad",
        "description" : "How often an item was loaded concurrently",
        "tags" : {
          "type" : "MyType"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.ItemCache.evictions",
        "description" : "Evictions in the item cache, as 'size' exceeded 'maximumSize'.",
        "tags" : {
          "type" : "MyType"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.ItemCache.gets",
        "description" : "The number of times cache lookup methods have returned a cached value.",
        "tags" : {
          "result" : "hit",
          "type" : "MyType"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.ItemCache.gets",
        "description" : "The number of times cache lookup methods have returned an uncached (newly loaded) value",
        "tags" : {
          "result" : "miss",
          "type" : "MyType"
        },
        "count" : 2.0
      }, {
        "name" : "com.exedio.cope.ItemCache.invalidations",
        "description" : "Invalidations in the item cache, that were effective because the item was in cache",
        "tags" : {
          "effect" : "actual",
          "type" : "MyType"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.ItemCache.invalidations",
        "description" : "Invalidations in the item cache, that were futile because the item was not in cache",
        "tags" : {
          "effect" : "futile",
          "type" : "MyType"
        },
        "count" : 6.0
      }, {
        "name" : "com.exedio.cope.ItemCache.stamp.hit",
        "description" : "How often a stamp prevented an item from being stored",
        "tags" : {
          "type" : "MyType"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.ItemCache.stamp.purge",
        "description" : "How many stamps were purged because there was no transaction older than the stamp",
        "tags" : {
          "type" : "MyType"
        },
        "count" : 6.0
      } ]""",
      writeJson(get(MODEL, request("com.exedio.cope.ItemCache")))
    );
  }

  @Test
  void testUniqueConstraint() throws IOException, ApiTextException {
    assertEquals(
      """
      [ ]""",
      writeJson(get(MODEL, request("com.exedio.cope.UniqueConstraint")))
    );
  }

  @Test
  void testQueryCache() throws IOException, ApiTextException {
    assertEquals(
      """
      [ {
        "name" : "com.exedio.cope.QueryCache.concurrentLoad",
        "description" : "How often a query was loaded concurrently",
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.QueryCache.evictions",
        "description" : "Evictions in the query cache, as 'size' exceeded 'maximumSize'.",
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.QueryCache.gets",
        "description" : "The number of times cache lookup methods have returned a cached value.",
        "tags" : {
          "result" : "hit"
        },
        "count" : 22.0
      }, {
        "name" : "com.exedio.cope.QueryCache.gets",
        "description" : "The number of times cache lookup methods have returned an uncached (newly loaded) value",
        "tags" : {
          "result" : "miss"
        },
        "count" : 1.0
      }, {
        "name" : "com.exedio.cope.QueryCache.invalidations",
        "description" : "Invalidations in the query cache",
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.QueryCache.stamp.hit",
        "description" : "How often a stamp prevented a query from being stored",
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.QueryCache.stamp.purge",
        "description" : "How many stamps were purged because there was no transaction older than the stamp",
        "count" : 1.0
      } ]""",
      writeJson(get(MODEL, request("com.exedio.cope.QueryCache")))
    );
  }

  @Test
  void testMediaPath() throws IOException, ApiTextException {
    assertEquals(
      """
      [ {
        "name" : "com.exedio.cope.pattern.MediaPath.failure",
        "description" : "An exception occurred while processing the request (500)",
        "tags" : {
          "feature" : "MyType.myMedia"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.pattern.MediaPath.moved",
        "description" : "Moved (301)",
        "tags" : {
          "cause" : "canonize",
          "feature" : "MyType.myMedia"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.pattern.MediaPath.notFound",
        "description" : "Not Found (404)",
        "tags" : {
          "cause" : "invalidSpecial",
          "feature" : "MyType.myMedia"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.pattern.MediaPath.notFound",
        "description" : "Not Found (404)",
        "tags" : {
          "cause" : "isNull",
          "feature" : "MyType.myMedia"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.pattern.MediaPath.notFound",
        "description" : "Not Found (404)",
        "tags" : {
          "cause" : "noSuchItem",
          "feature" : "MyType.myMedia"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.pattern.MediaPath.notFound",
        "description" : "Not Found (404)",
        "tags" : {
          "cause" : "noSuchPath",
          "feature" : "NONE"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.pattern.MediaPath.notFound",
        "description" : "Not Found (404)",
        "tags" : {
          "cause" : "notAnItem",
          "feature" : "MyType.myMedia"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.pattern.MediaPath.notModified",
        "description" : "Not Modified (304) response with empty body. Happens if Last-Modified did not change since last request.",
        "tags" : {
          "feature" : "MyType.myMedia"
        },
        "count" : 0.0
      }, {
        "name" : "com.exedio.cope.pattern.MediaPath.ok",
        "description" : "Responded successfully (200)",
        "tags" : {
          "feature" : "MyType.myMedia"
        },
        "count" : 0.0
      } ]""",
      writeJson(get(MODEL, request("com.exedio.cope.pattern.MediaPath")))
    );
  }

  @Test
  void testPrefixViolated() {
    final var request = request("com.exedio.copx");
    assertFails(
      () -> get(MODEL, request),
      ApiTextException.class,
      "404 prefix must start with com.exedio.cope"
    );
  }

  private static ParameterRequest request(final String prefix) {
    return new ParameterRequest(Map.of("prefix", prefix));
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final StringField myString = new StringField().unique().toFinal();

    @UsageEntryPoint
    static final Media myMedia = new Media().optional().toFinal();

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<MyType> TYPE = TypesBound.newType(
      MyType.class,
      MyType::new
    );

    private MyType(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final Model MODEL = new Model(MyType.TYPE);

  @BeforeAll
  static void connect() {
    final java.util.Properties props = new java.util.Properties();
    props.setProperty(
      "connection.url",
      "jdbc:hsqldb:mem:copeconsoletestmetrics"
    );
    props.setProperty("connection.username", "sa");
    props.setProperty("connection.password", "");
    MODEL.connect(
      assertOrphaned(ConnectProperties.create(Sources.view(props, "DESC")))
    );
    MODEL.createSchema();
    final MyType item1, item2;
    try (var tx = MODEL.startTransactionTry(MetricsApiTest.class.getName())) {
      item1 = MyType.TYPE.newItem(map(MyType.myString, "myStringValue1"));
      item2 = MyType.TYPE.newItem(map(MyType.myString, "myStringValue2"));
      MyType.TYPE.newItem(map(MyType.myString, "myStringValue3"));
      MyType.TYPE.newItem(map(MyType.myString, "myStringValue4"));
      MyType.TYPE.newItem(map(MyType.myString, "myStringValue5"));
      MyType.TYPE.newItem(map(MyType.myString, "myStringValue6"));
      tx.commit();
    }
    try (var tx = MODEL.startTransactionTry(MetricsApiTest.class.getName())) {
      assertEquals("myStringValue1", MyType.myString.get(item1)); // load into item cache
      assertEquals("myStringValue2", MyType.myString.get(item2)); // load into item cache
      tx.commit();
    }
    try (var tx = MODEL.startTransactionTry(MetricsApiTest.class.getName())) {
      for (int i = 0; i < 23; i++) MyType.myString.searchUnique(
        MyType.class,
        "myStringValueXXX"
      ); // miss in unique cache
      tx.commit();
    }
  }

  @AfterAll
  static void disconnect() {
    MODEL.disconnect();
  }

  static {
    globalRegistry.add(new SimpleMeterRegistry());
  }
}
