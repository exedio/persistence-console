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
import com.exedio.cope.Vault;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaThumbnail;
import com.exedio.cope.pattern.PreventUrlGuessing;
import com.exedio.cope.pattern.RedirectFrom;
import com.exedio.cope.pattern.UrlFingerPrinting;

@SuppressWarnings({"unused","StaticMethodOnlyUsedInOneClass"}) // OK: for example TYPE
final class AMediaItem extends Item
{
	@WrapperInitial
	static final StringField name = new StringField().optional();

	@RedirectFrom("contentAlt")
	static final Media content = new Media().optional();

	static final Media large = new Media().optional().lengthMax(50*1000*1000);
	static final Media small = new Media().optional().lengthMax(     50*1000);
	static final Media isFinal = new Media().toFinal().optional();

	static final Media stylesheet = new Media().optional().contentType("text/css");

	@Vault
	static final Media vault = new Media().optional().lengthMax(33*1000);

	@PreventUrlGuessing
	static final Media secret = new Media().optional();

	@UrlFingerPrinting
	@Vault("other")
	static final Media finger = new Media().optional();

	static final Media noUrl = new Media().withLocator(false).optional();

	static final MediaThumbnail thumbnail = new MediaThumbnail(content, 150, 150);

	static final MediaThumbnail thumbnailOfNoUrl = new MediaThumbnail(noUrl, 150, 150);

	static final MediaThumbnail noUrlThumbnail = new MediaThumbnail(content, 150, 150).withLocator(false);

	static final MediaThumbnail noUrlThumbnailOfNoUrl = new MediaThumbnail(noUrl, 150, 150).withLocator(false);

	@WrapperIgnore static final AMediaTestable testableOk1  = new AMediaTestable(content, false);
	@WrapperIgnore static final AMediaTestable testableOk2  = new AMediaTestable(content, false);
	@WrapperIgnore static final AMediaTestable testableFail = new AMediaTestable(content, true );

	@WrapperIgnore
	static final ANameServer nameServer = new ANameServer(name);

	@WrapperIgnore
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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression"})
	AMediaItem(
				@javax.annotation.Nullable final java.lang.String name,
				@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value isFinal)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(AMediaItem.name,name),
			com.exedio.cope.SetValue.map(AMediaItem.isFinal,isFinal),
		});
	}

	/**
	 * Creates a new AMediaItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AMediaItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getName()
	{
		return AMediaItem.name.get(this);
	}

	/**
	 * Sets a new value for {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setName(@javax.annotation.Nullable final java.lang.String name)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		AMediaItem.name.set(this,name);
	}

	/**
	 * Returns a URL the content of {@link #content} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getContentURL()
	{
		return AMediaItem.content.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #content} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getContentLocator()
	{
		return AMediaItem.content.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getContentContentType()
	{
		return AMediaItem.content.getContentType(this);
	}

	/**
	 * Returns whether media {@link #content} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isContentNull()
	{
		return AMediaItem.content.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getContentLastModified()
	{
		return AMediaItem.content.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getContentLength()
	{
		return AMediaItem.content.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getContentBody()
	{
		return AMediaItem.content.getBody(this);
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
		AMediaItem.content.getBody(this,body);
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
		AMediaItem.content.getBody(this,body);
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
		AMediaItem.content.set(this,content);
	}

	/**
	 * Sets the content of media {@link #content}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setContent(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AMediaItem.content.set(this,body,contentType);
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
		AMediaItem.content.set(this,body,contentType);
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
		AMediaItem.content.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #large} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getLargeURL()
	{
		return AMediaItem.large.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #large} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getLargeLocator()
	{
		return AMediaItem.large.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #large}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getLargeContentType()
	{
		return AMediaItem.large.getContentType(this);
	}

	/**
	 * Returns whether media {@link #large} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isLargeNull()
	{
		return AMediaItem.large.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #large}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getLargeLastModified()
	{
		return AMediaItem.large.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #large}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getLargeLength()
	{
		return AMediaItem.large.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #large}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getLargeBody()
	{
		return AMediaItem.large.getBody(this);
	}

	/**
	 * Writes the body of media {@link #large} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getLargeBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.large.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #large} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getLargeBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AMediaItem.large.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #large}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setLarge(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value large)
			throws
				java.io.IOException
	{
		AMediaItem.large.set(this,large);
	}

	/**
	 * Sets the content of media {@link #large}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setLarge(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AMediaItem.large.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #large}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setLarge(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.large.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #large}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setLarge(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.large.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #small} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getSmallURL()
	{
		return AMediaItem.small.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #small} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getSmallLocator()
	{
		return AMediaItem.small.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #small}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getSmallContentType()
	{
		return AMediaItem.small.getContentType(this);
	}

	/**
	 * Returns whether media {@link #small} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isSmallNull()
	{
		return AMediaItem.small.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #small}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getSmallLastModified()
	{
		return AMediaItem.small.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #small}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getSmallLength()
	{
		return AMediaItem.small.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #small}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getSmallBody()
	{
		return AMediaItem.small.getBody(this);
	}

	/**
	 * Writes the body of media {@link #small} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getSmallBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.small.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #small} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getSmallBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AMediaItem.small.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #small}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setSmall(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value small)
			throws
				java.io.IOException
	{
		AMediaItem.small.set(this,small);
	}

	/**
	 * Sets the content of media {@link #small}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setSmall(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AMediaItem.small.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #small}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setSmall(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.small.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #small}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setSmall(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.small.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #isFinal} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getIsFinalURL()
	{
		return AMediaItem.isFinal.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #isFinal} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getIsFinalLocator()
	{
		return AMediaItem.isFinal.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #isFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getIsFinalContentType()
	{
		return AMediaItem.isFinal.getContentType(this);
	}

	/**
	 * Returns whether media {@link #isFinal} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isIsFinalNull()
	{
		return AMediaItem.isFinal.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #isFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getIsFinalLastModified()
	{
		return AMediaItem.isFinal.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #isFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getIsFinalLength()
	{
		return AMediaItem.isFinal.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #isFinal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getIsFinalBody()
	{
		return AMediaItem.isFinal.getBody(this);
	}

	/**
	 * Writes the body of media {@link #isFinal} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getIsFinalBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.isFinal.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #isFinal} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getIsFinalBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AMediaItem.isFinal.getBody(this,body);
	}

	/**
	 * Returns a URL the content of {@link #stylesheet} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getStylesheetURL()
	{
		return AMediaItem.stylesheet.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #stylesheet} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getStylesheetLocator()
	{
		return AMediaItem.stylesheet.getLocator(this);
	}

	/**
	 * Returns whether media {@link #stylesheet} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isStylesheetNull()
	{
		return AMediaItem.stylesheet.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #stylesheet}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getStylesheetLastModified()
	{
		return AMediaItem.stylesheet.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #stylesheet}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getStylesheetLength()
	{
		return AMediaItem.stylesheet.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #stylesheet}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getStylesheetBody()
	{
		return AMediaItem.stylesheet.getBody(this);
	}

	/**
	 * Writes the body of media {@link #stylesheet} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getStylesheetBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.stylesheet.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #stylesheet} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getStylesheetBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AMediaItem.stylesheet.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #stylesheet}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setStylesheet(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value stylesheet)
			throws
				java.io.IOException
	{
		AMediaItem.stylesheet.set(this,stylesheet);
	}

	/**
	 * Sets the content of media {@link #stylesheet}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setStylesheet(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AMediaItem.stylesheet.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #stylesheet}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setStylesheet(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.stylesheet.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #stylesheet}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setStylesheet(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.stylesheet.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #vault} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getVaultURL()
	{
		return AMediaItem.vault.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #vault} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getVaultLocator()
	{
		return AMediaItem.vault.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #vault}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getVaultContentType()
	{
		return AMediaItem.vault.getContentType(this);
	}

	/**
	 * Returns whether media {@link #vault} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isVaultNull()
	{
		return AMediaItem.vault.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #vault}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getVaultLastModified()
	{
		return AMediaItem.vault.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #vault}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getVaultLength()
	{
		return AMediaItem.vault.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #vault}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getVaultBody()
	{
		return AMediaItem.vault.getBody(this);
	}

	/**
	 * Writes the body of media {@link #vault} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getVaultBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.vault.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #vault} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getVaultBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AMediaItem.vault.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #vault}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value vault)
			throws
				java.io.IOException
	{
		AMediaItem.vault.set(this,vault);
	}

	/**
	 * Sets the content of media {@link #vault}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AMediaItem.vault.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #vault}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.vault.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #vault}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.vault.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #secret} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getSecretURL()
	{
		return AMediaItem.secret.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #secret} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getSecretLocator()
	{
		return AMediaItem.secret.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #secret}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getSecretContentType()
	{
		return AMediaItem.secret.getContentType(this);
	}

	/**
	 * Returns whether media {@link #secret} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isSecretNull()
	{
		return AMediaItem.secret.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #secret}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getSecretLastModified()
	{
		return AMediaItem.secret.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #secret}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getSecretLength()
	{
		return AMediaItem.secret.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #secret}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getSecretBody()
	{
		return AMediaItem.secret.getBody(this);
	}

	/**
	 * Writes the body of media {@link #secret} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getSecretBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.secret.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #secret} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getSecretBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AMediaItem.secret.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #secret}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setSecret(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value secret)
			throws
				java.io.IOException
	{
		AMediaItem.secret.set(this,secret);
	}

	/**
	 * Sets the content of media {@link #secret}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setSecret(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AMediaItem.secret.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #secret}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setSecret(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.secret.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #secret}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setSecret(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.secret.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #finger} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getFingerURL()
	{
		return AMediaItem.finger.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #finger} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getFingerLocator()
	{
		return AMediaItem.finger.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #finger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getFingerContentType()
	{
		return AMediaItem.finger.getContentType(this);
	}

	/**
	 * Returns whether media {@link #finger} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isFingerNull()
	{
		return AMediaItem.finger.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #finger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getFingerLastModified()
	{
		return AMediaItem.finger.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #finger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getFingerLength()
	{
		return AMediaItem.finger.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #finger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getFingerBody()
	{
		return AMediaItem.finger.getBody(this);
	}

	/**
	 * Writes the body of media {@link #finger} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getFingerBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.finger.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #finger} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getFingerBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AMediaItem.finger.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #finger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setFinger(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value finger)
			throws
				java.io.IOException
	{
		AMediaItem.finger.set(this,finger);
	}

	/**
	 * Sets the content of media {@link #finger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setFinger(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AMediaItem.finger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #finger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setFinger(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.finger.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #finger}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setFinger(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.finger.set(this,body,contentType);
	}

	/**
	 * Returns the content type of the media {@link #noUrl}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getNoUrlContentType()
	{
		return AMediaItem.noUrl.getContentType(this);
	}

	/**
	 * Returns whether media {@link #noUrl} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isNoUrlNull()
	{
		return AMediaItem.noUrl.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #noUrl}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getNoUrlLastModified()
	{
		return AMediaItem.noUrl.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #noUrl}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getNoUrlLength()
	{
		return AMediaItem.noUrl.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #noUrl}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getNoUrlBody()
	{
		return AMediaItem.noUrl.getBody(this);
	}

	/**
	 * Writes the body of media {@link #noUrl} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getNoUrlBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AMediaItem.noUrl.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #noUrl} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getNoUrlBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AMediaItem.noUrl.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #noUrl}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setNoUrl(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value noUrl)
			throws
				java.io.IOException
	{
		AMediaItem.noUrl.set(this,noUrl);
	}

	/**
	 * Sets the content of media {@link #noUrl}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setNoUrl(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AMediaItem.noUrl.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #noUrl}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setNoUrl(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.noUrl.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #noUrl}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setNoUrl(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AMediaItem.noUrl.set(this,body,contentType);
	}

	/**
	 * Returns a URL the content of {@link #thumbnail} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getThumbnailURL()
	{
		return AMediaItem.thumbnail.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnail} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getThumbnailLocator()
	{
		return AMediaItem.thumbnail.getLocator(this);
	}

	/**
	 * Returns a URL the content of {@link #thumbnail} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURLWithFallbackToSource")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getThumbnailURLWithFallbackToSource()
	{
		return AMediaItem.thumbnail.getURLWithFallbackToSource(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnail} is available under, falling back to source if necessary.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocatorWithFallbackToSource")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getThumbnailLocatorWithFallbackToSource()
	{
		return AMediaItem.thumbnail.getLocatorWithFallbackToSource(this);
	}

	/**
	 * Returns the body of {@link #thumbnail}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getThumbnail()
			throws
				java.io.IOException
	{
		return AMediaItem.thumbnail.get(this);
	}

	/**
	 * Returns a URL the content of {@link #thumbnailOfNoUrl} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getThumbnailOfNoUrlURL()
	{
		return AMediaItem.thumbnailOfNoUrl.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #thumbnailOfNoUrl} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getThumbnailOfNoUrlLocator()
	{
		return AMediaItem.thumbnailOfNoUrl.getLocator(this);
	}

	/**
	 * Returns the body of {@link #thumbnailOfNoUrl}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getThumbnailOfNoUrl()
			throws
				java.io.IOException
	{
		return AMediaItem.thumbnailOfNoUrl.get(this);
	}

	/**
	 * Returns the body of {@link #noUrlThumbnail}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getNoUrlThumbnail()
			throws
				java.io.IOException
	{
		return AMediaItem.noUrlThumbnail.get(this);
	}

	/**
	 * Returns the body of {@link #noUrlThumbnailOfNoUrl}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getNoUrlThumbnailOfNoUrl()
			throws
				java.io.IOException
	{
		return AMediaItem.noUrlThumbnailOfNoUrl.get(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for aMediaItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AMediaItem> TYPE = com.exedio.cope.TypesBound.newType(AMediaItem.class,AMediaItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AMediaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
