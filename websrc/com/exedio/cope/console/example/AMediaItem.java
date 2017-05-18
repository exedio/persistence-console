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
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaRedirect;
import com.exedio.cope.pattern.MediaThumbnail;
import com.exedio.cope.pattern.PreventUrlGuessing;
import com.exedio.cope.pattern.RedirectFrom;
import com.exedio.cope.pattern.UrlFingerPrinting;

public final class AMediaItem extends Item
{
	@WrapperInitial
	static final StringField name = new StringField().optional();

	@RedirectFrom("contentAlt")
	static final Media content = new Media().optional();

	static final Media large = new Media().optional().lengthMax(50*1000*1000);
	static final Media small = new Media().optional().lengthMax(     50*1000);
	static final Media isFinal = new Media().toFinal().optional();

	static final Media stylesheet = new Media().optional().contentType("text/css");

	@PreventUrlGuessing
	static final Media secret = new Media().optional();

	@UrlFingerPrinting
	static final Media finger = new Media().optional();

	@Deprecated
	static final MediaRedirect redirect = new MediaRedirect(content);

	static final MediaThumbnail thumbnail = new MediaThumbnail(content, 150, 150);

	@Deprecated
	static final MediaRedirect thumbnailRedirect = new MediaRedirect(thumbnail);

	static final AMediaTestable testableOk1  = new AMediaTestable(content, false);
	static final AMediaTestable testableOk2  = new AMediaTestable(content, false);
	static final AMediaTestable testableFail = new AMediaTestable(content, true );

	static final ANameServer nameServer = new ANameServer(name);

	static final ConditionUnsupported conditionUnsupported = new ConditionUnsupported();


	AMediaItem()
	{
		this(null, null);
	}

	/**
	 * Creates a new AMediaItem with all the fields initially needed.
	 * @param name the initial value for field {@link #name}.
	 * @param isFinal the initial value for field {@link #isFinal}.
	 * @throws com.exedio.cope.StringLengthViolationException if name violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AMediaItem(
				final java.lang.String name,
				final com.exedio.cope.pattern.Media.Value isFinal)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AMediaItem.name.map(name),
			AMediaItem.isFinal.map(isFinal),
		});
	}

	/**
	 * Creates a new AMediaItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AMediaItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final java.lang.String getName()
	{
		return AMediaItem.name.get(this);
	}

	/**
	 * Sets a new value for {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setName(final java.lang.String name)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		AMediaItem.name.set(this,name);
	}

	/**
	 * Returns a URL the content of {@link #content} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getContentURL()
	{
		return AMediaItem.content.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #content} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getContentLocator()
	{
		return AMediaItem.content.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	final java.lang.String getContentContentType()
	{
		return AMediaItem.content.getContentType(this);
	}

	/**
	 * Returns whether media {@link #content} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isContentNull()
	{
		return AMediaItem.content.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	final java.util.Date getContentLastModified()
	{
		return AMediaItem.content.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getContentLength()
	{
		return AMediaItem.content.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final byte[] getContentBody()
	{
		return AMediaItem.content.getBody(this);
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
		AMediaItem.content.getBody(this,body);
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
		AMediaItem.content.getBody(this,body);
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
		AMediaItem.content.set(this,content);
	}

	/**
	 * Sets the content of media {@link #content}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setContent(final byte[] body,final java.lang.String contentType)
	{
		AMediaItem.content.set(this,body,contentType);
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
		AMediaItem.content.set(this,body,contentType);
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
		AMediaItem.content.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #large} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getLargeURL()
	{
		return AMediaItem.large.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #large} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getLargeLocator()
	{
		return AMediaItem.large.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #large}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	final java.lang.String getLargeContentType()
	{
		return AMediaItem.large.getContentType(this);
	}

	/**
	 * Returns whether media {@link #large} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isLargeNull()
	{
		return AMediaItem.large.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #large}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	final java.util.Date getLargeLastModified()
	{
		return AMediaItem.large.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #large}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getLargeLength()
	{
		return AMediaItem.large.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #large}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final byte[] getLargeBody()
	{
		return AMediaItem.large.getBody(this);
	}

	/**
	 * Writes the body of media {@link #large} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getLargeBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.large.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #large} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getLargeBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AMediaItem.large.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #large}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setLarge(final com.exedio.cope.pattern.Media.Value large)
			throws
				java.io.IOException
	{
		AMediaItem.large.set(this,large);
	}

	/**
	 * Sets the content of media {@link #large}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setLarge(final byte[] body,final java.lang.String contentType)
	{
		AMediaItem.large.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #large}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setLarge(final java.io.InputStream body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.large.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #large}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setLarge(final java.io.File body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.large.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #small} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getSmallURL()
	{
		return AMediaItem.small.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #small} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getSmallLocator()
	{
		return AMediaItem.small.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #small}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	final java.lang.String getSmallContentType()
	{
		return AMediaItem.small.getContentType(this);
	}

	/**
	 * Returns whether media {@link #small} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isSmallNull()
	{
		return AMediaItem.small.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #small}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	final java.util.Date getSmallLastModified()
	{
		return AMediaItem.small.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #small}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getSmallLength()
	{
		return AMediaItem.small.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #small}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final byte[] getSmallBody()
	{
		return AMediaItem.small.getBody(this);
	}

	/**
	 * Writes the body of media {@link #small} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getSmallBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.small.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #small} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getSmallBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AMediaItem.small.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #small}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setSmall(final com.exedio.cope.pattern.Media.Value small)
			throws
				java.io.IOException
	{
		AMediaItem.small.set(this,small);
	}

	/**
	 * Sets the content of media {@link #small}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setSmall(final byte[] body,final java.lang.String contentType)
	{
		AMediaItem.small.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #small}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setSmall(final java.io.InputStream body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.small.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #small}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setSmall(final java.io.File body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.small.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #isFinal} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getIsFinalURL()
	{
		return AMediaItem.isFinal.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #isFinal} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getIsFinalLocator()
	{
		return AMediaItem.isFinal.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #isFinal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	final java.lang.String getIsFinalContentType()
	{
		return AMediaItem.isFinal.getContentType(this);
	}

	/**
	 * Returns whether media {@link #isFinal} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isIsFinalNull()
	{
		return AMediaItem.isFinal.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #isFinal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	final java.util.Date getIsFinalLastModified()
	{
		return AMediaItem.isFinal.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #isFinal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getIsFinalLength()
	{
		return AMediaItem.isFinal.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #isFinal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final byte[] getIsFinalBody()
	{
		return AMediaItem.isFinal.getBody(this);
	}

	/**
	 * Writes the body of media {@link #isFinal} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getIsFinalBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.isFinal.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #isFinal} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getIsFinalBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AMediaItem.isFinal.getBody(this,body);
	}

	/**
	 * Returns a URL the content of {@link #stylesheet} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getStylesheetURL()
	{
		return AMediaItem.stylesheet.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #stylesheet} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getStylesheetLocator()
	{
		return AMediaItem.stylesheet.getLocator(this);
	}

	/**
	 * Returns whether media {@link #stylesheet} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isStylesheetNull()
	{
		return AMediaItem.stylesheet.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #stylesheet}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	final java.util.Date getStylesheetLastModified()
	{
		return AMediaItem.stylesheet.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #stylesheet}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getStylesheetLength()
	{
		return AMediaItem.stylesheet.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #stylesheet}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final byte[] getStylesheetBody()
	{
		return AMediaItem.stylesheet.getBody(this);
	}

	/**
	 * Writes the body of media {@link #stylesheet} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getStylesheetBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.stylesheet.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #stylesheet} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getStylesheetBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AMediaItem.stylesheet.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #stylesheet}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStylesheet(final com.exedio.cope.pattern.Media.Value stylesheet)
			throws
				java.io.IOException
	{
		AMediaItem.stylesheet.set(this,stylesheet);
	}

	/**
	 * Sets the content of media {@link #stylesheet}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStylesheet(final byte[] body,final java.lang.String contentType)
	{
		AMediaItem.stylesheet.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #stylesheet}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStylesheet(final java.io.InputStream body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.stylesheet.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #stylesheet}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStylesheet(final java.io.File body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.stylesheet.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #secret} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getSecretURL()
	{
		return AMediaItem.secret.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #secret} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getSecretLocator()
	{
		return AMediaItem.secret.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #secret}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	final java.lang.String getSecretContentType()
	{
		return AMediaItem.secret.getContentType(this);
	}

	/**
	 * Returns whether media {@link #secret} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isSecretNull()
	{
		return AMediaItem.secret.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #secret}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	final java.util.Date getSecretLastModified()
	{
		return AMediaItem.secret.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #secret}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getSecretLength()
	{
		return AMediaItem.secret.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #secret}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final byte[] getSecretBody()
	{
		return AMediaItem.secret.getBody(this);
	}

	/**
	 * Writes the body of media {@link #secret} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getSecretBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.secret.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #secret} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getSecretBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AMediaItem.secret.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #secret}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setSecret(final com.exedio.cope.pattern.Media.Value secret)
			throws
				java.io.IOException
	{
		AMediaItem.secret.set(this,secret);
	}

	/**
	 * Sets the content of media {@link #secret}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setSecret(final byte[] body,final java.lang.String contentType)
	{
		AMediaItem.secret.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #secret}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setSecret(final java.io.InputStream body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.secret.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #secret}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setSecret(final java.io.File body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.secret.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #finger} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getFingerURL()
	{
		return AMediaItem.finger.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #finger} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getFingerLocator()
	{
		return AMediaItem.finger.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #finger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	final java.lang.String getFingerContentType()
	{
		return AMediaItem.finger.getContentType(this);
	}

	/**
	 * Returns whether media {@link #finger} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isFingerNull()
	{
		return AMediaItem.finger.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #finger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	final java.util.Date getFingerLastModified()
	{
		return AMediaItem.finger.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #finger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getFingerLength()
	{
		return AMediaItem.finger.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #finger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final byte[] getFingerBody()
	{
		return AMediaItem.finger.getBody(this);
	}

	/**
	 * Writes the body of media {@link #finger} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getFingerBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.finger.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #finger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	final void getFingerBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AMediaItem.finger.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #finger}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setFinger(final com.exedio.cope.pattern.Media.Value finger)
			throws
				java.io.IOException
	{
		AMediaItem.finger.set(this,finger);
	}

	/**
	 * Sets the content of media {@link #finger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setFinger(final byte[] body,final java.lang.String contentType)
	{
		AMediaItem.finger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #finger}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setFinger(final java.io.InputStream body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.finger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #finger}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setFinger(final java.io.File body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.finger.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #redirect} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getRedirectURL()
	{
		return AMediaItem.redirect.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #redirect} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getRedirectLocator()
	{
		return AMediaItem.redirect.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #redirect}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	final java.lang.String getRedirectContentType()
	{
		return AMediaItem.redirect.getContentType(this);
	}

	/**
	 * Returns a URL the content of {@link #thumbnail} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getThumbnailURL()
	{
		return AMediaItem.thumbnail.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnail} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getThumbnailLocator()
	{
		return AMediaItem.thumbnail.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #thumbnail} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	final java.lang.String getThumbnailURLWithFallbackToSource()
	{
		return AMediaItem.thumbnail.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnail} is available under, falling back to source if necessary.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	final com.exedio.cope.pattern.MediaPath.Locator getThumbnailLocatorWithFallbackToSource()
	{
		return AMediaItem.thumbnail.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #thumbnail}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final byte[] getThumbnail()
			throws
				java.io.IOException
	{
		return AMediaItem.thumbnail.get(this);
	}

	/**
	 * Returns a URL the content of {@link #thumbnailRedirect} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getURL")
	final java.lang.String getThumbnailRedirectURL()
	{
		return AMediaItem.thumbnailRedirect.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnailRedirect} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	final com.exedio.cope.pattern.MediaPath.Locator getThumbnailRedirectLocator()
	{
		return AMediaItem.thumbnailRedirect.getLocator(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for aMediaItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<AMediaItem> TYPE = com.exedio.cope.TypesBound.newType(AMediaItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AMediaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
