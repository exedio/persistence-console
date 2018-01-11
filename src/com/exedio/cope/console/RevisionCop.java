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
import com.exedio.cope.Revision;
import com.exedio.cope.RevisionInfo;
import com.exedio.cope.RevisionInfoMutex;
import com.exedio.cope.Revisions;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;
import com.exedio.dsmf.SQLRuntimeException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;

final class RevisionCop extends ConsoleCop<Void> implements Pageable
{
	static final String TAB_REVISION = "revision";

	private static final Pager.Config PAGER_CONFIG = new Pager.Config(10, 20, 50, 100, 200, 500);

	private final Pager pager;

	private RevisionCop(final Args args, final Pager pager)
	{
		super(TAB_REVISION, "Revisions", args);
		this.pager = pager;

		//noinspection ThisEscapedInObjectConstruction
		pager.addParameters(this);
	}

	RevisionCop(final Args args)
	{
		this(args, PAGER_CONFIG.newPager());
	}

	RevisionCop(final Args args, final HttpServletRequest request)
	{
		this(args, PAGER_CONFIG.newPager(request));
	}

	@Override
	protected RevisionCop newArgs(final Args args)
	{
		return new RevisionCop(args, pager);
	}

	@Override
	public Pager getPager()
	{
		return pager;
	}

	@Override
	public RevisionCop toPage(final Pager pager)
	{
		return new RevisionCop(args, pager);
	}

	static final String REVISE  = "revise";
	static final String NICE_CREATE = "nice.create";
	static final String NICE_DROP   = "nice.drop";

	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);

		if(isPost(request))
		{
			if(request.getParameter(REVISE)!=null)
				model.revise();
			if(request.getParameter(NICE_CREATE)!=null)
			{
				try
				{
					new SchemaNice(model).create();
				}
				catch(final SQLException e)
				{
					throw new RuntimeException(e);
				}
			}
			if(request.getParameter(NICE_DROP)!=null)
			{
				try
				{
					new SchemaNice(model).drop();
				}
				catch(final SQLException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	void writeHead(final Out out)
	{
		Revision_Jspm.writeHead(out);
	}

	private static RevisionLine register(final TreeMap<Integer, RevisionLine> lines, final int revision)
	{
		return lines.computeIfAbsent(revision, RevisionLine::new);
	}

	static RevisionInfo read(final byte[] bytes)
	{
		if(bytes==null)
			return null;

		try
		{
			return RevisionInfo.read(bytes);
		}
		catch(final Exception | AssertionError e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = out.model;
		final Revisions revisions = model.getRevisions();

		if(revisions==null)
		{
			Revision_Jspm.writeBodyDisabled(out);
			return;
		}

		final TreeMap<Integer, RevisionLine> lines = new TreeMap<>();

		register(lines, revisions.getNumber()).setCurrent();
		for(final Revision m : revisions.getList())
			register(lines, m.getNumber()).setRevision(m);

		Map<Integer, byte[]> logsRaw = null;
		try
		{
			logsRaw = model.getRevisionLogsAndMutex();
		}
		catch(final SQLRuntimeException e)
		{
			e.printStackTrace(); // TODO show error in page together with declared revisions
		}

		byte[] mutex = null;
		if(logsRaw!=null)
		{
			for(final Map.Entry<Integer, byte[]> e : logsRaw.entrySet())
			{
				if(e.getKey()==-1)
					mutex = e.getValue();
				else
					register(lines, e.getKey()).setInfo(e.getValue());
			}
		}

		final ArrayList<RevisionLine> lineList = new ArrayList<>(lines.values());
		Collections.reverse(lineList);

		Revision_Jspm.writeBody(
				out, this,
				(RevisionInfoMutex)read(mutex),
				pager.init(lineList));
	}
}
