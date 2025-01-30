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
import com.exedio.cope.pattern.EnumSingleton;

@WrapperType(constructor=PRIVATE)
@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // OK: for example TYPE
final class EnumSingletonComplete extends Item
{
	static final EnumSingleton<MyEnum> feature = EnumSingleton.create(MyEnum.class);

	enum MyEnum { one, two, three }

	@SuppressWarnings("ResultOfObjectAllocationIgnored") // OK
	static void createSampleData()
	{
		new EnumSingletonComplete(MyEnum.one);
		new EnumSingletonComplete(MyEnum.two);
		new EnumSingletonComplete(MyEnum.three);
	}

	/**
	 * Creates a new EnumSingletonComplete with all the fields initially needed.
	 * @param feature the initial value for field {@link #feature}.
	 * @throws com.exedio.cope.MandatoryViolationException if feature is null.
	 * @throws com.exedio.cope.UniqueViolationException if feature is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression"})
	private EnumSingletonComplete(
				@javax.annotation.Nonnull final MyEnum feature)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(EnumSingletonComplete.feature,feature),
		});
	}

	/**
	 * Creates a new EnumSingletonComplete and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private EnumSingletonComplete(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #feature}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final MyEnum getFeature()
	{
		return EnumSingletonComplete.feature.get(this);
	}

	/**
	 * Gets the instance of enumSingletonComplete for the given value.
	 * @return never returns null.
	 * @throws java.lang.IllegalArgumentException if no such instance exists
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="instance")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final EnumSingletonComplete instance(@javax.annotation.Nonnull final MyEnum feature)
			throws
				java.lang.IllegalArgumentException
	{
		return EnumSingletonComplete.feature.instance(EnumSingletonComplete.class,feature);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for enumSingletonComplete.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<EnumSingletonComplete> TYPE = com.exedio.cope.TypesBound.newType(EnumSingletonComplete.class,EnumSingletonComplete::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private EnumSingletonComplete(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
