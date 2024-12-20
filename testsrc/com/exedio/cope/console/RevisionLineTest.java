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

import static com.exedio.cope.console.Format.highlightSQL;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Revision;
import com.exedio.cope.RevisionInfoCreate;
import com.exedio.cope.RevisionInfoRevise;
import com.exedio.cope.RevisionInfoRevise.Body;
import com.exedio.cope.junit.CopeAssert;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class RevisionLineTest extends CopeAssert
{
	private static final Date DATE = new Date(2874526134l);
	private static final String DATE_STRING = "1970/02/03 06:28:46.134";

	@Test void testBad() throws UnsupportedEncodingException
	{
		final RevisionLine l = new RevisionLine(55);
		assertEquals(55, l.number);
		assertEquals(false, l.hasRevision());
		assertEquals(null, l.getContent());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		assertEquals(null, l.getDate());
		assertEquals(0, l.getBodyCount());
		assertEquals(list(), l.getBody());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());

		final Revision r = new Revision(55, "comment55", "sql55.1", "sql55.2");
		l.setRevision(r);
		assertEquals(true, l.hasRevision());
		assertEquals("comment55", l.getContent());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		assertEquals(null, l.getDate());
		assertEquals(2, l.getBodyCount());
		assertEquals("sql55.1", l.getBody().get(0).getSQL());
		assertEquals(0, l.getBody().get(0).getRows());
		assertEquals(0, l.getBody().get(0).getElapsed());
		assertEquals("sql55.2", l.getBody().get(1).getSQL());
		assertEquals(0, l.getBody().get(1).getRows());
		assertEquals(0, l.getBody().get(1).getElapsed());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());

		l.setInfo("#migrationlogv01\nkey1=value1\nkey2=value2".getBytes("latin1"));
		assertEquals("#migrationlogv01\nkey1=value1\nkey2=value2", l.getLogString());
		final HashMap<String, String> map = new HashMap<>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		assertEquals(true, l.hasRevision());
		assertEquals("comment55", l.getContent());
		assertEquals(map, l.getLogProperties());
		assertEquals(null, l.getDate());
		assertEquals(2, l.getBodyCount());
		assertEquals("sql55.1", l.getBody().get(0).getSQL());
		assertEquals(0, l.getBody().get(0).getRows());
		assertEquals(0, l.getBody().get(0).getElapsed());
		assertEquals("sql55.2", l.getBody().get(1).getSQL());
		assertEquals(0, l.getBody().get(1).getRows());
		assertEquals(0, l.getBody().get(1).getElapsed());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());
	}

	@Test void testRevise()
	{
		final RevisionLine l = new RevisionLine(55);
		assertEquals(55, l.number);
		assertEquals(false, l.hasRevision());
		assertEquals(null, l.getContent());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		assertEquals(null, l.getDate());
		assertEquals(0, l.getBodyCount());
		assertEquals(list(), l.getBody());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());

		final Revision r = new Revision(55, "comment55", "sql55.1", "sql55.2");
		l.setRevision(r);
		assertEquals(true, l.hasRevision());
		assertEquals("comment55", l.getContent());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		assertEquals(null, l.getDate());
		assertEquals(2, l.getBodyCount());
		assertEquals("sql55.1", l.getBody().get(0).getSQL());
		assertEquals(0, l.getBody().get(0).getRows());
		assertEquals(0, l.getBody().get(0).getElapsed());
		assertEquals("sql55.2", l.getBody().get(1).getSQL());
		assertEquals(0, l.getBody().get(1).getRows());
		assertEquals(0, l.getBody().get(1).getElapsed());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());

		l.setInfo(new RevisionInfoRevise(55, null, DATE, Collections.emptyMap(), "comment55",
				new Body("sql55.1", 126, 567),
				new Body("sql55.2", 127, 568)).toBytes());
		assertTrue(l.getLogString().startsWith("#migrationlogv01"+lineSeparator()), l.getLogString());
		final HashMap<String, String> map = new HashMap<>();
		map.put("comment", "comment55");
		map.put("dateUTC", DATE_STRING);
		map.put("revision", "55");
		map.put("body0.elapsed", "567");
		map.put("body0.rows", "126");
		map.put("body0.sql", "sql55.1");
		map.put("body1.elapsed", "568");
		map.put("body1.rows", "127");
		map.put("body1.sql", "sql55.2");
		assertEquals(true, l.hasRevision());
		assertEquals("comment55", l.getContent());
		assertEquals(map, l.getLogProperties());
		assertEquals(DATE, l.getDate());
		assertEquals(2, l.getBodyCount());
		assertEquals("sql55.1", l.getBody().get(0).getSQL());
		assertEquals(126, l.getBody().get(0).getRows());
		assertEquals(567, l.getBody().get(0).getElapsed());
		assertEquals("sql55.2", l.getBody().get(1).getSQL());
		assertEquals(127, l.getBody().get(1).getRows());
		assertEquals(568, l.getBody().get(1).getElapsed());
		assertEquals(253, l.getRows());
		assertEquals(1135, l.getElapsed());
	}

	@Test void testReviseRemoved()
	{
		final RevisionLine l = new RevisionLine(55);
		assertEquals(55, l.number);
		assertEquals(false, l.hasRevision());
		assertEquals(null, l.getContent());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		assertEquals(null, l.getDate());
		assertEquals(0, l.getBodyCount());
		assertEquals(list(), l.getBody());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());

		l.setInfo(new RevisionInfoRevise(55, null, DATE, Collections.emptyMap(), "comment55",
				new Body("sql55.1", 126, 567),
				new Body("sql55.2", 127, 568)).toBytes());
		assertTrue(l.getLogString().startsWith("#migrationlogv01"+lineSeparator()), l.getLogString());
		final HashMap<String, String> map = new HashMap<>();
		map.put("comment", "comment55");
		map.put("dateUTC", DATE_STRING);
		map.put("revision", "55");
		map.put("body0.elapsed", "567");
		map.put("body0.rows", "126");
		map.put("body0.sql", "sql55.1");
		map.put("body1.elapsed", "568");
		map.put("body1.rows", "127");
		map.put("body1.sql", "sql55.2");
		assertEquals(false, l.hasRevision());
		assertEquals("comment55", l.getContent());
		assertEquals(map, l.getLogProperties());
		assertEquals(DATE, l.getDate());
		assertEquals(2, l.getBodyCount());
		assertEquals("sql55.1", l.getBody().get(0).getSQL());
		assertEquals(126, l.getBody().get(0).getRows());
		assertEquals(567, l.getBody().get(0).getElapsed());
		assertEquals("sql55.2", l.getBody().get(1).getSQL());
		assertEquals(127, l.getBody().get(1).getRows());
		assertEquals(568, l.getBody().get(1).getElapsed());
		assertEquals(253, l.getRows());
		assertEquals(1135, l.getElapsed());
	}

	@Test void testCreate()
	{
		final RevisionLine l = new RevisionLine(55);
		assertEquals(55, l.number);
		assertEquals(false, l.hasRevision());
		assertEquals(null, l.getContent());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		assertEquals(null, l.getDate());
		assertEquals(0, l.getBodyCount());
		assertEquals(list(), l.getBody());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());

		final Revision r = new Revision(55, "comment55", "sql55.1", "sql55.2");
		l.setRevision(r);
		assertEquals(true, l.hasRevision());
		assertEquals("comment55", l.getContent());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		assertEquals(null, l.getDate());
		assertEquals(2, l.getBodyCount());
		assertEquals("sql55.1", l.getBody().get(0).getSQL());
		assertEquals(0, l.getBody().get(0).getRows());
		assertEquals(0, l.getBody().get(0).getElapsed());
		assertEquals("sql55.2", l.getBody().get(1).getSQL());
		assertEquals(0, l.getBody().get(1).getRows());
		assertEquals(0, l.getBody().get(1).getElapsed());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());

		l.setInfo(new RevisionInfoCreate(55, DATE, Collections.emptyMap()).toBytes());
		assertTrue(l.getLogString().startsWith("#migrationlogv01"+lineSeparator()), l.getLogString());
		final HashMap<String, String> map = new HashMap<>();
		map.put("create", "true");
		map.put("dateUTC", DATE_STRING);
		map.put("revision", "55");
		assertEquals(true, l.hasRevision());
		assertEquals("Created Schema (comment55)", l.getContent());
		assertEquals(map, l.getLogProperties());
		assertEquals(DATE, l.getDate());
		assertEquals(2, l.getBodyCount());
		assertEquals("sql55.1", l.getBody().get(0).getSQL());
		assertEquals(0, l.getBody().get(0).getRows());
		assertEquals(0, l.getBody().get(0).getElapsed());
		assertEquals("sql55.2", l.getBody().get(1).getSQL());
		assertEquals(0, l.getBody().get(1).getRows());
		assertEquals(0, l.getBody().get(1).getElapsed());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());
	}

	@Test void testCreateRemoved()
	{
		final RevisionLine l = new RevisionLine(55);
		assertEquals(55, l.number);
		assertEquals(false, l.hasRevision());
		assertEquals(null, l.getContent());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		assertEquals(null, l.getDate());
		assertEquals(0, l.getBodyCount());
		assertEquals(list(), l.getBody());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());

		l.setInfo(new RevisionInfoCreate(55, DATE, Collections.emptyMap()).toBytes());
		assertTrue(l.getLogString().startsWith("#migrationlogv01"+lineSeparator()), l.getLogString());
		final HashMap<String, String> map = new HashMap<>();
		map.put("create", "true");
		map.put("dateUTC", DATE_STRING);
		map.put("revision", "55");
		assertEquals(false, l.hasRevision());
		assertEquals("Created Schema", l.getContent());
		assertEquals(map, l.getLogProperties());
		assertEquals(DATE, l.getDate());
		assertEquals(0, l.getBodyCount());
		assertEquals(list(), l.getBody());
		assertEquals(-1, l.getRows());
		assertEquals(-1, l.getElapsed());
	}

	@Test void testDiff()
	{
		final HashMap<String, String> left = new HashMap<>();
		left.put("leftOnly", "true");
		left.put("equal", "equalValue");
		left.put("nonEqual", "left");
		final HashMap<String, String> right = new HashMap<>();
		right.put("rightOnly", "true");
		right.put("equal", "equalValue");
		right.put("nonEqual", "right");

		assertContains("leftOnly", "rightOnly", "nonEqual", RevisionLine.diff(left, right));
	}

	@Test void testHighlightSQL()
	{
		assertEquals(
				"<b>alter</b> <b>table</b> hallo " +
				"<b>add</b> <b>column</b> zack integer, " +
				"<b>drop</b> <b>column</b> zosch",
				highlightSQL(
						"alter table hallo " +
						"add column zack integer, " +
						"drop column zosch"));
		assertEquals("<b>select</b> selected <b>from</b> fromage <b>where</b> x=1", highlightSQL("select selected from fromage where x=1"));
		assertEquals("<b>insert</b> <b>into</b> bing <b>values</b> (a,b,c)", highlightSQL("insert into bing values (a,b,c)"));
		assertEquals("<b>update</b> bong <b>set</b> bing = 1", highlightSQL("update bong set bing = 1"));
	}
}
