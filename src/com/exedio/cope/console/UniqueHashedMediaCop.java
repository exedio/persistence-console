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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.SchemaInfo.quoteName;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.UniqueHashedMedia;
import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

final class UniqueHashedMediaCop extends TestCop<UniqueHashedMedia>
{
	UniqueHashedMediaCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_UNIQUE_HASHED_MEDIA, "Unique Hashed Media", args, testArgs);
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
	String getHeadingHelp()
	{
		return
				"Verifies whether hash of UniqueHashedMedia is consistent to its actual data. " +
				"IMPACT: " +
				"Any failures here invalidate all contracts of UniqueHashedMedia. " +
				"Your application may either fail with errors or silently destroy your data stored in UniqueHashedMedia. " +
				"DANGER ZONE.";
	}

	@Override
	List<UniqueHashedMedia> getItems(final Model model)
	{
		final ArrayList<UniqueHashedMedia> result = new ArrayList<>();

		for(final Type<?> type : model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof UniqueHashedMedia)
					result.add((UniqueHashedMedia)feature);

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"ID", "Content Type", "Hash", "SQL"};
	}

	@Override
	void writeValue(final Out out, final UniqueHashedMedia media, final int h)
	{
		switch(h)
		{
			case 0:
				out.writeRaw("<a href=\"");
				out.write(new MediaCop(args, media.getMedia()));
				out.writeRaw("\">");
				out.write(media.getID());
				out.writeRaw("</a>");
				break;
			case 1:
				out.write(media.getMedia().getContentTypeDescription().replaceAll(",", ", "));
				break;
			case 2:
				out.write(media.getMessageDigestAlgorithm());
				break;
			case 3:
				out.write(getSQL(media));
				break;
			default:
				throw new RuntimeException(String.valueOf(h));
		}
	}

	private String getSQL(final UniqueHashedMedia field)
	{
		final Type<?> type = field.getType();
		final Model model = type.getModel();
		if(!model.isConnected())
			return "NOT YET CONNECTED";

		return
				"SELECT COUNT(*) " +
				"FROM "  + quoteName(model, getTableName(type)) + " " +
				"WHERE " + quoteName(model, getColumnName(field.getHash())) + "<>" + field.getMessageDigestAlgorithm() +
				'('      + quoteName(model, getColumnName(field.getMedia().getBody())) + ')';
	}

	@Override
	String getID(final UniqueHashedMedia media)
	{
		return media.getID();
	}

	@Override
	UniqueHashedMedia forID(final Model model, final String id)
	{
		return (UniqueHashedMedia)model.getFeature(id);
	}

	@Override
	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	long check(final UniqueHashedMedia media, final Model model)
	{
		// TODO use a check method in cope
		try(
			Connection con = newConnection(model);
			Statement st = con.createStatement())
		{
			try(ResultSet rs = st.executeQuery(getSQL(media)))
			{
				rs.next();
				return rs.getLong(1);
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, media.toString());
		}
	}
}
