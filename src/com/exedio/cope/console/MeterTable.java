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

import static java.util.Objects.requireNonNull;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

final class MeterTable
{
	private final LinkedHashMap<Meter.Id, Column> columns = new LinkedHashMap<>();
	private final LinkedHashMap<String, Row> rows = new LinkedHashMap<>();
	private final HashMap<CellKey, CellValue> cells = new HashMap<>();

	@SuppressWarnings("unused")
	void addColumnWhite(final String name)
	{
		addColumnWhite(name, Tags.empty());
	}

	void addColumnWhite(final String name, final Tags tags)
	{
		addColumn(name, tags, false);
	}

	void addColumnBlue(final String name)
	{
		addColumnBlue(name, Tags.empty());
	}

	void addColumnBlue(final String name, final Tags tags)
	{
		addColumn(name, tags, true);
	}

	private void addColumn(final String name, final Tags tags, final boolean blue)
	{
		final Meter.Id id = new Meter.Id(name, tags, null, null, Meter.Type.COUNTER);
		columns.put(id, new Column(id, blue));
	}

	void addRow(final String rowId)
	{
		rows.put(rowId, new Row(rowId));
	}

	Collection<Column> getColumns()
	{
		return Collections.unmodifiableCollection(columns.values());
	}

	Collection<Column> getColumns(final int fromIndex)
	{
		final ArrayList<Column> list = new ArrayList<>(columns.values());
		return Collections.unmodifiableCollection(list.subList(fromIndex, list.size()));
	}

	Collection<Row> getRows()
	{
		return Collections.unmodifiableCollection(rows.values());
	}

	CellValue getCell(final Column column, final Row row)
	{
		return cells.get(new CellKey(column.initialId, row.id));
	}


	static void fillup(
			final List<ListItem> list,
			final MeterTable table,
			final String prefix,
			final String filterKey, final String filterValue,
			final String rowKey)
	{
		for(final Meter m : Metrics.globalRegistry.getMeters())
		{

			final Meter.Id id = m.getId();
			if(!id.getName().startsWith(prefix) ||
				!filterValue.equals(id.getTag(filterKey)))
				continue;

			final List<Tag> tags = new ArrayList<>(id.getTags());
			tags.removeIf(t -> filterKey.equals(t.getKey()) || rowKey.equals(t.getKey()));
			final String row = id.getTag(rowKey);
			final Meter.Id newId = id.
					withName(id.getName().substring(prefix.length())).
					replaceTags(tags);
			if(row==null)
				list.add(new ListItem(newId, m));
			else
				table.add(newId, row, m);
		}
	}

	static final class ListItem
	{
		final Meter.Id id;
		final Meter.Id fullId;
		final double value;

		ListItem(final Meter.Id id, final Meter meter)
		{
			this.id = id;
			this.fullId = meter.getId();
			this.value = value(meter);
		}
	}

	private void add(final Meter.Id columnId, final String rowId, final Meter meter)
	{
		requireNonNull(columnId);
		requireNonNull(rowId);
		requireNonNull(meter);

		final Column column =
				columns.computeIfAbsent(columnId, i -> new Column(i, false));
		final Row row =
				rows.computeIfAbsent(rowId, Row::new);
		final CellValue value = new CellValue(meter.getId(), value(meter));
		if(cells.putIfAbsent(new CellKey(columnId, rowId), value)!=null)
			throw new RuntimeException(columnId+"/"+rowId);
		column.add(value);
		row.add(value);
	}

	private static double value(final Meter meter)
	{
		if(meter instanceof Counter)
			return ((Counter)meter).count();
		else if(meter instanceof Gauge)
			return ((Gauge)meter).value();
		else
			return Double.NaN;
	}

	private static final class CellKey
	{
		final Meter.Id column;
		final String row;

		private CellKey(final Meter.Id column, final String row)
		{
			this.column = requireNonNull(column);
			this.row = requireNonNull(row);
		}

		@Override
		public boolean equals(final Object other)
		{
			if(!(other instanceof CellKey))
				return false;

			final CellKey o = (CellKey)other;
			return column.equals(o.column) && row.equals(o.row);
		}

		@Override
		public int hashCode()
		{
			return column.hashCode()*31 + row.hashCode();
		}
	}

	static final class CellValue
	{
		final Meter.Id id;
		final double value;

		private CellValue(final Meter.Id id, final double value)
		{
			this.id = requireNonNull(id);
			this.value = value;
		}
	}

	static final class Column
	{
		final Meter.Id initialId;
		private Meter.Id cellId = null;
		private final boolean blue;
		private double sum = 0.0;

		Column(final Meter.Id initialId, final boolean blue)
		{
			this.initialId = requireNonNull(initialId);
			this.blue = blue;
		}

		Meter.Id cellId()
		{
			return cellId!=null ? cellId : initialId;
		}

		boolean isBlue()
		{
			return blue;
		}

		void add(final CellValue cell)
		{
			if(cellId==null)
				cellId = cell.id;
			sum += cell.value;
		}

		double getSum()
		{
			return sum;
		}
	}

	static final class Row
	{
		final String id;
		private boolean empty = true;

		Row(final String id)
		{
			this.id = requireNonNull(id);
		}

		void add(final CellValue cell)
		{
			if(cell.value!=0.0)
				empty = false;
		}

		boolean isEmpty()
		{
			return empty;
		}
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
	static void write(final double d, final Out out)
	{
		if(d==0.0)
			return;

		final long l = (long)d;
		//noinspection FloatingPointEquality
		if(l==d)
			out.write(l);
		else
			out.write(d);
	}
}
