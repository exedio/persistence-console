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

public final class AMediaSingleItem extends Item
{
	static final Media content = new Media().optional();

	/**
	 * Creates a new AMediaSingleItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public AMediaSingleItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new AMediaSingleItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AMediaSingleItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns a URL the content of {@link #content} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getContentURL()
	{
		return AMediaSingleItem.content.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #content} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getContentLocator()
	{
		return AMediaSingleItem.content.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	final java.lang.String getContentContentType()
	{
		return AMediaSingleItem.content.getContentType(this);
	}

	/**
	 * Returns whether media {@link #content} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isContentNull()
	{
		return AMediaSingleItem.content.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	final java.util.Date getContentLastModified()
	{
		return AMediaSingleItem.content.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getContentLength()
	{
		return AMediaSingleItem.content.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final byte[] getContentBody()
	{
		return AMediaSingleItem.content.getBody(this);
	}

	/**
	 * Writes the body of media {@link #content} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getContentBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaSingleItem.content.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #content} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getContentBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AMediaSingleItem.content.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #content}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setContent(final com.exedio.cope.pattern.Media.Value content)
			throws
				java.io.IOException
	{
		AMediaSingleItem.content.set(this,content);
	}

	/**
	 * Sets the content of media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setContent(final byte[] body,final java.lang.String contentType)
	{
		AMediaSingleItem.content.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #content}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setContent(final java.io.InputStream body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaSingleItem.content.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #content}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setContent(final java.io.File body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaSingleItem.content.set(this,body,contentType);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for aMediaSingleItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<AMediaSingleItem> TYPE = com.exedio.cope.TypesBound.newType(AMediaSingleItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AMediaSingleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
