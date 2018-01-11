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

import com.exedio.cope.DataField;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.Type;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

final class DataFieldCop extends ConsoleCop<Void>
{
	static final String TAB_DATA_FIELD = "datafield";

	DataFieldCop(final Args args)
	{
		super(TAB_DATA_FIELD, "Data Fields", args);
	}

	@Override
	protected DataFieldCop newArgs(final Args args)
	{
		return new DataFieldCop(args);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Here you can see sizes of data fields (blob columns). " +
				"Table \"Tables\" aggregates sizes of multiple columns within a table.",
			"This helps to configure your database, such as max_allowed_packet on MySQL.",
		};
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = out.model;

		final ArrayList<DataField> fields = new ArrayList<>();
		long lengthMax = 0;

		for(final Type<?> type : model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof DataField)
				{
					final DataField field = (DataField)feature;
					fields.add(field);
					if(lengthMax<field.getMaximumLength())
						lengthMax = field.getMaximumLength();
				}

		final Collection<Table> tables = tableInfo(model);
		long tablesMax = 0;
		if(tables!=null)
			for(final Table table : tables)
				if(tablesMax<table.getSize())
					tablesMax = table.getSize();

		DataField_Jspm.writeBody(out, this, tables, tablesMax, fields, lengthMax);
	}

	@SuppressWarnings("ComparableImplementedButEqualsNotOverridden") // OK: name is unique
	static final class Table implements Comparable<Table>
	{
		final String name;
		private long size = 0;
		private int count = 0;

		Table(final String name)
		{
			this.name = name;
		}

		void add(final long size)
		{
			this.size += size;
			count++;
		}

		long getSize()
		{
			return size;
		}

		int getCount()
		{
			return count;
		}

		@Override
		@SuppressFBWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS") // OK: name is unique
		public int compareTo(final Table other)
		{
			@SuppressWarnings("CompareToUsesNonFinalVariable") // OK: is not modified anymore after has been put into TreeSet
			final int sizeResult = Long.compare(size, other.size);
			if(sizeResult!=0)
				return sizeResult;

			return name.compareTo(other.name);
		}
	}

	private static Collection<Table> tableInfo(final Model model)
	{
		if(model.isConnected())
		{
			final HashMap<String, Table> result = new HashMap<>();

			for(final Type<?> type : model.getTypes())
				for(final Feature feature : type.getDeclaredFeatures())
					if(feature instanceof DataField)
					{
						final DataField field = (DataField)feature;
						final String tableName = SchemaInfo.getTableName(field.getType());
						Table table = result.get(tableName);
						if(table==null)
						{
							table = new Table(tableName);
							if(result.put(tableName, table)!=null)
								throw new RuntimeException(tableName);
						}
						table.add(field.getMaximumLength());
					}

			return new TreeSet<>(result.values());
		}
		else
		{
			return null;
		}
	}
}
