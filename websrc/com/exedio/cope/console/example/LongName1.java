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
import com.exedio.cope.Cope;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.StringField;

@CopeSchemaName("Long1Loooooooooooooooooooooooooooong")
@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // OK: for example TYPE
final class LongName1 extends AnItem
{
	@CopeSchemaName("looooooooooooooooooooooooooooooooongNameField")
	static final StringField longNameField = new StringField();

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint longConstraint = new CheckConstraint(Cope.and(
			longNameField.notEqual("a"),
			longNameField.notEqual("b"),
			longNameField.notEqual("c"),
			longNameField.notEqual("d"),
			longNameField.notEqual("e"),
			longNameField.notEqual("f"),
			longNameField.notEqual("g"),
			longNameField.notEqual("h"),
			longNameField.notEqual("i"),
			longNameField.notEqual("j"),
			longNameField.notEqual("k"),
			longNameField.notEqual("l"),
			longNameField.notEqual("m"),
			longNameField.notEqual("n"),
			longNameField.notEqual("o"),
			longNameField.notEqual("p"),
			longNameField.notEqual("q"),
			longNameField.notEqual("r"),
			longNameField.notEqual("s"),
			longNameField.notEqual("t"),
			longNameField.notEqual("u"),
			longNameField.notEqual("v"),
			longNameField.notEqual("w"),
			longNameField.notEqual("x"),
			longNameField.notEqual("y"),
			longNameField.notEqual("z")
	));

	/**
	 * Creates a new LongName1 with all the fields initially needed.
	 * @param aField the initial value for field {@link #aField}.
	 * @param letter the initial value for field {@link #letter}.
	 * @param secondLetter the initial value for field {@link #secondLetter}.
	 * @param color the initial value for field {@link #color}.
	 * @param longNameField the initial value for field {@link #longNameField}.
	 * @throws com.exedio.cope.MandatoryViolationException if aField, letter, secondLetter, color, longNameField is null.
	 * @throws com.exedio.cope.StringLengthViolationException if aField, longNameField violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression"})
	LongName1(
				@javax.annotation.Nonnull final java.lang.String aField,
				@javax.annotation.Nonnull final com.exedio.cope.console.example.AnItem.Letter letter,
				@javax.annotation.Nonnull final com.exedio.cope.console.example.AnItem.Letter secondLetter,
				@javax.annotation.Nonnull final com.exedio.cope.console.example.AnItem.Color color,
				@javax.annotation.Nonnull final java.lang.String longNameField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.console.example.AnItem.aField,aField),
			com.exedio.cope.SetValue.map(com.exedio.cope.console.example.AnItem.letter,letter),
			com.exedio.cope.SetValue.map(com.exedio.cope.console.example.AnItem.secondLetter,secondLetter),
			com.exedio.cope.SetValue.map(com.exedio.cope.console.example.AnItem.color,color),
			com.exedio.cope.SetValue.map(LongName1.longNameField,longNameField),
		});
	}

	/**
	 * Creates a new LongName1 and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private LongName1(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #longNameField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getLongNameField()
	{
		return LongName1.longNameField.get(this);
	}

	/**
	 * Sets a new value for {@link #longNameField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setLongNameField(@javax.annotation.Nonnull final java.lang.String longNameField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		LongName1.longNameField.set(this,longNameField);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1L;

	/**
	 * The persistent type information for longName1.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<LongName1> TYPE = com.exedio.cope.TypesBound.newType(LongName1.class,LongName1::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private LongName1(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
