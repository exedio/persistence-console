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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Cope;
import com.exedio.cope.EnvironmentInfo;
import com.exedio.cope.Model;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

final class EnvironmentCop extends ConsoleCop<Void>
{
	static final String TAB = "environment";

	EnvironmentCop(final Args args)
	{
		super(TAB, "Environment", args);
	}

	@Override
	protected EnvironmentCop newArgs(final Args args)
	{
		return new EnvironmentCop(args);
	}

	private static String replaceNull(final String s)
	{
		return (s==null) ? "n/a" : s;
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = out.model;
		final EnvironmentInfo env = model.getEnvironmentInfo();
		final java.util.Properties current = env.asProperties();
		for(final String name : current.stringPropertyNames())
			current.setProperty(name, replaceNull(current.getProperty(name)));

		Environment_Jspm.writeCurrent(out,
				env.getCatalog(),
				model.getConnectProperties().getDialect(),
				current);
		Environment_Jspm.writeTest(out, current, makeTestedDatabases());
	}

	@SuppressFBWarnings({"WMI_WRONG_MAP_ITERATOR", "ITA_INEFFICIENT_TO_ARRAY"})
	private static HashMap<String, Object>[] makeTestedDatabases()
	{
		final Properties p = new Properties();
		try(InputStream in = Cope.class.getResourceAsStream("testprotocol.properties"))
		{
			if(in==null)
				return null;

			p.load(in);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}

		final TreeMap<String, HashMap<String, Object>> testedDatabases = new TreeMap<>();
		for(final String name : p.stringPropertyNames())
		{
			final String value = replaceNull(p.getProperty(name));

			final int nameDot = name.indexOf('.');
			if(nameDot<=0)
				throw new RuntimeException(name);

			final String databaseName = name.substring(0, nameDot);
			HashMap<String, Object> database = testedDatabases.get(databaseName);
			if(database==null)
			{
				database = new HashMap<>();
				database.put("name", databaseName);
				testedDatabases.put(databaseName, database);
			}

			final String key = name.substring(nameDot+1);
			if(key.startsWith("cope."))
			{
				TreeMap<String, String> previousValue = castTreeMap(database.get(TEST_INFO_KEY_CONNECT_PROPERTIES));
				if(previousValue==null)
				{
					previousValue = new TreeMap<>();
					database.put(TEST_INFO_KEY_CONNECT_PROPERTIES, previousValue);
				}
				previousValue.put(key.substring("cope.".length()), value);
			}
			else
				database.put(key, value);
		}

		@SuppressWarnings({"rawtypes", "unchecked", "ZeroLengthArrayAllocation"})
		final HashMap<String, Object>[] result = testedDatabases.values().toArray(new HashMap[0]);
		return result;
	}

	@SuppressWarnings("unchecked") // TODO testedDatabases contains Strings and Maps
	private static TreeMap<String, String> castTreeMap(final Object o)
	{
		return (TreeMap<String, String>)o;
	}

	static final String TEST_INFO_KEY_CONNECT_PROPERTIES = "connect.properties";

	public static void main(final String[] args)
	{
		try(FileOutputStream out = new FileOutputStream(args[0]))
		{
			Environment_Jspm.writeTestBody(
					new OutBasic(new PrintStream(out, false, UTF_8.name())),
					new java.util.Properties(),
					requireNonNull(makeTestedDatabases()));
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
