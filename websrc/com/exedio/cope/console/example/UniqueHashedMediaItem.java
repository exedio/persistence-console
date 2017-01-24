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
import com.exedio.cope.pattern.UniqueHashedMedia;

public final class UniqueHashedMediaItem extends Item
{
	static final UniqueHashedMedia feature = new UniqueHashedMedia(new Media());

	/**
	 * Creates a new UniqueHashedMediaItem with all the fields initially needed.
	 * @param feature the initial value for field {@link #feature}.
	 * @throws com.exedio.cope.MandatoryViolationException if feature is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	UniqueHashedMediaItem(
				final com.exedio.cope.pattern.Media.Value feature)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			UniqueHashedMediaItem.feature.map(feature),
		});
	}

	/**
	 * Creates a new UniqueHashedMediaItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private UniqueHashedMediaItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns a URL the content of {@link #feature} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getURL()
	{
		return UniqueHashedMediaItem.feature.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #feature} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getLocator()
	{
		return UniqueHashedMediaItem.feature.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #feature}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	final java.lang.String getContentType()
	{
		return UniqueHashedMediaItem.feature.getContentType(this);
	}

	/**
	 * Returns the last modification date of media {@link #feature}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	final java.util.Date getLastModified()
	{
		return UniqueHashedMediaItem.feature.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #feature}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getLength()
	{
		return UniqueHashedMediaItem.feature.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #feature}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final byte[] getBody()
	{
		return UniqueHashedMediaItem.feature.getBody(this);
	}

	/**
	 * Returns the hash of the media body {@link #feature}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getHash")
	final java.lang.String getHash()
	{
		return UniqueHashedMediaItem.feature.getHash(this);
	}

	/**
	 * Finds a uniqueHashedMediaItem by it's hash.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forHash")
	static final UniqueHashedMediaItem forHash(final java.lang.String featureHash)
	{
		return UniqueHashedMediaItem.feature.forHash(UniqueHashedMediaItem.class,featureHash);
	}

	/**
	 * Returns a uniqueHashedMediaItem containing given media value or creates a new one.
	 * @param feature shall be equal to field {@link #feature}.
	 * @throws java.io.IOException if reading <tt>value</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getOrCreate")
	static final UniqueHashedMediaItem getOrCreate(final com.exedio.cope.pattern.Media.Value feature)
			throws
				java.io.IOException
	{
		return UniqueHashedMediaItem.feature.getOrCreate(UniqueHashedMediaItem.class,feature);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for uniqueHashedMediaItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<UniqueHashedMediaItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueHashedMediaItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private UniqueHashedMediaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
