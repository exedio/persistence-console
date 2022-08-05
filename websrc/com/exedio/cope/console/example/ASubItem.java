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

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.IntegerField;
import com.exedio.cope.StringField;

@SuppressWarnings("unused")
final class ASubItem extends AnItem
{
	static final StringField aSubField = new StringField();

	static final IntegerField subInteger = new IntegerField().defaultTo(2);
	static final CheckConstraint supportedCheckConstraint = new CheckConstraint(subInteger.lessOrEqual(subInteger));
	static final CheckConstraint unsupportedCheckConstraint = new CheckConstraint(subInteger.less(superInteger));

	/**
	 * Creates a new ASubItem with all the fields initially needed.
	 * @param aField the initial value for field {@link #aField}.
	 * @param letter the initial value for field {@link #letter}.
	 * @param secondLetter the initial value for field {@link #secondLetter}.
	 * @param color the initial value for field {@link #color}.
	 * @param aSubField the initial value for field {@link #aSubField}.
	 * @throws com.exedio.cope.MandatoryViolationException if aField, letter, secondLetter, color, aSubField is null.
	 * @throws com.exedio.cope.StringLengthViolationException if aField, aSubField violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression"})
	ASubItem(
				final java.lang.String aField,
				final com.exedio.cope.console.example.AnItem.Letter letter,
				final com.exedio.cope.console.example.AnItem.Letter secondLetter,
				final com.exedio.cope.console.example.AnItem.Color color,
				final java.lang.String aSubField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.console.example.AnItem.aField.map(aField),
			com.exedio.cope.console.example.AnItem.letter.map(letter),
			com.exedio.cope.console.example.AnItem.secondLetter.map(secondLetter),
			com.exedio.cope.console.example.AnItem.color.map(color),
			ASubItem.aSubField.map(aSubField),
		});
	}

	/**
	 * Creates a new ASubItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private ASubItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #aSubField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final java.lang.String getASubField()
	{
		return ASubItem.aSubField.get(this);
	}

	/**
	 * Sets a new value for {@link #aSubField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setASubField(final java.lang.String aSubField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		ASubItem.aSubField.set(this,aSubField);
	}

	/**
	 * Returns the value of {@link #subInteger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final int getSubInteger()
	{
		return ASubItem.subInteger.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #subInteger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setSubInteger(final int subInteger)
	{
		ASubItem.subInteger.set(this,subInteger);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for aSubItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ASubItem> TYPE = com.exedio.cope.TypesBound.newType(ASubItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private ASubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
