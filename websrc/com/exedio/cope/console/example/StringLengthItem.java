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

import com.exedio.cope.Item;
import com.exedio.cope.StringField;

public final class StringLengthItem extends Item
{
	static final StringField empty = new StringField().toFinal().lengthMin(0);
	static final StringField emptyOk = new StringField().toFinal().lengthMin(0);
	static final StringField normal = new StringField().toFinal().lengthMin(1);
	static final StringField normalOk = new StringField().toFinal().lengthMin(1);
	static final StringField min10 = new StringField().toFinal().lengthMin(10);
	static final StringField min10Ok = new StringField().toFinal().lengthMin(10);


	/**
	 * Creates a new StringLengthItem with all the fields initially needed.
	 * @param empty the initial value for field {@link #empty}.
	 * @param emptyOk the initial value for field {@link #emptyOk}.
	 * @param normal the initial value for field {@link #normal}.
	 * @param normalOk the initial value for field {@link #normalOk}.
	 * @param min10 the initial value for field {@link #min10}.
	 * @param min10Ok the initial value for field {@link #min10Ok}.
	 * @throws com.exedio.cope.MandatoryViolationException if empty, emptyOk, normal, normalOk, min10, min10Ok is null.
	 * @throws com.exedio.cope.StringLengthViolationException if empty, emptyOk, normal, normalOk, min10, min10Ok violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	StringLengthItem(
				final java.lang.String empty,
				final java.lang.String emptyOk,
				final java.lang.String normal,
				final java.lang.String normalOk,
				final java.lang.String min10,
				final java.lang.String min10Ok)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			StringLengthItem.empty.map(empty),
			StringLengthItem.emptyOk.map(emptyOk),
			StringLengthItem.normal.map(normal),
			StringLengthItem.normalOk.map(normalOk),
			StringLengthItem.min10.map(min10),
			StringLengthItem.min10Ok.map(min10Ok),
		});
	}

	/**
	 * Creates a new StringLengthItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private StringLengthItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #empty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.lang.String getEmpty()
	{
		return StringLengthItem.empty.get(this);
	}

	/**
	 * Returns the value of {@link #emptyOk}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.lang.String getEmptyOk()
	{
		return StringLengthItem.emptyOk.get(this);
	}

	/**
	 * Returns the value of {@link #normal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.lang.String getNormal()
	{
		return StringLengthItem.normal.get(this);
	}

	/**
	 * Returns the value of {@link #normalOk}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.lang.String getNormalOk()
	{
		return StringLengthItem.normalOk.get(this);
	}

	/**
	 * Returns the value of {@link #min10}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.lang.String getMin10()
	{
		return StringLengthItem.min10.get(this);
	}

	/**
	 * Returns the value of {@link #min10Ok}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.lang.String getMin10Ok()
	{
		return StringLengthItem.min10Ok.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for stringLengthItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<StringLengthItem> TYPE = com.exedio.cope.TypesBound.newType(StringLengthItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private StringLengthItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
