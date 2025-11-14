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

import com.exedio.cope.Model;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.pattern.MediaRequestLog;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Function;

final class MediaErrorLogCop extends ConsoleCop<Void>
{
	static final String TAB = "mediaerror";
	private static final String MEDIA = "m";
	private static final String KIND = "k";

	final Kind kind;
	final MediaPath media;

	MediaErrorLogCop(
			final Args args,
			final Kind kind,
			final MediaPath media)
	{
		super(TAB,
				"Media " + kind.caption + " " + (media!=null ? (" - " + media.getID()) : ""),
				args);

		assert (kind==Kind.NoSuchPath) || (media!=null);

		this.kind = kind;
		this.media = media;

		addParameter(KIND, kind, Kind.NoSuchPath);
		if(media!=null)
			addParameter(MEDIA, media.getID());
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	static MediaErrorLogCop noSuchPath(final Args args)
	{
		return new MediaErrorLogCop(args, Kind.NoSuchPath, null);
	}

	static MediaErrorLogCop getMediaErrorLogCop(
			final Model model,
			final Args args,
			final HttpServletRequest request)
	{
		final String mediaID = request.getParameter(MEDIA);

		return new MediaErrorLogCop(
				args,
				getEnumParameter(request, KIND, Kind.NoSuchPath),
				mediaID!=null ? (MediaPath)model.getFeature(mediaID) : null);
	}

	@Override
	protected MediaErrorLogCop newArgs(final Args args)
	{
		return new MediaErrorLogCop(args, kind, media);
	}

	@Override
	void writeHead(final Out out)
	{
		StackTrace_Jspm.writeHead(out);
	}

	@Override
	void writeBody(final Out out)
	{
		Media_Jspm.writeErrorLogs(out, kind.logs.apply(media));
	}

	enum Kind
	{
		NoSuchPath    ("No Such Path"   , m -> MediaPath.getNoSuchPathLogs()),
		Exception     ("Exception"      , MediaPath::getExceptionLogs),
		InvalidSpecial("Invalid Special", MediaPath::getInvalidSpecialLogs),
		GuessedUrl    ("Guessed Url"    , MediaPath::getGuessedUrlLogs),
		NotAnItem     ("Not An Item"    , MediaPath::getNotAnItemLogs),
		NoSuchItem    ("No Such Item"   , MediaPath::getNoSuchItemLogs),
		IsNull        ("Is Null"        , MediaPath::getIsNullLogs);

		final String caption;
		final Function<MediaPath,List<MediaRequestLog>> logs;

		Kind(
				final String caption,
				final Function<MediaPath,List<MediaRequestLog>> logs)
		{
			this.caption = caption;
			this.logs = logs;
		}
	}
}
