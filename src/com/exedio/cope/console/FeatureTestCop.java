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
import com.exedio.cope.Field;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

abstract class FeatureTestCop<I extends Feature> extends TestCop<I>
{
	private final Class<I> clazz;

	protected FeatureTestCop(
			final Class<I> clazz,
			final String tab, final String name,
			final Args args, final TestArgs testArgs)
	{
		super(tab, name, args, testArgs);
		this.clazz = clazz;
	}

	@Override
	List<I> getItems()
	{
		final Function<Type<?>, List<? extends Feature>> featuresByType;
		if(Field.class.isAssignableFrom(clazz))
			featuresByType = Type::getDeclaredFields;
		else
			featuresByType = Type::getDeclaredFeatures;

		final ArrayList<I> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
		{
			for(final Feature feature : featuresByType.apply(type))
			{
				if(clazz.isInstance(feature))
				{
					final I featureCasted = clazz.cast(feature);
					if(acceptsItem(featureCasted))
						result.add(featureCasted);
				}
			}
		}
		return result;
	}

	boolean acceptsItem(final I feature)
	{
		return true;
	}

	@Override
	final String getID(final I feature)
	{
		return feature.getID();
	}

	@Override
	final I forID(final String id)
	{
		return clazz.cast(app.model.getFeature(id));
	}
}
