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

final class MediaTestableCop extends TestCop<MediaTestable>
{
	static final String TAB = "mediatestable";

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
	List<MediaTestable> getItems()
	{
		final ArrayList<MediaTestable> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaTestable)
					result.add((MediaTestable)feature);

		return result;
	}

	@Override
	List<Column<MediaTestable>> columns()
	{
		return COLUMNS;
	}

	private static final List<Column<MediaTestable>> COLUMNS = List.of(
			column("Type",  testable -> ((Feature)testable).getType().getID()),
			column("Name",  testable -> ((Feature)testable).getName()),
			column("Class", (out, testable) -> out.write(testable.getClass()))
	);

	@Override
	String getID(final MediaTestable testable)
	{
		return ((Feature)testable).getID();
	}

	@Override
	MediaTestable forID(final String id)
	{
		return (MediaTestable)app.model.getFeature(id);
	}

	@Override
	long check(final MediaTestable testable)
	{
		try
		{
			testable.test();
		}
		catch(final Exception e)
		{
			throw new RuntimeException(e);
		}
		return 0;
	}
}
