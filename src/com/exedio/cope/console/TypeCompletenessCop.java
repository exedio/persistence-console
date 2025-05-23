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

import static com.exedio.cope.console.SchemaCop.HELP_IMPACT_FATAL;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.List;

final class TypeCompletenessCop extends TestCop<TypeCompletenessCop.Constraint<?>>
{
	static final String TAB = "typecompleteness";

	TypeCompletenessCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Type Completeness", args, testArgs);
	}

	@Override
	protected TypeCompletenessCop newArgs(final Args args)
	{
		return new TypeCompletenessCop(args, testArgs);
	}

	@Override
	protected TypeCompletenessCop newTestArgs(final TestArgs testArgs)
	{
		return new TypeCompletenessCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"For type hierarchies, verifies that each item has a row in all relevant tables.",
			HELP_IMPACT_FATAL
		};
	}

	@Override
	List<Constraint<?>> getItems()
	{
		final ArrayList<Constraint<?>> result = new ArrayList<>();

		for(final Type<?> superType : app.model.getTypes())
			addTypes(superType, result);

		return result;
	}

	private static <T extends Item> void addTypes(
			final Type<T> superType,
			final ArrayList<Constraint<?>> result)
	{
		for(final Type<? extends T> subType : superType.getTypesOfInstances())
			if(!superType.equals(subType))
				result.add(new Constraint<>(superType, subType));
	}

	@Override
	List<Column<Constraint<?>>> columns()
	{
		return COLUMNS;
	}

	private static final List<Column<Constraint<?>>> COLUMNS = List.of(
			column("Supertype", constraint -> constraint.superType.toString()),
			column("Subtype",   constraint -> constraint.subType  .toString())
	);

	@Override
	String getID(final Constraint<?> constraint)
	{
		return constraint.getID();
	}

	@Override
	Constraint<?> forID(final String id)
	{
		return Constraint.forID(app.model, id);
	}

	@Override
	long check(final Constraint<?> constraint)
	{
		return constraint.check(app.model);
	}

	@Override
	String getViolationSql(final Constraint<?> constraint)
	{
		return constraint.getViolationSql();
	}

	record Constraint<T extends Item>(Type<T> superType, Type<? extends T> subType)
	{
		String getID()
		{
			return superType.getID() + ID_SEPARATOR + subType.getID();
		}

		static Constraint<?> forID(final Model model, final String id)
		{
			final int pos = id.indexOf(ID_SEPARATOR);
			assert pos>0 : id;
			return getConstraint(
					model.getType(id.substring(0, pos)),
					model.getType(id.substring(pos+1)));
		}

		private static <T extends Item> Constraint<?> getConstraint(final Type<T> superType, final Type<?> subType)
		{
			return new Constraint<>(
					superType,
					superType.castTypeExtends(subType));
		}

		private static final char ID_SEPARATOR = '#';

		long check(final Model model)
		{
			try(TransactionTry tx = model.startTransactionTry("Console TypeCompleteness " + superType + ' ' + subType))
			{
				return tx.commit(
						superType.checkCompletenessL(subType));
			}
		}

		String getViolationSql()
		{
			return SchemaInfo.checkCompleteness(superType, subType);
		}
	}
}
