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

import com.exedio.cope.ItemCacheInfo;
import com.exedio.cope.ItemCacheStatistics;
import javax.servlet.http.HttpServletRequest;

final class ItemCacheCop extends ConsoleCop<Void>
{
	private static final String DETAILED = "dt";

	final boolean detailed;

	ItemCacheCop(final Args args)
	{
		this(args, false);
	}

	private ItemCacheCop(final Args args, final boolean detailed)
	{
		super(TAB_ITEM_CACHE, "Item Cache", args);
		this.detailed = detailed;

		addParameter(DETAILED, detailed);
	}

	static ItemCacheCop getItemCacheCop(final Args args, final HttpServletRequest request)
	{
		return new ItemCacheCop(args, getBooleanParameter(request, DETAILED));
	}

	@Override
	protected ItemCacheCop newArgs(final Args args)
	{
		return new ItemCacheCop(args);
	}

	@Override
	final void writeBody(final Out out)
	{
		final ItemCacheStatistics statistics = out.model.getItemCacheStatistics();
		ItemCache_Jspm.writeBody(this, out, statistics);
	}

	ItemCacheCop toToggleDetailed()
	{
		return new ItemCacheCop(args, !detailed);
	}

	boolean showInfo(ItemCacheInfo info)
	{
		return detailed
			|| info.getLevel()!=0
			|| info.getHits()!=0
			|| info.getMisses()!=0
			|| info.getConcurrentLoads()!=0
			|| info.getReplacementsL()!=0
			|| info.getInvalidationsOrdered()!=0
			|| info.getInvalidationsDone()!=0
			|| info.getStampsSize()!=0
			|| info.getStampsHits()!=0
			|| info.getStampsPurged()!=0;
	}
}
