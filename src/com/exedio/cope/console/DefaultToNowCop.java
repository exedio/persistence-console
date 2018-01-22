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

import com.exedio.cope.DateField;
import com.exedio.cope.DayField;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.util.Day;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

final class DefaultToNowCop extends ConsoleCop<Void>
{
	static final String TAB = "defaultToNow";

	DefaultToNowCop(final Args args)
	{
		super(TAB, "Default To Now", args);
	}

	@Override
	protected DefaultToNowCop newArgs(final Args args)
	{
		return new DefaultToNowCop(args);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Checks DateFields and DayFields whether their default constants are equal or close to time of model initialization.",
			"This means, that you probably should have used defaultToNow instead."
		};
	}

	private static List<FunctionField<?>> fields(final Model model)
	{
		ArrayList<FunctionField<?>> result = null;
		for(final Type<?> t : model.getTypes())
			for(final Field<?> f : t.getDeclaredFields())
				if(f instanceof DateField || f instanceof DayField)
				{
					final FunctionField<?> ff = (FunctionField<?>)f;
					if(ff.getDefaultConstant()!=null)
					{
						if(result==null)
							result = new ArrayList<>();

						result.add(ff);
					}
				}
		return result!=null ? result : Collections.emptyList();
	}

	static boolean isSuspicious(final FunctionField<?> field)
	{
		final Date modelDate = field.getType().getModel().getInitializeDate();

		if(field instanceof DateField)
		{
			final DateField f = (DateField)field;
			return Math.abs(modelDate.getTime()-f.getDefaultConstant().getTime())<1000;
		}
		else
		{
			final DayField f = (DayField)field;
			return f.getDefaultConstant().equals(new Day(modelDate, TimeZone.getDefault()));
		}
	}

	@Override
	ChecklistIcon getChecklistIcon(final Model model)
	{
		final List<FunctionField<?>> fields = fields(model);
		if(fields.isEmpty())
			return ChecklistIcon.empty;

		for(final FunctionField<?> ff : fields)
			if(isSuspicious(ff))
				return ChecklistIcon.error;

		return ChecklistIcon.ok;
	}

	@Override
	void writeBody(final Out out)
	{
		DefaultToNow_Jspm.writeBody(out, fields(out.model));
	}
}
