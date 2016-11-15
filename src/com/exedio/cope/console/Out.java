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

import com.exedio.cope.Model;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.XMLEncoder;
import com.exedio.cops.Cop;
import com.exedio.cops.Resource;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Node;
import java.io.PrintStream;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

final class Out extends OutBasic
{
	final HttpServletRequest request;
	final Model model;
	private final ConsoleServlet servlet;
	private final String mediaURLPrefix;
	private int nextId = 0;

	Out(
			final HttpServletRequest request,
			final Model model,
			final ConsoleServlet servlet,
			final ConsoleCop.Args args,
			final PrintStream bf)
	{
		super(bf);
		assert request!=null;
		this.request = request;
		this.model = model;
		this.mediaURLPrefix = args.mediaURLPrefix;
		this.servlet = servlet;

		final String datePattern = args.datePrecision.pattern;
		fullDateFormat  = new SimpleDateFormat("yyyy/MM/dd'&nbsp;'" + datePattern);
		yearFormat      = new SimpleDateFormat(     "MM/dd'&nbsp;'" + datePattern);
		todayDateFormat = new SimpleDateFormat(                       datePattern);
	}

	void writeRaw(final String s)
	{
		bf.print(s);
	}

	void writeRaw(final char c)
	{
		bf.print(c);
	}

	void write(final long i)
	{
		bf.print(i);
	}

	void write(final double d)
	{
		bf.print(d);
	}

	void write(final boolean b)
	{
		bf.print(b);
	}

	void write(final Enum<?> e)
	{
		bf.print(e.name());
	}

	void write(final Charset cs)
	{
		bf.print(cs.name());
	}

	void write(final Class<?> c)
	{
		final String s = c.getName();
		final int pos = s.lastIndexOf('.');
		if(pos<0)
		{
			bf.print(s);
		}
		else
		{
			bf.print("<small>");
			bf.print(s.substring(0, pos));
			bf.print("</small>");
			bf.print(s.substring(pos));
		}
	}

	void write(final InetAddress s)
	{
		bf.print(s);
	}

	void write(final Node.Color c)
	{
		bf.print(c.name());
	}

	private final long now = System.currentTimeMillis();
	private final SimpleDateFormat fullDateFormat;
	private final SimpleDateFormat yearFormat;
	private final SimpleDateFormat todayDateFormat;
	private static final long yearInterval  = 1000l * 60 * 60 * 24 * 90; // 90 days
	private static final long todayInterval = 1000l * 60 * 60 * 6; // 6 hours

	void write(final Date date)
	{
		if(date!=null)
			bf.print(format(date));
	}

	private String format(final Date date)
	{
		final long dateMillis = date.getTime();
		return (
			( (now-todayInterval) < dateMillis && dateMillis < (now+todayInterval) )
			? todayDateFormat :
			( (now-yearInterval) < dateMillis && dateMillis < (now+yearInterval) )
			? yearFormat : fullDateFormat).format(date);
	}

	void write(final Thread.State state)
	{
		if(state!=null)
			write(state.name().toLowerCase(Locale.ENGLISH));
	}

	void write(final Constraint.Type type)
	{
		switch(type)
		{
			case PrimaryKey: bf.append("pk"    ); break;
			case ForeignKey: bf.append("fk"    ); break;
			case Unique:     bf.append("unique"); break;
			case Check:      bf.append("check" ); break;
			default:
				throw new RuntimeException(type.name());
		}
	}

	void writeStackTrace(final Throwable t)
	{
		t.printStackTrace(bf);
	}

	void writeSQL(final String s)
	{
		bf.print(Format.highlightSQL(XMLEncoder.encode(s)));
	}

	void write(final Resource resource)
	{
		bf.print(resource.getURL(request));
	}

	void write(final Cop cop)
	{
		// here we don't have to call HttpServletResponse.encodeURL
		// since HttpSessions are not used at all
		bf.print(XMLEncoder.encode(cop.getURL(request)));
	}

	void write(final MediaPath.Locator locator)
	{
		if(mediaURLPrefix!=null)
		{
			bf.print(mediaURLPrefix);
			bf.print(locator.getPath());
		}
		else
		{
			bf.print(locator.getURLByConnect());
		}
	}

	List<String> getMediaURLPrefixes()
	{
		return servlet.getMediaURLPrefixes(request);
	}

	void connect()
	{
		servlet.connect();
	}

	boolean willBeReturned(final ConnectToken token)
	{
		return servlet.willBeReturned(token);
	}

	/**
	 * Returns an id unique within the document.
	 * Useful for html ids needed for javascript manipulating DOM.
	 */
	int nextId()
	{
		return nextId++;
	}

	void flush()
	{
		bf.flush();
	}

	void close()
	{
		bf.close();
	}
}
