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

import static com.exedio.cope.console.Console_Jspm.writeError;
import static com.exedio.cope.console.Console_Jspm.writeNotConnected;
import static com.exedio.cope.console.Console_Jspm.writeUnknown;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.HashConstraint;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaPath;
import java.util.ArrayList;
import java.util.List;

final class UniqueHashedMediaCop extends TestCop<HashConstraint>
{
	UniqueHashedMediaCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_UNIQUE_HASHED_MEDIA, "Hash Constraints", args, testArgs);
	}

	@Override
	protected UniqueHashedMediaCop newArgs(final Args args)
	{
		return new UniqueHashedMediaCop(args, testArgs);
	}

	@Override
	protected UniqueHashedMediaCop newTestArgs(final TestArgs testArgs)
	{
		return new UniqueHashedMediaCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Verifies that hash of UniqueHashedMedia is consistent to its actual data.",
			"IMPACT: " +
				"Any failures here invalidate all contracts of UniqueHashedMedia. " +
				"Your application may either fail with errors or silently destroy your data stored in UniqueHashedMedia. " +
				"DANGER ZONE."
		};
	}

	@Override
	List<HashConstraint> getItems(final Model model)
	{
		final ArrayList<HashConstraint> result = new ArrayList<>();

		for(final Type<?> type : model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof HashConstraint)
					result.add((HashConstraint)feature);

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"ID", "Content Type", "Hash", "SQL"};
	}

	@Override
	void writeValue(final Out out, final HashConstraint media, final int h)
	{
		final Pattern pattern = media.getData().getPattern();
		switch(h)
		{
			case 0:
				if(pattern instanceof MediaPath)
				{
					out.writeRaw("<a href=\"");
					out.write(new MediaCop(args, (MediaPath)pattern));
					out.writeRaw("\">");
				}

				out.write(media.getID());

				if(pattern instanceof Media)
					out.writeRaw("</a>");

				break;
			case 1:
				if(pattern instanceof Media)
					out.write(((Media)pattern).getContentTypeDescription().replaceAll(",", ", "));
				break;
			case 2:
			{
				final String algorithm = media.getAlgorithm();
				out.write(algorithm);
				final Model model = media.getType().getModel();
				writeError(out,
						model.isConnected() &&
						!model.getSupportedDataHashAlgorithms().contains(algorithm));
				break;
			}
			case 3:
			{
				final Query<?> query = getQuery(media);
				if(!query.getType().getModel().isConnected())
				{
					out.writeRaw("not yet connected");
					writeUnknown(out, true);
					writeNotConnected(out, this);
					out.writeRaw("<small>");
					out.write(query.toString());
					out.writeRaw("</small>");
					break;
				}
				final String sql;
				try
				{
					sql = SchemaInfo.total(query);
				}
				catch(final IllegalArgumentException e)
				{
					// hash is not supported
					out.write(e.getMessage());
					writeError(out, true);
					out.writeRaw("<br><small>");
					out.write(query.toString());
					out.writeRaw("</small>");
					break;
				}
				out.write(sql);
				break;
			}
			default:
				throw new RuntimeException(String.valueOf(h));
		}
	}

	private static Query<?> getQuery(final HashConstraint field)
	{
		return field.getType().newQuery(field.hashDoesNotMatchIfSupported());
	}

	@Override
	String getID(final HashConstraint media)
	{
		return media.getID();
	}

	@Override
	HashConstraint forID(final Model model, final String id)
	{
		return (HashConstraint)model.getFeature(id);
	}

	@Override
	long check(final HashConstraint media, final Model model)
	{
		try(TransactionTry tx = model.startTransactionTry("Console HashConstraint " + id))
		{
			return tx.commit(
					getQuery(media).total());
		}
	}
}
