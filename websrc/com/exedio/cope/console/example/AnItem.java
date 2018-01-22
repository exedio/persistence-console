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

import com.exedio.cope.CopeSchemaValue;
import com.exedio.cope.EnumField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class AnItem extends Item
{
	static final StringField aField = new StringField();

	enum Letter
	{
		A, B, C, D
	}

	static final EnumField<Letter> letter = EnumField.create(Letter.class);
	static final EnumField<Letter> secondLetter = EnumField.create(Letter.class);

	enum Color
	{
		red, green, yellow, @CopeSchemaValue(35) blue35, pink
	}

	static final EnumField<Color> color = EnumField.create(Color.class);

	static final IntegerField superInteger = new IntegerField().defaultTo(3);

	// for serialization check
	@SuppressWarnings("TransientFieldNotInitialized")
	transient int transientField = 0;
	int nonTransientField = 0;
	Map<String, List<Integer>> nonTransientField2 = null;
	Item serializable = null;


	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param aField the initial value for field {@link #aField}.
	 * @param letter the initial value for field {@link #letter}.
	 * @param secondLetter the initial value for field {@link #secondLetter}.
	 * @param color the initial value for field {@link #color}.
	 * @throws com.exedio.cope.MandatoryViolationException if aField, letter, secondLetter, color is null.
	 * @throws com.exedio.cope.StringLengthViolationException if aField violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AnItem(
				final java.lang.String aField,
				final Letter letter,
				final Letter secondLetter,
				final Color color)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.aField.map(aField),
			AnItem.letter.map(letter),
			AnItem.secondLetter.map(secondLetter),
			AnItem.color.map(color),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #aField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.lang.String getAField()
	{
		return AnItem.aField.get(this);
	}

	/**
	 * Sets a new value for {@link #aField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setAField(final java.lang.String aField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		AnItem.aField.set(this,aField);
	}

	/**
	 * Returns the value of {@link #letter}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final Letter getLetter()
	{
		return AnItem.letter.get(this);
	}

	/**
	 * Sets a new value for {@link #letter}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setLetter(final Letter letter)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AnItem.letter.set(this,letter);
	}

	/**
	 * Returns the value of {@link #secondLetter}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final Letter getSecondLetter()
	{
		return AnItem.secondLetter.get(this);
	}

	/**
	 * Sets a new value for {@link #secondLetter}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setSecondLetter(final Letter secondLetter)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AnItem.secondLetter.set(this,secondLetter);
	}

	/**
	 * Returns the value of {@link #color}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final Color getColor()
	{
		return AnItem.color.get(this);
	}

	/**
	 * Sets a new value for {@link #color}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setColor(final Color color)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AnItem.color.set(this,color);
	}

	/**
	 * Returns the value of {@link #superInteger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final int getSuperInteger()
	{
		return AnItem.superInteger.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #superInteger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setSuperInteger(final int superInteger)
	{
		AnItem.superInteger.set(this,superInteger);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
