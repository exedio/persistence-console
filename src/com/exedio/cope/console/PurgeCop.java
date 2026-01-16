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
import com.exedio.cope.revstat.RevisionStatistics;
import com.exedio.cope.util.EmptyJobContext;
import jakarta.servlet.http.HttpServletRequest;

final class PurgeCop extends ConsoleCop<Void>
{
	static final String TAB = "purge";
	static final String PURGE = "purge";
	static final String REVISION_STATISTICS = "revisionStatistics";

	PurgeCop(final Args args)
	{
		super(TAB, "Purge", args);
	}

	@Override
	protected PurgeCop newArgs(final Args args)
	{
		return new PurgeCop(args);
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = app.model;
		failIfNotConnected();

		final HttpServletRequest request = out.request;
		final boolean post = isPost(request);
		final boolean purge = post && request.getParameter(PURGE)!=null;
		final boolean revisionStatistics = post && request.getParameter(REVISION_STATISTICS)!=null;

		Purge_Jspm.writeBody(
				out, model, this,
				purge,
				RevisionStatistics.isContainedIn(model),
				revisionStatistics,
				(purge||revisionStatistics) ? new Context(out) : null);
	}

	private static final class Context extends EmptyJobContext
	{
		private final Out out;

		Context(final Out out)
		{
			this.out = out;
		}

		@Override
		public boolean supportsMessage()
		{
			return true;
		}

		@Override
		public void setMessage(final String message)
		{
			out.writeStatic("\n\t\t\t<li>");
			out.write(message);
			out.writeStatic("</li>");
			out.flush();
		}

		@Override
		public boolean supportsProgress()
		{
			return true;
		}

		@Override
		public void incrementProgress(final int delta)
		{
			out.writeStatic("\n\t\t\t<li>progress: ");
			out.write(delta);
			out.writeStatic("</li>");
			out.flush();
		}
	}
}
