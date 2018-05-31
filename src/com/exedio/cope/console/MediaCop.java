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

package com.exedio.cope.console;

import com.exedio.cope.Condition;
import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.Query;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.pattern.MediaRedirect;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

final class MediaCop extends ConsoleCop<Void> implements Pageable
{
	private static final String MEDIA = "m";
	private static final String MEDIA_INLINE = "ilm";
	private static final String OTHER_INLINE = "ilo";
	private static final String CONTENT_TYPE_MISMATCH = "ctmm";

	static final String TOUCH = "touch";
	static final String TOUCH_OTHER = "touchOther";

	private static final Pager.Config PAGER_CONFIG = new Pager.Config(10, 20, 50, 100, 200, 500);

	final MediaPath media;
	final MediaPath other;
	final boolean mediaInline;
	final boolean otherInline;
	final boolean contentTypeMismatch;
	private final Pager pager;

	MediaCop(final Args args, final MediaPath media)
	{
		this(args, media, false, false, false, PAGER_CONFIG.newPager());
	}

	private MediaCop(
			final Args args,
			final MediaPath media,
			final boolean mediaInline,
			final boolean otherInline,
			final boolean contentTypeMismatch,
			final Pager pager)
	{
		super("media", "Media - " + media.getID(), args);

		this.media = media;

		if(media instanceof MediaFilter)
			other = ((MediaFilter)media).getSource();
		else if(media instanceof MediaRedirect)
			other = ((MediaRedirect)media).getTarget();
		else
			other = null;

		this.mediaInline = mediaInline;
		this.otherInline = otherInline;
		this.contentTypeMismatch = contentTypeMismatch;
		this.pager = pager;

		addParameter(MEDIA, media.getID());
		addParameter(MEDIA_INLINE, mediaInline);
		addParameter(OTHER_INLINE, otherInline);
		addParameter(CONTENT_TYPE_MISMATCH, contentTypeMismatch);
		//noinspection ThisEscapedInObjectConstruction
		pager.addParameters(this);
	}

	static MediaCop getMediaCop(final Model model, final Args args, final HttpServletRequest request)
	{
		final String mediaID = request.getParameter(MEDIA);
		if(mediaID==null)
			return null;

		return new MediaCop(
				args,
				(MediaPath)model.getFeature(mediaID),
				getBooleanParameter(request, MEDIA_INLINE),
				getBooleanParameter(request, OTHER_INLINE),
				getBooleanParameter(request, CONTENT_TYPE_MISMATCH),
				PAGER_CONFIG.newPager(request));
	}

	@Override
	protected MediaCop newArgs(final Args args)
	{
		return new MediaCop(args, media, mediaInline, otherInline, contentTypeMismatch, pager);
	}

	MediaCop toggleInlineMedia()
	{
		return new MediaCop(args, media, !mediaInline, otherInline, contentTypeMismatch, pager);
	}

	MediaCop toggleInlineOther()
	{
		return new MediaCop(args, media, mediaInline, !otherInline, contentTypeMismatch, pager);
	}

	MediaCop toggleContentTypeMismatch()
	{
		return new MediaCop(args, media, mediaInline, otherInline, !contentTypeMismatch, pager);
	}

	@Override
	public Pager getPager()
	{
		return pager;
	}

	@Override
	public MediaCop toPage(final Pager pager)
	{
		return new MediaCop(args, media, mediaInline, otherInline, contentTypeMismatch, pager);
	}

	MediaCop toOther()
	{
		return new MediaCop(args, other, otherInline, false, contentTypeMismatch, pager);
	}

	boolean canContentTypeMismatch()
	{
		return media instanceof Media;
	}

	boolean canTouch()
	{
		return canTouchInternal(media);
	}

	boolean canTouchOther()
	{
		return canTouchInternal(other);
	}

	private static boolean canTouchInternal(final MediaPath media)
	{
		return
			media instanceof Media &&
			!((Media)media).getLastModified().isFinal();
	}

	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);
		if(isPost(request))
		{
			final String[] touchIds = request.getParameterValues(TOUCH);
			final String[] touchOtherIds = request.getParameterValues(TOUCH_OTHER);
			if(touchIds!=null || touchOtherIds!=null)
			{
				try(TransactionTry tx = model.startTransactionTry("Console Media touch"))
				{
					final Date now = new Date();
					touchInternal(model, now, media, touchIds);
					touchInternal(model, now, other, touchOtherIds);

					tx.commit();
				}
			}
		}
	}

	private static void touchInternal(final Model model, final Date now, final MediaPath media, final String[] touchIds)
	{
		if(touchIds!=null)
		{
			final DateField lastModified = ((Media)media).getLastModified();
			try
			{
				for(final String touchId : touchIds)
					lastModified.set(model.getItem(touchId), now);
			}
			catch(final NoSuchIDException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	void writeHead(final Out out)
	{
		if(mediaInline || otherInline)
			Media_Jspm.writeHead(out);

		StackTrace_Jspm.writeHead(out);
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = out.model;

		Media_Jspm.writeStats(out, this, media.getInfo());
		Media_Jspm.writeErrorLogs(out, "No Such Path"   , MediaPath.getNoSuchPathLogs());
		Media_Jspm.writeErrorLogs(out, "Exception"      , media.getExceptionLogs());
		Media_Jspm.writeErrorLogs(out, "Invalid Special", media.getInvalidSpecialLogs());
		Media_Jspm.writeErrorLogs(out, "Guessed Url"    , media.getGuessedUrlLogs());
		Media_Jspm.writeErrorLogs(out, "Not An Item"    , media.getNotAnItemLogs());
		Media_Jspm.writeErrorLogs(out, "No Such Item"   , media.getNoSuchItemLogs());
		Media_Jspm.writeErrorLogs(out, "Is Null"        , media.getIsNullLogs());
		Media_Jspm.writeErrorLogs(out, "Not Computable" , media.getNotComputableLogs());
		Media_Jspm.writeHorizontalRule(out);

		{
			final List<String> prefixes = out.getMediaURLPrefixes();
			if(!prefixes.isEmpty())
			{
				Media_Jspm.writeMediaURLPrefixes(out, this, args.mediaURLPrefix, prefixes);
				Media_Jspm.writeHorizontalRule(out);
			}
		}

		try(TransactionTry tx = model.startTransactionTry("Console Media list"))
		{
			final Query<? extends Item> q = media.getType().newQuery(isNotNull());
			if(contentTypeMismatch)
				q.narrow(((Media)media).bodyMismatchesContentTypeIfSupported());
			q.setLimit(pager.getOffset(), pager.getLimit());
			q.setOrderBy(media.getType().getThis(), true);
			final Query.Result<? extends Item> items = q.searchAndTotal();
			pager.init(items.getData().size(), items.getTotal());
			Media_Jspm.writeBody(this, out, items);
			tx.commit();
		}
	}

	private Condition isNotNull()
	{
		try
		{
			return media.isNotNull();
		}
		catch(final UnsupportedOperationException ignored)
		{
			return null;
		}
	}
}
