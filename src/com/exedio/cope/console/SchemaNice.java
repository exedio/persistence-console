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

import static com.exedio.cope.SchemaInfo.quoteName;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.exedio.cope.DateField;
import com.exedio.cope.EnumField;
import com.exedio.cope.Field;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.Type;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

final class SchemaNice
{
	private final Model model;

	SchemaNice(final Model model)
	{
		this.model = model;
	}

	void create() throws SQLException
	{
		final Connection connection = SchemaInfo.newConnection(model);
		try
		{
			final StringBuilder bf = new StringBuilder();

			for(final Type<?> type : model.getTypes())
			{
				bf.append("create view ").
					append(view(type)).
					append(" as (select ").
					append(table(type)).append('.').append(column(type));

				for(Type<?> superType = type; superType!=null; superType = superType.getSupertype())
				{
					for(final Field field : superType.getDeclaredFields())
					{
						bf.append(',');
						if(field instanceof EnumField)
						{
							bf.append("case ").
								append(table(superType)).append('.').append(column(field));

							for(final Enum v : ((EnumField<?>)field).getValueClass().getEnumConstants())
							{
								@SuppressWarnings("unchecked")
								final int columnValue = SchemaInfo.getColumnValue(v);
								bf.append(" when ").
									append(columnValue).
									append(" then '").
									append(v.name()).
									append('\'');
							}
							bf.append(" end as ").append(column(field));
						}
						else if(field instanceof DateField && !SchemaInfo.supportsNativeDate(model))
						{
							bf.append("from_unixtime(").
								append(table(superType)).append('.').append(column(field)).
								append("/1000) as ").append(column(field));
						}
						else
						{
							bf.append(table(superType)).append('.').append(column(field));
						}
					}
				}

				bf.append(" from ").
					append(table(type));

				for(Type<?> superType = type.getSupertype(); superType!=null; superType=superType.getSupertype())
				{
					bf.append(" join ").
						append(table(superType)).
						append(" on ").
						append(table(superType)).append('.').append(column(superType)).
						append('=').
						append(table(type)).append('.').append(column(type));
				}

				bf.append(')');

				execute(connection, bf);
			}
		}
		finally
		{
			connection.close();
		}
	}

	void drop() throws SQLException
	{
		final Connection connection = SchemaInfo.newConnection(model);
		try
		{
			final StringBuilder bf = new StringBuilder();

			for(final Type<?> type : model.getTypes())
			{
				bf.append("drop view if exists ").
					append(view(type));
				execute(connection, bf);
			}
		}
		finally
		{
			connection.close();
		}
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private static void execute(final Connection connection, final StringBuilder bf) throws SQLException
	{
		System.out.println(bf.toString());
		final Statement statement = connection.createStatement();
		try
		{
			statement.execute(bf.toString());
		}
		finally
		{
			statement.close();
		}
		bf.setLength(0);
	}

	private String table(final Type type)
	{
		return quoteName(model, SchemaInfo.getTableName(type));
	}

	private String view(final Type type)
	{
		return quoteName(model, SchemaInfo.getTableName(type)+'V');
	}

	private String column(final Type type)
	{
		return quoteName(model, SchemaInfo.getPrimaryKeyColumnName(type));
	}

	private String column(final Field field)
	{
		return quoteName(model, SchemaInfo.getColumnName(field));
	}
}
