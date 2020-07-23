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

import com.exedio.cope.CopeCreateLimit;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;

@CopeCreateLimit(Long.MAX_VALUE)
final class OptionalItem extends Item
{
	static final StringField mandatory = new StringField().toFinal();
	static final StringField optional = new StringField().toFinal().optional();
	static final StringField optionalOk = new StringField().toFinal().optional();


	/**
	 * Creates a new OptionalItem with all the fields initially needed.
	 * @param mandatory the initial value for field {@link #mandatory}.
	 * @param optional the initial value for field {@link #optional}.
	 * @param optionalOk the initial value for field {@link #optionalOk}.
	 * @throws com.exedio.cope.MandatoryViolationException if mandatory is null.
	 * @throws com.exedio.cope.StringLengthViolationException if mandatory, optional, optionalOk violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression","ZeroLengthArrayAllocation"})
	OptionalItem(
				final java.lang.String mandatory,
				final java.lang.String optional,
				final java.lang.String optionalOk)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			OptionalItem.mandatory.map(mandatory),
			OptionalItem.optional.map(optional),
			OptionalItem.optionalOk.map(optionalOk),
		});
	}

	/**
	 * Creates a new OptionalItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private OptionalItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final java.lang.String getMandatory()
	{
		return OptionalItem.mandatory.get(this);
	}

	/**
	 * Returns the value of {@link #optional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final java.lang.String getOptional()
	{
		return OptionalItem.optional.get(this);
	}

	/**
	 * Returns the value of {@link #optionalOk}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final java.lang.String getOptionalOk()
	{
		return OptionalItem.optionalOk.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for optionalItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<OptionalItem> TYPE = com.exedio.cope.TypesBound.newType(OptionalItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private OptionalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
