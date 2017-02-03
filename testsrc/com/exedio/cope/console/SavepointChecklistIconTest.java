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

import static com.exedio.cope.console.ChecklistIcon.error;
import static com.exedio.cope.console.ChecklistIcon.ok;
import static com.exedio.cope.console.ChecklistIcon.unknown;
import static com.exedio.cope.console.SavepointCop.getChecklistIcon;

import com.exedio.cope.console.SavepointCop.Point;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;

public class SavepointChecklistIconTest extends TestCase
{
	public void testEmpty()
	{
		assertEquals(unknown, icon());
	}
	public void testOneSuccess()
	{
		assertEquals(unknown, icon(
				new Point("ok")));
	}
	public void testOneFailure()
	{
		assertEquals(error, icon(
				new Point(new SQLException())));
	}
	public void testTwoSuccessesEquals()
	{
		assertEquals(unknown, icon(
				new Point("ok"),
				new Point("ok")));
	}
	public void testTwoSuccessesDistinct()
	{
		assertEquals(ok, icon(
				new Point("ok1"),
				new Point("ok2")));
	}
	public void testTwoFailures()
	{
		assertEquals(error, icon(
				new Point(new SQLException()),
				new Point(new SQLException())));
	}
	public void testOneFailureOneSuccess()
	{
		assertEquals(error, icon(
				new Point("ok"),
				new Point(new SQLException())));
	}
	public void testOneSuccessOneFailure()
	{
		assertEquals(unknown, icon(
				new Point(new SQLException()),
				new Point("ok")));
	}
	public void testManySuccessesEquals()
	{
		assertEquals(unknown, icon(
				new Point("ok"),
				new Point("ok"),
				new Point("ok"),
				new Point("ok"),
				new Point("ok")));
	}
	public void testManySuccessesDistinct()
	{
		assertEquals(ok, icon(
				new Point("okOther"),
				new Point("ok"),
				new Point("ok"),
				new Point("ok"),
				new Point("ok")));
	}
	public void testManySuccessesDistinctButFailure()
	{
		assertEquals(unknown, icon(
				new Point("okOther"),
				new Point("ok"),
				new Point("ok"),
				new Point(new SQLException()),
				new Point("ok")));
	}
	public void testManySuccessesDistinctButFailureLast()
	{
		assertEquals(error, icon(
				new Point("okOther"),
				new Point("ok"),
				new Point("ok"),
				new Point("ok"),
				new Point(new SQLException())));
	}


	private static ChecklistIcon icon(final Point... points)
	{
		return getChecklistIcon(new ArrayList<>(Arrays.asList(points)));
	}
}
