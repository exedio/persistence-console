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

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;

public final class CopyOriginItem extends Item
{
	static final StringField string = new StringField().toFinal();
	static final IntegerField integer = new IntegerField().toFinal();

	/**
	 * Creates a new CopyOriginItem with all the fields initially needed.
	 * @param string the initial value for field {@link #string}.
	 * @param integer the initial value for field {@link #integer}.
	 * @throws com.exedio.cope.MandatoryViolationException if string is null.
	 * @throws com.exedio.cope.StringLengthViolationException if string violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	CopyOriginItem(
				final java.lang.String string,
				final int integer)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CopyOriginItem.string.map(string),
			CopyOriginItem.integer.map(integer),
		});
	}

	/**
	 * Creates a new CopyOriginItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CopyOriginItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.lang.String getString()
	{
		return CopyOriginItem.string.get(this);
	}

	/**
	 * Returns the value of {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final int getInteger()
	{
		return CopyOriginItem.integer.getMandatory(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copyOriginItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CopyOriginItem> TYPE = com.exedio.cope.TypesBound.newType(CopyOriginItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private CopyOriginItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
