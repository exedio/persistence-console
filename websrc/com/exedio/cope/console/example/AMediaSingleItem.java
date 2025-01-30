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
import com.exedio.cope.pattern.Media;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // OK: for example TYPE
final class AMediaSingleItem extends Item
{
	static final Media content = new Media().optional();

	/**
	 * Creates a new AMediaSingleItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression"})
	AMediaSingleItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new AMediaSingleItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AMediaSingleItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns a URL the content of {@link #content} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getContentURL()
	{
		return AMediaSingleItem.content.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #content} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getContentLocator()
	{
		return AMediaSingleItem.content.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getContentContentType()
	{
		return AMediaSingleItem.content.getContentType(this);
	}

	/**
	 * Returns whether media {@link #content} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isContentNull()
	{
		return AMediaSingleItem.content.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getContentLastModified()
	{
		return AMediaSingleItem.content.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getContentLength()
	{
		return AMediaSingleItem.content.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getContentBody()
	{
		return AMediaSingleItem.content.getBody(this);
	}

	/**
	 * Writes the body of media {@link #content} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getContentBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaSingleItem.content.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #content} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getContentBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AMediaSingleItem.content.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #content}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setContent(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value content)
			throws
				java.io.IOException
	{
		AMediaSingleItem.content.set(this,content);
	}

	/**
	 * Sets the content of media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setContent(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AMediaSingleItem.content.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #content}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setContent(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaSingleItem.content.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #content}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setContent(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaSingleItem.content.set(this,body,contentType);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for aMediaSingleItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AMediaSingleItem> TYPE = com.exedio.cope.TypesBound.newType(AMediaSingleItem.class,AMediaSingleItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AMediaSingleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
