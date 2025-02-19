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
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.util.CharSet;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // OK: for example TYPE
final class StringLengthItem extends Item
{
	static final StringField empty = new StringField().toFinal().lengthMin(0);
	static final StringField emptyOk = new StringField().toFinal().lengthMin(0);
	static final StringField normal = new StringField().toFinal().lengthMin(1);
	static final StringField normalOk = new StringField().toFinal().lengthMin(1);
	static final StringField min10 = new StringField().toFinal().lengthMin(10);
	static final StringField min10Ok = new StringField().toFinal().lengthMin(10);

	/**
	 * Provokes an "TypeError: responseXML is null" when running tests in
	 * {@code CharacterNulCop} with copeutil before revision 863.
	 */
	@WrapperIgnore
	@UsageEntryPoint
	static final StringField charSet = new StringField().optional().charSet(CharSet.EMAIL_INTERNATIONAL);


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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression"})
	StringLengthItem(
				@javax.annotation.Nonnull final java.lang.String empty,
				@javax.annotation.Nonnull final java.lang.String emptyOk,
				@javax.annotation.Nonnull final java.lang.String normal,
				@javax.annotation.Nonnull final java.lang.String normalOk,
				@javax.annotation.Nonnull final java.lang.String min10,
				@javax.annotation.Nonnull final java.lang.String min10Ok)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(StringLengthItem.empty,empty),
			com.exedio.cope.SetValue.map(StringLengthItem.emptyOk,emptyOk),
			com.exedio.cope.SetValue.map(StringLengthItem.normal,normal),
			com.exedio.cope.SetValue.map(StringLengthItem.normalOk,normalOk),
			com.exedio.cope.SetValue.map(StringLengthItem.min10,min10),
			com.exedio.cope.SetValue.map(StringLengthItem.min10Ok,min10Ok),
		});
	}

	/**
	 * Creates a new StringLengthItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private StringLengthItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #empty}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getEmpty()
	{
		return StringLengthItem.empty.get(this);
	}

	/**
	 * Returns the value of {@link #emptyOk}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getEmptyOk()
	{
		return StringLengthItem.emptyOk.get(this);
	}

	/**
	 * Returns the value of {@link #normal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getNormal()
	{
		return StringLengthItem.normal.get(this);
	}

	/**
	 * Returns the value of {@link #normalOk}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getNormalOk()
	{
		return StringLengthItem.normalOk.get(this);
	}

	/**
	 * Returns the value of {@link #min10}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getMin10()
	{
		return StringLengthItem.min10.get(this);
	}

	/**
	 * Returns the value of {@link #min10Ok}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getMin10Ok()
	{
		return StringLengthItem.min10Ok.get(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for stringLengthItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<StringLengthItem> TYPE = com.exedio.cope.TypesBound.newType(StringLengthItem.class,StringLengthItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private StringLengthItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
