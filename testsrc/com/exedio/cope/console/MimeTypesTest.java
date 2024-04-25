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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import junit.framework.TestCase;

public class MimeTypesTest extends TestCase
{
	public void testIt()
	{
		assertIt("image/png, text/plain", "image/png", "text/plain");
		assertIt("image/png, text/plain", "text/plain", "image/png");
		assertIt("image/jpeg, ~/png, text/plain", "image/jpeg", "text/plain", "image/png");
		assertIt("image/[p]jpeg, ~/png, text/plain", "image/jpeg", "text/plain", "image/png", "image/pjpeg");
		assertIt("image/[p]jpeg, ~/[x-]png, text/plain", "image/x-png", "image/jpeg", "text/plain", "image/png", "image/pjpeg");
	}

	private static void assertIt(final String expected, final String... actual)
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final OutBasic out = new OutBasic(new PrintStream(baos, false, UTF_8));
		MediaStatsCop.printContentTypes(out, Arrays.asList(actual));
		assertEquals(expected, baos.toString(UTF_8));
	}
}
