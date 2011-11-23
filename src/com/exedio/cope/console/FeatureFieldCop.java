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

import java.util.ArrayList;
import java.util.List;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.reflect.FeatureField;

final class FeatureFieldCop extends TestCop<FeatureField<?>>
{
	FeatureFieldCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_FEATURE_FIELD, "Feature Fields", args, testArgs);
	}

	@Override
	protected FeatureFieldCop newArgs(final Args args)
	{
		return new FeatureFieldCop(args, testArgs);
	}

	@Override
	protected FeatureFieldCop newTestArgs(final TestArgs testArgs)
	{
		return new FeatureFieldCop(args, testArgs);
	}

	@Override
	List<FeatureField<?>> getItems(final Model model)
	{
		final ArrayList<FeatureField<?>> result = new ArrayList<FeatureField<?>>();

		for(final Type<?> type : model.getTypes())
		{
			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof FeatureField)
					result.add((FeatureField)feature);
			}
		}
		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Type", "Name", "Values"};
	}

	@Override
	void writeValue(final Out out, final FeatureField field, final int h)
	{
		switch(h)
		{
			case 0: out.write(field.getType().getID()); break;
			case 1: out.write(field.getName()); break;
			case 2: out.write(field.getValues().toString()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}

	@Override
	String getID(final FeatureField field)
	{
		return field.getID();
	}

	@Override
	FeatureField forID(final Model model, final String id)
	{
		return (FeatureField)model.getFeature(id);
	}

	@Override
	int check(final FeatureField<?> field)
	{
		final ArrayList<String> ids = new ArrayList<String>();
		for(final Feature feature : field.getValues())
			ids.add(feature.getID());

		final Query query = field.getType().newQuery(field.getIdField().in(ids).not());
		final Model model = field.getType().getModel();
		try
		{
			model.startTransaction("Console FeatureField " + id);
			final int result = query.total();
			model.commit();
			return result;
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}
}
