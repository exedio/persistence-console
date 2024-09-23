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
import com.exedio.cope.UnsupportedQueryException;
import com.exedio.cope.pattern.HashConstraint;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaPath;
import java.util.ArrayList;
import java.util.List;

final class HashConstraintCop extends TestCop<HashConstraint>
{
	static final String TAB = "hashconstraint";

	HashConstraintCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Hash Constraints", args, testArgs);
	}

	@Override
	protected HashConstraintCop newArgs(final Args args)
	{
		return new HashConstraintCop(args, testArgs);
	}

	@Override
	protected HashConstraintCop newTestArgs(final TestArgs testArgs)
	{
		return new HashConstraintCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Verifies that hashes do match its actual data.",
			"IMPACT: " +
				"Any failure may cause arbitrary problems in your application. " +
				"If the HashConstraint was declared by a UniqueHashedMedia, all contracts of UniqueHashedMedia are void. " +
				"Your application may either fail with errors or silently destroy your data. " +
				"DANGER ZONE."
		};
	}

	@Override
	List<HashConstraint> getItems()
	{
		final ArrayList<HashConstraint> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
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
	int getNumberOfFilterableColumns()
	{
		return 1;
	}

	@Override
	void writeValue(final Out out, final HashConstraint constraint, final int h)
	{
		final Pattern pattern = constraint.getData().getPattern();
		switch(h)
		{
			case 0 -> {
				if(pattern instanceof MediaPath)
				{
					out.writeRaw("<a href=\"");
					out.write(new MediaCop(args, (MediaPath)pattern));
					out.writeRaw("\">");
				}

				out.write(constraint.getID());

				if(pattern instanceof Media)
					out.writeRaw("</a>");

			}
			case 1 -> {
				if(pattern instanceof Media)
					out.write(((Media)pattern).getContentTypeDescription().replaceAll(",", ", "));
			}
			case 2 ->
			{
				final String algorithm = constraint.getAlgorithm();
				out.write(algorithm);
				final Model model = constraint.getType().getModel();
				writeError(out,
						model.isConnected() &&
						!model.getSupportedDataHashAlgorithms().contains(algorithm));
			}
			case 3 ->
			{
				final Query<?> query = getQuery(constraint);
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
			}
			default ->
				throw new RuntimeException(String.valueOf(h));
		}
	}

	private static Query<?> getQuery(final HashConstraint constraint)
	{
		return constraint.getType().newQuery(constraint.hashDoesNotMatchIfSupported());
	}

	@Override
	String getID(final HashConstraint constraint)
	{
		return constraint.getID();
	}

	@Override
	HashConstraint forID(final String id)
	{
		return (HashConstraint)app.model.getFeature(id);
	}

	@Override
	long check(final HashConstraint constraint)
	{
		try(TransactionTry tx = app.model.startTransactionTry("Console HashConstraint " + id))
		{
			return tx.commit(
					getQuery(constraint).total());
		}
	}

	@Override
	String getViolationSql(final HashConstraint constraint)
	{
		try
		{
			return SchemaInfo.search(getQuery(constraint));
		}
		catch(final UnsupportedQueryException e)
		{
			return e.getMessage(); // happens typically when hash algorithm is not supported
		}
	}
}
