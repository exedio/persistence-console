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

import com.exedio.cope.Condition;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Media;
import java.util.ArrayList;
import java.util.List;

final class MediaTypeCop extends TestCop<Media>
{
	static final String TAB = "mediatype";

	MediaTypeCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Media Types", args, testArgs);
	}

	@Override
	protected MediaTypeCop newArgs(final Args args)
	{
		return new MediaTypeCop(args, testArgs);
	}

	@Override
	protected MediaTypeCop newTestArgs(final TestArgs testArgs)
	{
		return new MediaTypeCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Verifies whether the content type of Media is consistent to their body (i.e., binary data). " +
				"This is done by usual mime-magic.",
			"IMPACT: " +
				"Failures here do cause MediaServlet to send wrong Content-Type headers in http responses, " +
				"which may confuse browsers and cause broken web pages."
		};
	}

	/**
	 * Is needed, because {@link #getItems(Model)} calls
	 * {@link com.exedio.cope.DataField#getVaultInfo()}.
	 */
	@Override
	boolean requiresConnect()
	{
		return true;
	}

	@Override
	List<Media> getItems(final Model model)
	{
		final ArrayList<Media> result = new ArrayList<>();

		for(final Type<?> type : model.getTypes())
		{
			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof Media)
					result.add((Media)feature);
			}
		}
		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Type", "Name", "Content Type", "Query"};
	}

	@Override
	void writeValue(final Out out, final Media media, final int h)
	{
		switch(h)
		{
			case 0: out.write(media.getType().getID()); break;
			case 1:
				out.writeRaw("<a href=\"");
				out.write(new MediaCop(args, media));
				out.writeRaw("\">");
				out.write(media.getName());
				out.writeRaw("</a>");
				break;
			case 2: out.write(media.getContentTypeDescription().replaceAll(",", ", ")); break;
			case 3:
				final Condition c = media.bodyMismatchesContentTypeIfSupported();
				if(c!=Condition.FALSE)
					writeValueLong(out, c.toString());
				break;
			default:
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final Media media)
	{
		return media.getID();
	}

	@Override
	Media forID(final Model model, final String id)
	{
		return (Media)model.getFeature(id);
	}

	@Override
	long check(final Media media, final Model model)
	{
		final Query<?> query = getQuery(media);

		try(TransactionTry tx = model.startTransactionTry("Console MediaType " + id))
		{
			return tx.commit(
					query.total());
		}
	}

	private static Query<?> getQuery(final Media media)
	{
		return media.getType().newQuery(media.bodyMismatchesContentTypeIfSupported());
	}

	@Override
	String getViolationSql(final Media media, final Model model)
	{
		if (media.bodyMismatchesContentTypeIfSupported()==Condition.FALSE)
			return null;
		else
			return SchemaInfo.search(getQuery(media));
	}
}
