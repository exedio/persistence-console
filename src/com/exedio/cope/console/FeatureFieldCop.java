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
import com.exedio.cope.Query;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.reflect.FeatureField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;

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
		final ArrayList<FeatureField<?>> result = new ArrayList<>();

		for(final Type<?> type : model.getTypes())
		{
			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof FeatureField)
					result.add((FeatureField<?>)feature);
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
	void writeValue(final Out out, final FeatureField<?> field, final int h)
	{
		switch(h)
		{
			case 0: out.write(field.getType().getID()); break;
			case 1: out.write(field.getName()); break;
			case 2: writeValueLong(out, field.getValues().toString()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final FeatureField<?> field)
	{
		return field.getID();
	}

	@Override
	FeatureField<?> forID(final Model model, final String id)
	{
		return (FeatureField<?>)model.getFeature(id);
	}

	@Override
	@SuppressFBWarnings({"NP_LOAD_OF_KNOWN_NULL_VALUE","RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE"}) // OK: caused by try-with-resources
	long check(final FeatureField<?> field, final Model model)
	{
		final Query<?> query = field.getType().newQuery(field.isInvalid());
		try(TransactionTry tx = model.startTransactionTry("Console FeatureField " + id))
		{
			return tx.commit(
					query.total());
		}
	}
}
