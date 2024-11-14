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

import static com.exedio.cope.CopeHack.newSchemaSavepointNotAvailable;
import static com.exedio.cope.console.ChecklistIcon.error;
import static com.exedio.cope.console.ChecklistIcon.ok;
import static com.exedio.cope.console.ChecklistIcon.unknown;
import static com.exedio.cope.console.SavepointCop.getChecklistIcon;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.console.SavepointCop.Point;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class SavepointChecklistIconTest
{
	@Test void testEmpty()
	{
		assertEquals(unknown, icon());
	}
	@Test void testOneSuccess()
	{
		assertEquals(unknown, icon(
				new Point("ok")));
	}
	@Test void testOneFailure()
	{
		assertEquals(error, icon(
				new Point(newSchemaSavepointNotAvailable())));
	}
	@Test void testTwoSuccessesEquals()
	{
		assertEquals(unknown, icon(
				new Point("ok"),
				new Point("ok")));
	}
	@Test void testTwoSuccessesDistinct()
	{
		assertEquals(ok, icon(
				new Point("ok1"),
				new Point("ok2")));
	}
	@Test void testTwoFailures()
	{
		assertEquals(error, icon(
				new Point(newSchemaSavepointNotAvailable()),
				new Point(newSchemaSavepointNotAvailable())));
	}
	@Test void testOneFailureOneSuccess()
	{
		assertEquals(error, icon(
				new Point("ok"),
				new Point(newSchemaSavepointNotAvailable())));
	}
	@Test void testOneSuccessOneFailure()
	{
		assertEquals(unknown, icon(
				new Point(newSchemaSavepointNotAvailable()),
				new Point("ok")));
	}
	@Test void testManySuccessesEquals()
	{
		assertEquals(unknown, icon(
				new Point("ok"),
				new Point("ok"),
				new Point("ok"),
				new Point("ok"),
				new Point("ok")));
	}
	@Test void testManySuccessesDistinct()
	{
		assertEquals(ok, icon(
				new Point("okOther"),
				new Point("ok"),
				new Point("ok"),
				new Point("ok"),
				new Point("ok")));
	}
	@Test void testManySuccessesDistinctButFailure()
	{
		assertEquals(unknown, icon(
				new Point("okOther"),
				new Point("ok"),
				new Point("ok"),
				new Point(newSchemaSavepointNotAvailable()),
				new Point("ok")));
	}
	@Test void testManySuccessesDistinctButFailureLast()
	{
		assertEquals(error, icon(
				new Point("okOther"),
				new Point("ok"),
				new Point("ok"),
				new Point("ok"),
				new Point(newSchemaSavepointNotAvailable())));
	}


	private static ChecklistIcon icon(final Point... points)
	{
		return getChecklistIcon(new ArrayList<>(Arrays.asList(points)));
	}
}
