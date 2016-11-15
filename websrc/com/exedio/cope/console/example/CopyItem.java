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
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;

public final class CopyItem extends Item
{
	static final ItemField<CopyOriginItem> origin = ItemField.create(CopyOriginItem.class).toFinal();

	static final StringField string = new StringField().toFinal().copyFrom(origin);

	static final IntegerField integer = new IntegerField().toFinal().copyFrom(origin);


	/**
	 * Creates a new CopyItem with all the fields initially needed.
	 * @param origin the initial value for field {@link #origin}.
	 * @throws com.exedio.cope.MandatoryViolationException if origin is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	CopyItem(
				final CopyOriginItem origin)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CopyItem.origin.map(origin),
		});
	}

	/**
	 * Creates a new CopyItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CopyItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #origin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final CopyOriginItem getOrigin()
	{
		return CopyItem.origin.get(this);
	}

	/**
	 * Returns the value of {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.lang.String getString()
	{
		return CopyItem.string.get(this);
	}

	/**
	 * Returns the value of {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final int getInteger()
	{
		return CopyItem.integer.getMandatory(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copyItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CopyItem> TYPE = com.exedio.cope.TypesBound.newType(CopyItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private CopyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
