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
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class SuspicionsCop extends ConsoleCop<Void>
{
	static final String TAB = "defaultToNow";

	SuspicionsCop(final Args args)
	{
		super(TAB, "Suspicions", args);
	}

	@Override
	protected SuspicionsCop newArgs(final Args args)
	{
		return new SuspicionsCop(args);
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

	private static Map<Feature,Collection<String>> features(final Model model)
	{
		LinkedHashMap<Feature,Collection<String>> result = null;
		for(final Type<?> t : model.getTypes())
			for(final Feature f : t.getDeclaredFeatures())
			{
				final Collection<String> suspicions = f.getSuspicions();
				if(!suspicions.isEmpty())
				{
					if(result==null)
						result = new LinkedHashMap<>();

					result.put(f, suspicions);
				}
			}
		return result!=null ? result : Collections.emptyMap();
	}

	@Override
	ChecklistIcon getChecklistIcon(final Model model)
	{
		return
				features(model).isEmpty()
				? ChecklistIcon.empty
				: ChecklistIcon.error;
	}

	@Override
	void writeBody(final Out out)
	{
		Suspicions_Jspm.writeBody(out, features(out.model));
	}
}
