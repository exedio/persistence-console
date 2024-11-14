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

package com.exedio.cope.junit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class CopeAssert
{
	public static final <T> void assertContainsList(final List<T> expected, final Collection<T> actual)
	{
		if(expected==null && actual==null)
			return;

		assertNotNull(expected, "expected null, but was " + actual);
		assertNotNull(actual, "expected " + expected + ", but was null");

		if(expected.size()!=actual.size() ||
				!expected.containsAll(actual) ||
				!actual.containsAll(expected))
			fail("expected "+expected+", but was "+actual);
	}

	public static final void assertContains(final Collection<?> actual)
	{
		assertContainsList(Collections.emptyList(), actual);
	}

	public static final <T> void assertContains(final T o, final Collection<T> actual)
	{
		assertContainsList(Collections.singletonList(o), actual);
	}

	public static final <T> void assertContains(final T o1, final T o2, final Collection<T> actual)
	{
		assertContainsList(Arrays.asList(o1, o2), actual);
	}

	public static final <T> void assertContains(final T o1, final T o2, final T o3, final Collection<T> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3), actual);
	}

	public static final <T> void assertContains(final T o1, final T o2, final T o3, final T o4, final Collection<T> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4), actual);
	}

	public static final <T> void assertContains(final T o1, final T o2, final T o3, final T o4, final T o5, final Collection<T> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4, o5), actual);
	}

	public static final <T> void assertContains(final T o1, final T o2, final T o3, final T o4, final T o5, final T o6, final Collection<T> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4, o5, o6), actual);
	}

	public static final <T> void assertContains(final T o1, final T o2, final T o3, final T o4, final T o5, final T o6, final T o7, final Collection<T> actual)
	{
		assertContainsList(Arrays.asList(o1, o2, o3, o4, o5, o6, o7), actual);
	}

	public static final List<Object> list(final Object... o)
	{
		return List.of(o);
	}
}
