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

import static com.exedio.cope.console.Format.format;

import java.util.List;

import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.SequenceInfo;
import com.exedio.cope.This;

final class SequenceCop extends TestAjaxCop<SequenceInfo>
{
	SequenceCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_SEQUENCE, "Sequences", args, testArgs);
	}

	@Override
	protected SequenceCop newArgs(final Args args)
	{
		return new SequenceCop(args, testArgs);
	}

	@Override
	protected SequenceCop newTestArgs(final TestArgs testArgs)
	{
		return new SequenceCop(args, testArgs);
	}

	@Override
	List<SequenceInfo> getItems(final Model model)
	{
		return model.getSequenceInfo();
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Type", "Name", "Start", "Min", "Max", "Count", "First", "Last"};
	}

	@Override
	void writeValue(final Out out, final SequenceInfo info, final int h)
	{
		final Feature feature = info.getFeature();
		final boolean known = info.isKnown();
		switch(h)
		{
			case 0: out.write(feature.getType().getID()); break;
			case 1: out.write(feature.getName()); break;
			case 2: out.write(format(info.getStart())); break;
			case 3: out.write(format(info.getMinimum())); break;
			case 4: out.write(format(info.getMaximum())); break;
			case 5: out.write(format(info.getCount())); break;
			case 6: if(known) out.write(format(info.getFirst())); break;
			case 7: if(known) out.write(format(info.getLast())); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final SequenceInfo info)
	{
		return info.getFeature().getID();
	}

	@Override
	SequenceInfo forID(final Model model, final String id)
	{
		final Feature feature = model.getFeature(id);
		if(feature instanceof This)
			return ((This)feature).getType().getPrimaryKeyInfo();
		else if(feature instanceof IntegerField)
			return ((IntegerField)feature).getDefaultToNextInfo();
		else
			throw new RuntimeException(feature.toString());
	}

	@Override
	int check(final SequenceInfo info)
	{
		final Feature feature = info.getFeature();
		if(feature instanceof This)
			return ((This)feature).getType().checkPrimaryKey();
		else if(feature instanceof IntegerField)
			return ((IntegerField)feature).checkDefaultToNext();
		else
			throw new RuntimeException(feature.toString());
	}
}
