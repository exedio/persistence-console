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

package com.exedio.cope.console.example;

import static com.exedio.cope.instrument.Visibility.PRIVATE;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Singleton;

@WrapperType(constructor=PRIVATE)
@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // OK: for example TYPE
final class SingletonComplete extends Item
{
	static final Singleton feature = new Singleton();

	@SuppressWarnings("ResultOfObjectAllocationIgnored") // OK
	static void createSampleData()
	{
		new SingletonComplete();
	}

	/**
	 * Creates a new SingletonComplete with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression"})
	private SingletonComplete()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new SingletonComplete and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private SingletonComplete(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Gets the single instance of singletonComplete.
	 * Creates an instance, if none exists.
	 * @return never returns null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="instance")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final SingletonComplete instance()
	{
		return SingletonComplete.feature.instance(SingletonComplete.class);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for singletonComplete.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<SingletonComplete> TYPE = com.exedio.cope.TypesBound.newType(SingletonComplete.class,SingletonComplete::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private SingletonComplete(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
