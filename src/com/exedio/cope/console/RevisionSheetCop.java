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
import com.exedio.cope.RevisionInfo;
import com.exedio.cope.RevisionInfoRevise;
import java.util.Map;
import java.util.TreeMap;

final class RevisionSheetCop extends ConsoleCop<Void>
{
	RevisionSheetCop(final Args args)
	{
		super(TAB_REVISION_SHEET, "Revision Sheet", args);
	}

	@Override
	protected RevisionSheetCop newArgs(final Args args)
	{
		return new RevisionSheetCop(args);
	}

	@Override
	final void writeBody(final Out out)
	{
		final Model model = out.model;

		if(model.getRevisions()==null)
		{
			Revision_Jspm.writeBodyDisabled(out);
			return;
		}

		final TreeMap<Integer, RevisionInfoRevise> revisions = new TreeMap<>();

		for(final Map.Entry<Integer, byte[]> e : model.getRevisionLogs().entrySet())
		{
			final RevisionInfo info = RevisionInfo.read(e.getValue());
			if(info!=null && info instanceof RevisionInfoRevise)
				revisions.put(info.getNumber(), (RevisionInfoRevise)info);
		}

		RevisionSheet_Jspm.writeBody(out, revisions);
	}
}
