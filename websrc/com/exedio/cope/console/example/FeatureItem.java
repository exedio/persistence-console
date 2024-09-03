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

import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.reflect.FeatureField;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // OK: for example TYPE
final class FeatureItem extends Item
{
	@WrapperInitial
	static final FeatureField<Feature> feature = FeatureField.create().optional();
	@WrapperInitial
	static final FeatureField<StringField> string = FeatureField.create(StringField.class).optional();

	static final StringField stringField1 = new StringField().optional();
	static final StringField stringField2 = new StringField().optional();
	static final IntegerField intField1 = new IntegerField().optional();
	static final IntegerField intField2 = new IntegerField().optional();

	FeatureItem(
			final String feature,
			final String string)
		throws
			com.exedio.cope.StringLengthViolationException
	{
		this(
				SetValue.map(FeatureItem.feature.getIdField(), feature),
				SetValue.map(FeatureItem.string.getIdField(), string));
	}

	/**
	 * Creates a new FeatureItem with all the fields initially needed.
	 * @param feature the initial value for field {@link #feature}.
	 * @param string the initial value for field {@link #string}.
	 * @throws com.exedio.cope.StringLengthViolationException if feature, string violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression"})
	FeatureItem(
				final Feature feature,
				final StringField string)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(FeatureItem.feature,feature),
			com.exedio.cope.SetValue.map(FeatureItem.string,string),
		});
	}

	/**
	 * Creates a new FeatureItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private FeatureItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #feature}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final Feature getFeature()
	{
		return FeatureItem.feature.get(this);
	}

	/**
	 * Sets a new value for {@link #feature}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setFeature(final Feature feature)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		FeatureItem.feature.set(this,feature);
	}

	/**
	 * Returns the value of {@link #string}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final StringField getString()
	{
		return FeatureItem.string.get(this);
	}

	/**
	 * Sets a new value for {@link #string}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setString(final StringField string)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		FeatureItem.string.set(this,string);
	}

	/**
	 * Returns the value of {@link #stringField1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final java.lang.String getStringField1()
	{
		return FeatureItem.stringField1.get(this);
	}

	/**
	 * Sets a new value for {@link #stringField1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setStringField1(final java.lang.String stringField1)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		FeatureItem.stringField1.set(this,stringField1);
	}

	/**
	 * Returns the value of {@link #stringField2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final java.lang.String getStringField2()
	{
		return FeatureItem.stringField2.get(this);
	}

	/**
	 * Sets a new value for {@link #stringField2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setStringField2(final java.lang.String stringField2)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		FeatureItem.stringField2.set(this,stringField2);
	}

	/**
	 * Returns the value of {@link #intField1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final java.lang.Integer getIntField1()
	{
		return FeatureItem.intField1.get(this);
	}

	/**
	 * Sets a new value for {@link #intField1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setIntField1(final java.lang.Integer intField1)
	{
		FeatureItem.intField1.set(this,intField1);
	}

	/**
	 * Returns the value of {@link #intField2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final java.lang.Integer getIntField2()
	{
		return FeatureItem.intField2.get(this);
	}

	/**
	 * Sets a new value for {@link #intField2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setIntField2(final java.lang.Integer intField2)
	{
		FeatureItem.intField2.set(this,intField2);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for featureItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<FeatureItem> TYPE = com.exedio.cope.TypesBound.newType(FeatureItem.class,FeatureItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private FeatureItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
