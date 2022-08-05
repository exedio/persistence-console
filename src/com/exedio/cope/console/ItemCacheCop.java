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
import com.exedio.cope.Type;
import io.micrometer.core.instrument.Tags;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

final class ItemCacheCop extends ConsoleCop<Void>
{
	static final String TAB = "itemcache";
	private static final String DETAILED = "dt";
	private static final String DEPRECATED = "dep";

	final boolean detailed;
	final boolean deprecated;

	ItemCacheCop(final Args args)
	{
		this(args, false, false);
	}

	private ItemCacheCop(final Args args, final boolean detailed, final boolean deprecated)
	{
		super(TAB, "Item Cache", args);
		this.detailed = detailed;
		this.deprecated = deprecated;

		addParameter(DETAILED, detailed);
		addParameter(DEPRECATED, deprecated);
	}

	static ItemCacheCop getItemCacheCop(final Args args, final HttpServletRequest request)
	{
		return new ItemCacheCop(args, getBooleanParameter(request, DETAILED), getBooleanParameter(request, DEPRECATED));
	}

	@Override
	protected ItemCacheCop newArgs(final Args args)
	{
		return new ItemCacheCop(args);
	}

	@Override
	void writeBody(final Out out)
	{
		ItemCache_Jspm.writeToggles(this, out);
		if(deprecated)
			writeBodyDeprecated(out);
		else
			writeBodyMetrics(out);
	}

	private void writeBodyDeprecated(final Out out)
	{
		@SuppressWarnings("deprecation")
		final ItemCacheStatistics statistics = out.model.getItemCacheStatistics();
		ItemCache_Jspm.writeBody(this, out, statistics);
	}

	private void writeBodyMetrics(final Out out)
	{
		final List<MeterTable.ListItem> list = new ArrayList<>();
		list.add(new MeterTable.ListItem("maximumSize"));
		list.add(new MeterTable.ListItem("size"));
		list.add(new MeterTable.ListItem("stamp.transactions"));
		//list.add(new MeterTable.ListItem("nonexisting")); just for debugging
		final MeterTable table = new MeterTable();
		table.addColumnWhite("gets", Tags.of("result", "hit"));
		table.addColumnWhite("gets", Tags.of("result", "miss"));
		table.addColumnBlue ("evictions");
		table.addColumnWhite("invalidations", Tags.of("effect", "actual"));
		table.addColumnWhite("invalidations", Tags.of("effect", "futile"));
		table.addColumnBlue ("stamp.hit");
		table.addColumnBlue ("stamp.purge");
		table.addColumnWhite("concurrentLoad");
		for(final Type<?> type : out.model.getConcreteTypes())
			table.addRow(type.getID());
		MeterTable.fillup(
				list,
				table,
				"com.exedio.cope.ItemCache.",  // prefix
				"model", out.model.toString(), // filter key/value
				"type"); // row key
		Meter_Jspm.write("Statistics", null, list, out);
		ItemCache_Jspm.writeBody(detailed, table, out);
	}

	ItemCacheCop toToggleDetailed()
	{
		return new ItemCacheCop(args, !detailed, deprecated);
	}

	ItemCacheCop toToggleDeprecated()
	{
		return new ItemCacheCop(args, detailed, !deprecated);
	}

	boolean showInfo(final ItemCacheInfo info)
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
