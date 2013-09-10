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

import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.SequenceInfo;
import com.exedio.cope.This;
import java.util.ArrayList;

final class MysqlSequenceCop extends ConsoleCop<Void>
{
	MysqlSequenceCop(final Args args)
	{
		super(TAB_MYSQL_SEQUENCE, "MySQL Sequences", args);
	}

	@Override
	protected MysqlSequenceCop newArgs(final Args args)
	{
		return new MysqlSequenceCop(args);
	}

	@Override
	final void writeBody(final Out out)
	{
		final Model model = out.model;
		final ArrayList<String> sequences = new ArrayList<String>();
		for(final SequenceInfo info : model.getSequenceInfo() )
		{
			final Feature feature = info.getFeature();
			final String name;
			if(feature instanceof This)
			{
				name = SchemaInfo.getPrimaryKeySequenceName(feature.getType());
			}
			else if(feature instanceof IntegerField)
			{
				name = SchemaInfo.getDefaultToNextSequenceName((IntegerField)feature);
			}
			else
				continue;

			sequences.add(SchemaInfo.quoteName(model, name));
		}
		MysqlSequence_Jspm.write(out, sequences);
	}
}
