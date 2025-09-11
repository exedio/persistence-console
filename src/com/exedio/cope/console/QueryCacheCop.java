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

import static com.exedio.cope.console.Console_Jspm.writeToggle;

import com.exedio.cope.Model;
import com.exedio.cope.QueryCacheHistogram;
import com.exedio.cope.QueryCacheInfo;
import com.exedio.cope.console.MeterTable.ListItem;
import io.micrometer.core.instrument.Tags;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

final class QueryCacheCop extends ConsoleCop<Void>
{
	static final String TAB = "querycache";
	private static final String DEPRECATED = "dep";
	static final String HISTOGRAM_LIMIT = "hl";
	private static final int HISTOGRAM_LIMIT_DEFAULT = 100;
	private static final String CONDENSE = "condense";

	final boolean deprecated;
	final int histogramLimit;
	final boolean condense;

	QueryCacheCop(final Args args)
	{
		this(args, false, HISTOGRAM_LIMIT_DEFAULT, true);
	}

	private QueryCacheCop(final Args args, final boolean deprecated, final int histogramLimit, final boolean condense)
	{
		super(TAB, "Query Cache", args);
		addParameter(DEPRECATED, deprecated);
		addParameter(HISTOGRAM_LIMIT, histogramLimit, HISTOGRAM_LIMIT_DEFAULT);
		addParameter(CONDENSE, condense);

		this.deprecated = deprecated;
		this.histogramLimit = histogramLimit;
		this.condense = condense;
	}

	static QueryCacheCop getQueryCacheCop(final Args args, final HttpServletRequest request)
	{
		return new QueryCacheCop(args,
				getBooleanParameter(request, DEPRECATED),
				getIntParameter(request, HISTOGRAM_LIMIT, HISTOGRAM_LIMIT_DEFAULT),
				getBooleanParameter(request, CONDENSE));
	}

	void writeHiddenParametersOtherThanHistogramLimit(final Out out)
	{
		if (deprecated)
			out.writeRaw("<input type=\"hidden\" name=\"" + DEPRECATED + "\" value=\"t\">");
		if (condense)
			out.writeRaw("<input type=\"hidden\" name=\"" + CONDENSE + "\" value=\"t\">");
	}

	@Override
	protected QueryCacheCop newArgs(final Args args)
	{
		return new QueryCacheCop(args, deprecated, histogramLimit, condense);
	}

	QueryCacheCop toToggleDeprecated()
	{
		return new QueryCacheCop(args, !deprecated, histogramLimit, condense);
	}

	QueryCacheCop toToggleCondense()
	{
		return new QueryCacheCop(args, deprecated, histogramLimit, !condense);
	}

	static final String CLEAR = "cache.clear";

	@Override
	void doPost(final HttpServletRequest request, final Model model)
	{
		{
			if(request.getParameter(CLEAR)!=null)
			{
				System.out.println("QueryCacheCop#clear");
				model.clearCache();
			}
		}
	}

	static final class Content
	{
		final QueryCacheHistogram[] histogram;
		final Condense[] histogramCondensed;

		final int avgKeyLength;
		final int maxKeyLength;
		final int minKeyLength;

		final int avgResultSize;
		final int maxResultSize;
		final int minResultSize;
		final int[] resultSizes;

		final long avgHits;
		final long maxHits;
		final long minHits;

		@SuppressWarnings("ZeroLengthArrayAllocation") // OK: is rarely called
		Content(final QueryCacheHistogram[] histogram, final boolean condense)
		{
			if(histogram.length>0)
			{
				//noinspection AssignmentToCollectionOrArrayFieldFromParameter
				this.histogram = histogram;

				final HashMap<String, Condense> histogramCondensed = condense ? new HashMap<>() : null;

				int sumKeyLength = 0;
				int maxKeyLength = 0;
				int minKeyLength = Integer.MAX_VALUE;

				int sumResultSize = 0;
				int maxResultSize = 0;
				int minResultSize = Integer.MAX_VALUE;
				final int[] resultSizes = new int[5];

				long sumHits = 0;
				long maxHits = 0;
				long minHits = Integer.MAX_VALUE;

				int recentUsage = 0;
				for(final QueryCacheHistogram info : histogram)
				{
					final String q = info.getQuery();

					if(condense)
					{
						final String qx = replaceParameters(q);

						final Condense existing = histogramCondensed.get(qx);
						if(existing==null)
							histogramCondensed.put(qx, new Condense(qx, recentUsage, info));
						else
							existing.accumulate(qx, recentUsage, info);
					}

					final int keyLength = q.length();
					sumKeyLength += keyLength;
					if(keyLength<minKeyLength)
						minKeyLength = keyLength;
					if(keyLength>maxKeyLength)
						maxKeyLength = keyLength;

					final int resultSize = info.getResultSize();
					sumResultSize += resultSize;
					if(resultSize<minResultSize)
						minResultSize = resultSize;
					if(resultSize>maxResultSize)
						maxResultSize = resultSize;
					if(resultSize<resultSizes.length)
						resultSizes[resultSize]++;

					final long hits = info.getHits();
					sumHits += hits;
					if(hits<minHits)
						minHits = hits;
					if(hits>maxHits)
						maxHits = hits;

					recentUsage++;
				}

				if(histogramCondensed!=null)
				{
					this.histogramCondensed = histogramCondensed.values().toArray(EMPTY_CONDENSE);
					Arrays.sort(this.histogramCondensed,
						Comparator.<Condense,Long>comparing(c->c.getRecentUsage().getMin()).
							thenComparing(c->c.getRecentUsage().getAverage()).
							thenComparing(c->c.getRecentUsage().getMax()).
							thenComparing(c->c.query));
				}
				else
				{
					this.histogramCondensed = null;
				}

				this.avgKeyLength = sumKeyLength / histogram.length;
				this.maxKeyLength = maxKeyLength;
				this.minKeyLength = minKeyLength;

				this.avgResultSize = sumResultSize / histogram.length;
				this.maxResultSize = maxResultSize;
				this.minResultSize = minResultSize;
				this.resultSizes = resultSizes;

				this.avgHits = sumHits / histogram.length;
				this.maxHits = maxHits;
				this.minHits = minHits;
			}
			else
			{
				//noinspection AssignmentToCollectionOrArrayFieldFromParameter
				this.histogram = histogram;
				this.histogramCondensed = condense ? new Condense[0] : null;

				this.avgKeyLength = -1;
				this.maxKeyLength = -1;
				this.minKeyLength = -1;

				this.avgResultSize = -1;
				this.maxResultSize = -1;
				this.minResultSize = -1;
				this.resultSizes = new int[0];

				this.avgHits = -1;
				this.maxHits = -1;
				this.minHits = -1;
			}
		}

		private static String replaceParameters(final String q)
		{
			StringBuilder qxbuf = null;
			int lastpos = 0;
			for(int pos = q.indexOf('\''); pos>=0; pos = q.indexOf('\'', pos+1))
			{
				if(qxbuf==null)
					qxbuf = new StringBuilder(q.substring(0, pos));
				else
					qxbuf.append(q, lastpos+1, pos);

				qxbuf.append('?');

				//noinspection AssignmentToForLoopParameter
				pos = q.indexOf('\'', pos+1);
				if(pos<0)
				{
					qxbuf = null;
					break;
				}

				lastpos = pos;
			}
			final String qx;
			if(qxbuf!=null)
			{
				qxbuf.append(q.substring(lastpos+1));
				qx = qxbuf.toString();
			}
			else
				qx = q;
			return qx;
		}
	}

	static final class Condense
	{
		final String query;
		private int count;
		private final CondenseRange recentUsage;
		private final CondenseRange resultSize;
		private final CondenseRange hits;

		Condense(final String query, final int recentUsage, final QueryCacheHistogram info)
		{
			this.query = query;
			this.count = 1;
			this.recentUsage = new CondenseRange(recentUsage);
			this.resultSize  = new CondenseRange(info.getResultSize());
			this.hits        = new CondenseRange(info.getHits());
		}

		void accumulate(final String query, final int recentUsage, final QueryCacheHistogram info)
		{
			assert this.query.equals(query);
			this.count++;
			this.recentUsage.accumulate(recentUsage);
			resultSize.accumulate(info.getResultSize());
			hits.accumulate(info.getHits());
		}

		int getCount()
		{
			return count;
		}

		CondenseRange getRecentUsage()
		{
			return recentUsage;
		}

		CondenseRange getResultSize()
		{
			return resultSize;
		}

		CondenseRange getHits()
		{
			return hits;
		}
	}

	private static final Condense[] EMPTY_CONDENSE = {};

	@Override boolean requiresUnsafeInlineStyle() { return true; }

	@Override boolean requiresUnsafeInlineScript() { return true; }

	@Override
	void writeBody(final Out out)
	{
		final Model model = app.model;

		writeToggle(out, toToggleDeprecated(), deprecated);
		out.write("deprecated");

		if(deprecated)
		{
			@SuppressWarnings("deprecation")
			final QueryCacheInfo queryCacheInfo = model.getQueryCacheInfo();
			QueryCache_Jspm.writeStatistics(out,
					model.getConnectProperties().getQueryCacheLimit(),
					queryCacheInfo);
		}
		else
		{
			final List<ListItem> list = new ArrayList<>();
			list.add(new ListItem("maximumSize"));
			list.add(new ListItem("size"));
			list.add(new ListItem("concurrentLoad"));
			list.add(new ListItem("evictions"));
			list.add(new ListItem("invalidations"));
			list.add(new ListItem("gets", Tags.of("result", "hit")));
			list.add(new ListItem("gets", Tags.of("result", "miss")));
			list.add(new ListItem("stamp.transactions"));
			list.add(new ListItem("stamp.hit"));
			list.add(new ListItem("stamp.purge"));
			final MeterTable table = new MeterTable();
			MeterTable.fillup(
					list,
					table,
					"com.exedio.cope.QueryCache.", // prefix
					"model", model.toString(), // filter key/value
					"DUMMY"); // row key
			Meter_Jspm.write("Statistics", "float:left;", list, out);
		}
		final QueryCacheHistogram[] histogram = model.getQueryCacheHistogram();
		QueryCache_Jspm.writeHistogram(this, out,
				new Content(histogram, condense));
	}

	static final class CondenseRange
	{
		private int count;
		private long min;
		private long max;
		private long sum;

		private CondenseRange(final long value)
		{
			count = 1;
			sum = value;
			min = value;
			max = value;
		}

		private void accumulate(final long value)
		{
			count++;
			sum += value;
			min = Math.min(min, value);
			max = Math.max(max, value);
		}

		long getMin()
		{
			return min;
		}

		long getMax()
		{
			return max;
		}

		double getAverage()
		{
			return ((double)sum) / count;
		}
	}
}
