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
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Media;
import java.util.ArrayList;
import java.util.List;

final class MediaTypeCop extends TestCop<Media>
{
	MediaTypeCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_MEDIA_TYPE, "Media Types", args, testArgs);
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
	List<Media> getItems(final Model model)
	{
		final ArrayList<Media> result = new ArrayList<Media>();

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
		final Condition c = media.bodyMismatchesContentType();
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
				if(c!=Condition.FALSE)
					out.writeSQL(c.toString());
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
	int check(final Media media)
	{
		final Model model = media.getType().getModel();
		final Query<?> query = media.getType().newQuery(media.bodyMismatchesContentType());
		try
		{
			model.startTransaction("Console MediaType " + id);
			final int result = query.total();
			model.commit();
			return result;
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}
}
