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

import com.exedio.cope.CopeSchemaName;

@CopeSchemaName("Long2Loooooooooooooooooooooooooooong")
public final class LongName2 extends AnItem
{
	/**
	 * Creates a new LongName2 with all the fields initially needed.
	 * @param aField the initial value for field {@link #aField}.
	 * @param letter the initial value for field {@link #letter}.
	 * @param secondLetter the initial value for field {@link #secondLetter}.
	 * @param color the initial value for field {@link #color}.
	 * @throws com.exedio.cope.MandatoryViolationException if aField, letter, secondLetter, color is null.
	 * @throws com.exedio.cope.StringLengthViolationException if aField violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression","ZeroLengthArrayAllocation"})
	LongName2(
				final java.lang.String aField,
				final com.exedio.cope.console.example.AnItem.Letter letter,
				final com.exedio.cope.console.example.AnItem.Letter secondLetter,
				final com.exedio.cope.console.example.AnItem.Color color)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.console.example.AnItem.aField.map(aField),
			com.exedio.cope.console.example.AnItem.letter.map(letter),
			com.exedio.cope.console.example.AnItem.secondLetter.map(secondLetter),
			com.exedio.cope.console.example.AnItem.color.map(color),
		});
	}

	/**
	 * Creates a new LongName2 and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private LongName2(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for longName2.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<LongName2> TYPE = com.exedio.cope.TypesBound.newType(LongName2.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private LongName2(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
