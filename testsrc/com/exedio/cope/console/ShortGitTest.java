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

import static com.exedio.cope.console.VmCop.shortGit;

import junit.framework.TestCase;

public class ShortGitTest extends TestCase
{
	public void testMatch()
	{
		assertEquals(
				"revision: 9131eef or so", shortGit(
				"revision: 9131eefa398531c7dc98776e8a3fe839e544c5b2 or so"));
	}

	public void testTooShort()
	{
		assertEquals(
				"revision: 9131eefa398531c7dc98776e8a3fe839e544c5b or so", shortGit(
				"revision: 9131eefa398531c7dc98776e8a3fe839e544c5b or so"));
	}

	public void testTooLong()
	{
		assertEquals(
				"revision: 9131eefa398531c7dc98776e8a3fe839e544c5b2a or so", shortGit(
				"revision: 9131eefa398531c7dc98776e8a3fe839e544c5b2a or so"));
	}

	public void testNoHex()
	{
		assertEquals(
				"revision: 9131eefa398531c7dc98776e8a3fe839e544c5bX or so", shortGit(
				"revision: 9131eefa398531c7dc98776e8a3fe839e544c5bX or so"));
	}
}
