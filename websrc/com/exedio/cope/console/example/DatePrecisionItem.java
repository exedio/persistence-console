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

import static com.exedio.cope.instrument.Visibility.PRIVATE;

import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperType;
import java.time.Instant;
import java.util.Date;

@WrapperType(constructor=PRIVATE)
final class DatePrecisionItem extends Item
{
	static final DateField millis  = new DateField().toFinal().optional();
	static final DateField seconds = new DateField().toFinal().optional();
	static final DateField minutes = new DateField().toFinal().optional();
	static final DateField hours   = new DateField().toFinal().optional();

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	static void createSampleData()
	{
		new DatePrecisionItem(
				Date.from(Instant.parse("2007-12-03T10:15:30.123Z")),
				Date.from(Instant.parse("2007-12-03T10:15:30.00Z")),
				Date.from(Instant.parse("2007-12-03T10:15:00.00Z")),
				Date.from(Instant.parse("2007-12-03T10:00:00.00Z")));
		new DatePrecisionItem(null, null, null, null);
	}

	/**
	 * Creates a new DatePrecisionItem with all the fields initially needed.
	 * @param millis the initial value for field {@link #millis}.
	 * @param seconds the initial value for field {@link #seconds}.
	 * @param minutes the initial value for field {@link #minutes}.
	 * @param hours the initial value for field {@link #hours}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression"})
	private DatePrecisionItem(
				@javax.annotation.Nullable final java.util.Date millis,
				@javax.annotation.Nullable final java.util.Date seconds,
				@javax.annotation.Nullable final java.util.Date minutes,
				@javax.annotation.Nullable final java.util.Date hours)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(DatePrecisionItem.millis,millis),
			com.exedio.cope.SetValue.map(DatePrecisionItem.seconds,seconds),
			com.exedio.cope.SetValue.map(DatePrecisionItem.minutes,minutes),
			com.exedio.cope.SetValue.map(DatePrecisionItem.hours,hours),
		});
	}

	/**
	 * Creates a new DatePrecisionItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DatePrecisionItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #millis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getMillis()
	{
		return DatePrecisionItem.millis.get(this);
	}

	/**
	 * Returns the value of {@link #seconds}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getSeconds()
	{
		return DatePrecisionItem.seconds.get(this);
	}

	/**
	 * Returns the value of {@link #minutes}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getMinutes()
	{
		return DatePrecisionItem.minutes.get(this);
	}

	/**
	 * Returns the value of {@link #hours}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getHours()
	{
		return DatePrecisionItem.hours.get(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1L;

	/**
	 * The persistent type information for datePrecisionItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DatePrecisionItem> TYPE = com.exedio.cope.TypesBound.newType(DatePrecisionItem.class,DatePrecisionItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DatePrecisionItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
