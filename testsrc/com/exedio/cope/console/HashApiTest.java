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

import static com.exedio.cope.console.ApiTest.readJson;
import static com.exedio.cope.console.ApiTest.writeJson;
import static com.exedio.cope.console.HashApi.get;
import static com.exedio.cope.console.HashApi.hash;
import static com.exedio.cope.junit.CopeAssert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.console.HashApi.HashRequest;
import com.exedio.cope.pattern.Hash;
import com.exedio.cope.pattern.HashAlgorithm;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class HashApiTest {

  @Test
  void testGet() throws IOException {
    assertEquals(
      """
      [ {
        "type" : "MyType",
        "name" : "myName",
        "plainTextLimit" : 777,
        "algorithmID" : "myID",
        "algorithmDescription" : "myDescription"
      }, {
        "type" : "MyType",
        "name" : "myValidated",
        "plainTextLimit" : 150,
        "plainTextValidator" : "MyPlainTextValidator",
        "algorithmID" : "myID",
        "algorithmDescription" : "myDescription"
      } ]""",
      writeJson(get(MODEL))
    );
  }

  @Test
  void testHash() throws IOException, ApiTextException {
    final HashRequest request = readJson(
      HashRequest.class,
      """
      {
        "type": "MyType",
        "name": "myName",
        "plainText": "myPlainText"
      }
      """
    );
    // prettier-ignore
    assertEquals(
      "HashRequest[" +
        "type=MyType, " +
        "name=myName, " +
        "plainText=myPlainText" +
      "]",
      request.toString()
    );

    assertEquals(
      """
      {
        "hash" : "[[myPlainText]]",
        "elapsedNanos" : NNN
      }""",
      writeJson(hash(MODEL, request)).replaceAll("[0-9]+", "NNN")
    );
  }

  @Test
  void testHash404Type() {
    final HashApi.HashRequest request = new HashRequest(
      "MyTypeX",
      "myNameX",
      "myPlainText"
    );
    assertFails(
      () -> hash(MODEL, request),
      ApiTextException.class,
      "404 type not found within " + MODEL
    );
  }

  @Test
  void testHash404Name() {
    final HashRequest request = new HashApi.HashRequest(
      "MyType",
      "myNameX",
      "myPlainText"
    );
    assertFails(
      () -> hash(MODEL, request),
      ApiTextException.class,
      "404 name not found within " + MODEL
    );
  }

  @Test
  void testHash404NoHash() {
    final HashApi.HashRequest request = new HashApi.HashRequest(
      "MyType",
      "noHash",
      "myPlainText"
    );
    assertFails(
      () -> hash(MODEL, request),
      ApiTextException.class,
      "404 name not a com.exedio.cope.pattern.Hash within " + MODEL
    );
  }

  @Test
  void testHashNoType() {
    assertFails(
      () ->
        readJson(
          HashApi.HashRequest.class,
          """
          {
            "name": "myName",
            "plainText": "myPlainText"
          }
          """
        ),
      ApiTextException.class,
      "400 Missing required creator property 'type' (index 0) / " +
      "line: 4, column: 1 / " +
      "com.exedio.cope.console.HashApi$HashRequest[\"type\"]"
    );
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final Hash myName = new Hash(new MyHashAlgorithm()).limit(777);

    @UsageEntryPoint
    static final Hash myValidated = new Hash(new MyHashAlgorithm()).validate(
      new MyPlainTextValidator()
    );

    @UsageEntryPoint
    static final StringField noHash = new StringField();

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(MyType.class, MyType::new);

    private MyType(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static class MyHashAlgorithm implements HashAlgorithm {

    @Override
    public String getID() {
      return "myID";
    }

    @Override
    public String getDescription() {
      return "myDescription";
    }

    @Override
    public StringField constrainStorage(final StringField storage) {
      return storage;
    }

    @Override
    public String hash(final String plainText) {
      return "[[" + plainText + "]]";
    }

    @Override
    public boolean check(final String plainText, final String hash) {
      throw new AssertionFailedError();
    }
  }

  private static class MyPlainTextValidator extends Hash.PlainTextValidator {

    @Override
    protected void validate(
      final String plainText,
      final Item exceptionItem,
      final Hash hash
    ) throws Hash.InvalidPlainTextException {
      throw new AssertionFailedError();
    }

    @Override
    public String toString() {
      return "MyPlainTextValidator";
    }
  }

  private static final Model MODEL = new Model(MyType.TYPE);
}
