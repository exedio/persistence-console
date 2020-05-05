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
import com.exedio.cope.DateField;
import com.exedio.cope.DayField;
import com.exedio.cope.EnumField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.pattern.Dispatcher;
import com.exedio.cope.util.Day;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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


	// for default to now
	static final DateField defaultNoneDate = new DateField().optional();
	static final DayField  defaultNoneDay  = new DayField ().optional();
	static final DateField defaultGoodDate = new DateField().defaultTo(new Date(19028589127l));
	static final DayField  defaultGoodDay  = new DayField ().defaultTo(new Day(2005, 8, 23));
	static final DateField defaultBadDate  = new DateField().defaultTo(new Date());
	static final DayField  defaultBadDay   = new DayField ().defaultTo(new Day(TimeZone.getDefault()));


	// for serialization check
	@SuppressWarnings("TransientFieldNotInitialized")
	transient int transientField = 0;
	int nonTransientField = 0;
	Map<String, List<Integer>> nonTransientField2 = null;
	Item serializable = null;

	static final Dispatcher dispatcher = Dispatcher.create(AnItem::dispatch);

	@WrapInterim(methodBody=false)
	@SuppressWarnings("EmptyMethod") // OK: just a dummy
	@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD") // OK: just a dummy
	private void dispatch() {}


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

	/**
	 * Returns the value of {@link #defaultNoneDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.util.Date getDefaultNoneDate()
	{
		return AnItem.defaultNoneDate.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultNoneDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDefaultNoneDate(final java.util.Date defaultNoneDate)
	{
		AnItem.defaultNoneDate.set(this,defaultNoneDate);
	}

	/**
	 * Sets the current date for the date field {@link #defaultNoneDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDefaultNoneDate()
	{
		AnItem.defaultNoneDate.touch(this);
	}

	/**
	 * Returns the value of {@link #defaultNoneDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final com.exedio.cope.util.Day getDefaultNoneDay()
	{
		return AnItem.defaultNoneDay.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultNoneDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDefaultNoneDay(final com.exedio.cope.util.Day defaultNoneDay)
	{
		AnItem.defaultNoneDay.set(this,defaultNoneDay);
	}

	/**
	 * Sets today for the date field {@link #defaultNoneDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDefaultNoneDay(final java.util.TimeZone zone)
	{
		AnItem.defaultNoneDay.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #defaultGoodDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.util.Date getDefaultGoodDate()
	{
		return AnItem.defaultGoodDate.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultGoodDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDefaultGoodDate(final java.util.Date defaultGoodDate)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AnItem.defaultGoodDate.set(this,defaultGoodDate);
	}

	/**
	 * Sets the current date for the date field {@link #defaultGoodDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDefaultGoodDate()
	{
		AnItem.defaultGoodDate.touch(this);
	}

	/**
	 * Returns the value of {@link #defaultGoodDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final com.exedio.cope.util.Day getDefaultGoodDay()
	{
		return AnItem.defaultGoodDay.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultGoodDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDefaultGoodDay(final com.exedio.cope.util.Day defaultGoodDay)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AnItem.defaultGoodDay.set(this,defaultGoodDay);
	}

	/**
	 * Sets today for the date field {@link #defaultGoodDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDefaultGoodDay(final java.util.TimeZone zone)
	{
		AnItem.defaultGoodDay.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #defaultBadDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.util.Date getDefaultBadDate()
	{
		return AnItem.defaultBadDate.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultBadDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDefaultBadDate(final java.util.Date defaultBadDate)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AnItem.defaultBadDate.set(this,defaultBadDate);
	}

	/**
	 * Sets the current date for the date field {@link #defaultBadDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDefaultBadDate()
	{
		AnItem.defaultBadDate.touch(this);
	}

	/**
	 * Returns the value of {@link #defaultBadDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final com.exedio.cope.util.Day getDefaultBadDay()
	{
		return AnItem.defaultBadDay.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultBadDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDefaultBadDay(final com.exedio.cope.util.Day defaultBadDay)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AnItem.defaultBadDay.set(this,defaultBadDay);
	}

	/**
	 * Sets today for the date field {@link #defaultBadDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDefaultBadDay(final java.util.TimeZone zone)
	{
		AnItem.defaultBadDay.touch(this,zone);
	}

	/**
	 * Dispatch by {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="dispatch")
	static final void dispatchDispatcher(final com.exedio.cope.pattern.Dispatcher.Config config,final com.exedio.cope.util.JobContext ctx)
	{
		AnItem.dispatcher.dispatch(AnItem.class,config,ctx);
	}

	/**
	 * Dispatch by {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="dispatch")
	static final void dispatchDispatcher(final com.exedio.cope.pattern.Dispatcher.Config config,final java.lang.Runnable probe,final com.exedio.cope.util.JobContext ctx)
	{
		AnItem.dispatcher.dispatch(AnItem.class,config,probe,ctx);
	}

	/**
	 * Returns, whether this item is yet to be dispatched by {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isPending")
	final boolean isDispatcherPending()
	{
		return AnItem.dispatcher.isPending(this);
	}

	/**
	 * Sets whether this item is yet to be dispatched by {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setPending")
	final void setDispatcherPending(final boolean pending)
	{
		AnItem.dispatcher.setPending(this,pending);
	}

	/**
	 * Returns, whether this item is allowed to be purged by {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNoPurge")
	final boolean isDispatcherNoPurge()
	{
		return AnItem.dispatcher.isNoPurge(this);
	}

	/**
	 * Sets whether this item is allowed to be purged by {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setNoPurge")
	final void setDispatcherNoPurge(final boolean noPurge)
	{
		AnItem.dispatcher.setNoPurge(this,noPurge);
	}

	/**
	 * Returns the date, this item was last successfully dispatched by {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastSuccessDate")
	final java.util.Date getDispatcherLastSuccessDate()
	{
		return AnItem.dispatcher.getLastSuccessDate(this);
	}

	/**
	 * Returns the milliseconds, this item needed to be last successfully dispatched by {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastSuccessElapsed")
	final java.lang.Long getDispatcherLastSuccessElapsed()
	{
		return AnItem.dispatcher.getLastSuccessElapsed(this);
	}

	/**
	 * Returns the attempts to dispatch this item by {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getRuns")
	final java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getDispatcherRuns()
	{
		return AnItem.dispatcher.getRuns(this);
	}

	/**
	 * Returns the failed attempts to dispatch this item by {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getFailures")
	final java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getDispatcherFailures()
	{
		return AnItem.dispatcher.getFailures(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	static final void purgeDispatcher(final com.exedio.cope.pattern.DispatcherPurgeProperties properties,final com.exedio.cope.util.JobContext ctx)
	{
		AnItem.dispatcher.purge(properties,ctx);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	static final void purgeDispatcher(final com.exedio.cope.pattern.DispatcherPurgeProperties properties,final com.exedio.cope.Condition restriction,final com.exedio.cope.util.JobContext ctx)
	{
		AnItem.dispatcher.purge(properties,restriction,ctx);
	}

	/**
	 * Returns the parent field of the run type of {@link #dispatcher}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="RunParent")
	static final com.exedio.cope.ItemField<AnItem> dispatcherRunParent()
	{
		return AnItem.dispatcher.getRunParent(AnItem.class);
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
