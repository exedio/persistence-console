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
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.util.CharSet;
import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

final class CharacterNulCop extends TestCop<StringField>
{
	CharacterNulCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_CHARACTER_NUL, "Character Nul", args, testArgs);
	}

	@Override
	protected CharacterNulCop newArgs(final Args args)
	{
		return new CharacterNulCop(args, testArgs);
	}

	@Override
	protected CharacterNulCop newTestArgs(final TestArgs testArgs)
	{
		return new CharacterNulCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Checks string fields for character NUL. " +
				"This character is not supported by PostgreSQL. " +
				"This check currently works on MySQL only.",
			"IMPACT: " +
				"A failure here means that you cannot migrate your data to PostgreSQL."
		};
	}

	@Override
	List<StringField> getItems(final Model model)
	{
		final ArrayList<StringField> result = new ArrayList<>();

		for(final Type<?> type : model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof StringField)
					result.add((StringField)feature);

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Field", "CharSet", "SQL"};
	}

	@Override
	void writeValue(final Out out, final StringField field, final int h)
	{
		switch(h)
		{
			case 0: out.write(field.getID()); break;
			case 1:
				final CharSet charSet = field.getCharSet();
				if(charSet!=null)
				{
					out.write(charSet.toString());
					if(charSet.contains('\0'))
						out.write(" (contains NUL)");
				}
				break;
			case 2: out.write(getSQL(field)); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}

	private static String getSQL(final StringField field)
	{
		final Type<?> type = field.getType();
		final Model model = type.getModel();
		if(!model.isConnected())
			return "NOT YET CONNECTED";

		return
				"SELECT COUNT(*) " +
				"FROM "  + quoteName(model, getTableName(type)) + " " +
				"WHERE " + quoteName(model, getColumnName(field)) + " LIKE '%\\0%'";
	}

	@Override
	String getID(final StringField field)
	{
		return ((Feature)field).getID();
	}

	@Override
	StringField forID(final Model model, final String id)
	{
		return (StringField)model.getFeature(id);
	}

	/**
	 * Works on MySQL only.
	 */
	@Override
	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	long check(final StringField field, final Model model)
	{
		try(
			Connection con = newConnection(model);
			Statement st = con.createStatement())
		{
			// drop NO_BACKSLASH_ESCAPES, otherwise \0 will not work
			st.execute(
					"SET SESSION sql_mode = '" +
					"ONLY_FULL_GROUP_BY," +
					"STRICT_ALL_TABLES," +
					"NO_ZERO_IN_DATE," +
					"NO_ZERO_DATE," +
					"NO_ENGINE_SUBSTITUTION'");

			try(ResultSet rs = st.executeQuery(getSQL(field)))
			{
				rs.next();
				return rs.getLong(1);
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, field.toString());
		}
	}
}
