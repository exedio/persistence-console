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

import com.exedio.cope.Feature;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.MediaTestable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

final class MediaTestableCop extends TestCop<MediaTestableCop.Test>
{
	static final String TAB = "mediatestable";

	record Test(MediaTestable testable, int index)
	{
		Type<?> getType()
		{
			return testable.getType();
		}

		String getName()
		{
			return testable.getName();
		}

		String getID()
		{
			return testable.getID() + SEPARATOR + index;
		}

		Callable<?> getTest()
		{
			return testable.getTests().get(index);
		}
	}

	MediaTestableCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Media Testables", args, testArgs);
	}

	@Override
	protected MediaTestableCop newArgs(final Args args)
	{
		return new MediaTestableCop(args, testArgs);
	}

	@Override
	protected MediaTestableCop newTestArgs(final TestArgs testArgs)
	{
		return new MediaTestableCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Runs MediaTestable#test on all features of the model implementing MediaTestable.",
			"IMPACT: " +
				"A failure here means that the affected feature may fail to work. " +
				"In particular, MediaServlet may repond with code 500 Internal Server Error. " +
				"DANGER ZONE."
		};
	}

	@Override
	List<Test> getItems()
	{
		final ArrayList<Test> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof final MediaTestable testable)
				{
					int index = 0;
					for(final var ignored : testable.getTests())
						result.add(new Test(testable, index++));
				}

		return result;
	}

	@Override
	List<Column<Test>> columns()
	{
		return COLUMNS;
	}

	@SuppressWarnings("Convert2MethodRef") // OK: better readable together with the other lines
	private static final List<Column<Test>> COLUMNS = List.of(
			column("Type",  testable -> testable.getType().getID()),
			column("Name",  testable -> testable.getName()),
			column("Class", (out, testable) -> out.write(testable.testable.getClass())),
			column("Test",  testable -> testable.getTest().toString())
	);

	@Override
	String getID(final Test test)
	{
		return test.getID();
	}

	@Override
	Test forID(final String id)
	{
		final var split = id.split(SEPARATOR);
		return new Test((MediaTestable)app.model.getFeature(split[0]), Integer.parseInt(split[1]));
	}

	private static final String SEPARATOR = "#";

	@Override
	long check(final Test test)
	{
		try
		{
			test.getTest().call();
		}
		catch(final Exception e)
		{
			throw new RuntimeException(e);
		}
		return 0;
	}
}
