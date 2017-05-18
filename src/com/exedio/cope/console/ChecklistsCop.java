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

final class ChecklistsCop extends ConsoleCop<Void>
{
	ChecklistsCop(final Args args)
	{
		super(TAB_CHECKLISTS, "Checklists", args);
	}

	@Override
	protected ChecklistsCop newArgs(final Args args)
	{
		return new ChecklistsCop(args);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Organizes menu items into checklists for certain situations. " +
				"All items listed here can be found somewhere else in the menu as well."
		};
	}

	@Override
	void writeBody(final Out out)
	{
		final TestCop.TestArgs testArgs = new TestCop.TestArgs();
		Checklists_Jspm.writeBody(out,
				new ConsoleCop<?>[]{ // schema
						new SchemaCop(args),
						new UnsupportedCheckConstraintByTableCop(args, testArgs),
						new MultiTableCheckConstraintCop(args, testArgs),
						new UpdateCounterCop(args, testArgs),
						new SequenceCop(args, testArgs),
						new TypeColumnCop(args, testArgs),
						new TypeCompletenessCop(args, testArgs),
						new CopyConstraintCop(args, testArgs),
						new MediaTypeCop(args, testArgs),
						new UniqueHashedMediaCop(args, testArgs)
				},
				new ConsoleCop<?>[]{ // machine
						new SavepointCop(args),
						new MediaStatsCop(args, MediaStatsCop.Variant.guessingPrevented),
						new MediaTestableCop(args, testArgs),
				},
				new ConsoleCop<?>[]{ // application
						new SerializationCheckCop(args),
				},
				new ConsoleCop<?>[]{ // operations
						new TransactionCop(args),
						new ConnectionPoolCop(args),
						new ItemCacheCop(args),
						new QueryCacheCop(args),
						new ThreadCop(args),
						new ChangeListenerCop(args),
						new ClusterCop(args),
						new MediaStatsCop(args, MediaStatsCop.Variant.all),
						new HashCop(args)
				});
	}
}
