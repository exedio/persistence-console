/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.util.Sources;
import java.util.Set;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public final class ConnectRule implements AfterEachCallback, ParameterResolver {

  private ConnectRule() {
    // just make private
  }

  private Model model;

  public void connectWithoutCreate(final Model model) {
    assertNotNull(model);
    assertNull(this.model);

    this.model = model;

    final java.util.Properties props = new java.util.Properties();
    props.setProperty("connection.url", "jdbc:hsqldb:mem:copeconsoletest");
    props.setProperty("connection.username", "sa");
    props.setProperty("connection.password", "");
    props.setProperty("schema.primaryKeyGenerator", "sequence");
    model.connect(
      assertOrphaned(ConnectProperties.create(Sources.view(props, "DESC")))
    );
  }

  public void connect(final Model model) {
    connectWithoutCreate(model);
    model.createSchema();
  }

  private static ConnectProperties assertOrphaned(
    final ConnectProperties properties
  ) {
    assertEquals(Set.of(), properties.getOrphanedKeys());
    return properties;
  }

  @Override
  public void afterEach(final ExtensionContext context) {
    if (model != null) {
      model.rollbackIfNotCommitted();
      model.tearDownSchema();
      model.disconnect();
      model = null;
    }
  }

  @Override
  public boolean supportsParameter(
    final ParameterContext parameterContext,
    final ExtensionContext extensionContext
  ) {
    return (ConnectRule.class == parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(
    final ParameterContext parameterContext,
    final ExtensionContext extensionContext
  ) {
    return this;
  }
}
