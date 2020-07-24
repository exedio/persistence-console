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

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.misc.MediaSummary;
import com.exedio.cope.pattern.MediaFingerprintOffset;
import com.exedio.cope.pattern.MediaInfo;
import com.exedio.cope.pattern.MediaPath;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import javax.servlet.http.HttpServletRequest;

final class MediaStatsCop extends ConsoleCop<Void>
{
	static final String TAB_ALL = "mediastats";
	static final String TAB_GUESSUNG_PREVENTED = "mediaguess";
	static final String TAB_FINGER_PRINTING = "mediafinger";

	final Variant variant;
	final double fingerprintRampStep;

	static final String FINGER_OFFSET_RAMP_STEP = "fors";
	private static final double FINGER_OFFSET_RAMP_STEP_DEFAULT = 0.002;

	MediaStatsCop(final Args args, final Variant variant)
	{
		this(args, variant, FINGER_OFFSET_RAMP_STEP_DEFAULT);
	}

	private MediaStatsCop(final Args args, final Variant variant, final double fingerprintRampStep)
	{
		super(variant.tab, variant.name, args);
		this.variant = variant;
		this.fingerprintRampStep = fingerprintRampStep;

		addParameter(FINGER_OFFSET_RAMP_STEP, fingerprintRampStep, FINGER_OFFSET_RAMP_STEP_DEFAULT);
	}

	static MediaStatsCop getMediaStatsCop(final Args args, final Variant variant, final HttpServletRequest request)
	{
		return new MediaStatsCop(
				args, variant,
				getDoubleParameter(request, FINGER_OFFSET_RAMP_STEP, FINGER_OFFSET_RAMP_STEP_DEFAULT));
	}

	private void addParameter(final String key, final double value, final double defaultValue)
	{
		//noinspection FloatingPointEquality
		if(value==defaultValue)
			return;

		addParameter(key, String.valueOf(value));
	}

	private static double getDoubleParameter(
			final HttpServletRequest request,
			final String name,
			final double defaultValue)
	{
		final String value = request.getParameter(name);
		return (value==null) ? defaultValue : parseDouble(value);
	}

	@Override
	protected MediaStatsCop newArgs(final Args args)
	{
		return new MediaStatsCop(args, variant, fingerprintRampStep);
	}

	@Override
	ChecklistIcon getChecklistIcon(final Model model)
	{
		return variant.getChecklistIcon(model);
	}

	enum Variant
	{
		all(TAB_ALL, "Media"),
		guessingPrevented(TAB_GUESSUNG_PREVENTED, "Media Url Guessing")
		{
			@Override boolean accepts(final MediaPath path)
			{
				return path.isUrlGuessingPrevented();
			}

			@Override ChecklistIcon getChecklistIcon(final Model model)
			{
				for(final Type<?> type : model.getTypes())
					for(final Feature feature : type.getDeclaredFeatures())
						if(feature instanceof MediaPath &&
							((MediaPath)feature).isUrlGuessingPrevented())
								return
									MediaPath.isUrlGuessingPreventedSecurely(ConnectToken.getProperties(model))
									? ChecklistIcon.ok
									: ChecklistIcon.error;

				return ChecklistIcon.empty;
			}

			@Override boolean writeUrlGuessingPrevented()
			{
				return false;
			}
		},
		fingerprint(TAB_FINGER_PRINTING, "Media Fingerprinting")
		{
			@Override boolean accepts(final MediaPath path)
			{
				return path.isUrlFingerPrinted();
			}

			@Override boolean writeUrlFingerPrinted()
			{
				return false;
			}
		};

		final String tab;
		final String name;

		Variant(final String tab, final String name)
		{
			this.tab = tab;
			this.name = name;
		}

		/**
		 * @param path used by subclasses
		 */
		boolean accepts(final MediaPath path)
		{
			return true;
		}

		/**
		 * @param model used by subclasses
		 */
		ChecklistIcon getChecklistIcon(final Model model)
		{
			return null;
		}

		boolean writeUrlGuessingPrevented()
		{
			return true;
		}

		boolean writeUrlFingerPrinted()
		{
			return true;
		}
	}

	static final String FINGER_OFFSET_RESET           = "fingerprintOffset.reset";
	static final String FINGER_OFFSET_SET_VALUE_PARAM = "fingerprintOffset.setValueAndResetRamp.param";
	static final String FINGER_OFFSET_SET_VALUE       = "fingerprintOffset.setValueAndResetRamp";
	static final String FINGER_OFFSET_RAMP_DOWN       = "fingerprintOffset.ramp.down";
	static final String FINGER_OFFSET_RAMP_VALUE      = "fingerprintOffset.ramp.value";
	static final String FINGER_OFFSET_RAMP_UP         = "fingerprintOffset.ramp.up";
	@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
	static final String FINGER_OFFSET_WARNING =
			"This operation invalidates media caches on all levels.\\n" +
			"Do you really want to do this?";

	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);

		if(isPost(request))
		{
			final MediaFingerprintOffset offset =
					model.getConnectProperties().mediaFingerprintOffset();

			if(request.getParameter(FINGER_OFFSET_RESET)!=null)
			{
				offset.reset();
			}
			else if(request.getParameter(FINGER_OFFSET_SET_VALUE)!=null)
			{
				offset.setValueAndResetRamp(
						parseInt(request.getParameter(FINGER_OFFSET_SET_VALUE_PARAM)));
			}
			else if(
					request.getParameter(FINGER_OFFSET_RAMP_DOWN)!=null ||
					request.getParameter(FINGER_OFFSET_RAMP_UP  )!=null)
			{
				offset.setRamp(
						parseDouble(request.getParameter(FINGER_OFFSET_RAMP_VALUE)));
			}
		}
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	static String formatRamp(final double ramp)
	{
		return new DecimalFormat("0.0000").format(ramp);
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = out.model;

		final ArrayList<MediaPath> medias = new ArrayList<>();

		for(final Type<?> type : model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaPath)
				{
					final MediaPath path = (MediaPath)feature;
					if(variant.accepts(path))
						medias.add(path);
				}

		final boolean isUrlGuessingNotSecure =
			variant==Variant.guessingPrevented &&
			!medias.isEmpty() &&
			!MediaPath.isUrlGuessingPreventedSecurely(ConnectToken.getProperties(model));

		final MediaInfo[] infos = new MediaInfo[medias.size()];
		int mediaIndex = 0;
		for(final MediaPath media : medias)
			infos[mediaIndex++] = media.getInfo();
		final MediaSummary summary = new MediaSummary(infos);

		switch(variant)
		{
			case fingerprint:
				final MediaFingerprintOffset offset =
						model.getConnectProperties().mediaFingerprintOffset();
				final double rampCurrent = offset.getRamp();
				MediaFingerprint_Jspm.write(
						this, out,
						offset.getInfo(),
						offset.isInitial(),
						limitRamp(rampCurrent - fingerprintRampStep),
						rampCurrent,
						limitRamp(rampCurrent + fingerprintRampStep)
				);
				break;
			case all:
			case guessingPrevented:
				// disable warning about incomplete switch
				break;
		}
		Media_Jspm.writeBody(this, out, Column.values().length+1, isUrlGuessingNotSecure, infos, summary);
	}

	private static double limitRamp(final double ramp)
	{
		if(ramp<0.0)
			return 0.0;
		//noinspection ManualMinMaxCalculation OK: easier to read
		if(ramp>1.0)
			return 1.0;

		return ramp;
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
	static void writeTableHeader(final Out out)
	{
		final String[] shortNames = new String[Column.values().length];
		for(final Column c : Column.values())
			shortNames[c.ordinal()] = c.shortName;

		final String[] otherNames = {
				"Type",
				"Name",
				"Description"
		};

		final String[] names = new String[Column.values().length + otherNames.length];
		for(final Column c : Column.values())
			names[c.ordinal()] = c.name;
		System.arraycopy(otherNames, 0, names, Column.values().length, otherNames.length);

		ColoredTable_Jspm.writeHeader(out, names, shortNames);
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
	static Out.Consumer[] format(final MediaSummary summary)
	{
		final Out.Consumer[] result = new Out.Consumer[Column.values().length];
		Arrays.setAll(result, i -> Column.values()[i].summary.apply(summary));
		return result;
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
	static Out.Consumer[] format(
			final MediaInfo info,
			final ConsoleCop<?> cop)
	{
		final Out.Consumer[] result = new Out.Consumer[Column.values().length];
		Arrays.setAll(result, i -> Column.values()[i].info.apply(info, cop));
		return result;
	}

	private enum Column
	{
		RedirectFrom  ("Redirect From (301)",   "rf",  MediaSummary::getRedirectFrom,   MediaInfo::getRedirectFrom,   null),
		Exception     ("Exception (500)",       "ex",  MediaSummary::getException,      MediaInfo::getException,      MediaErrorLogCop.Kind.Exception),
		InvalidSpecial("Invalid Special (404)", "is",  MediaSummary::getInvalidSpecial, MediaInfo::getInvalidSpecial, MediaErrorLogCop.Kind.InvalidSpecial),
		GuessedUrl    ("Guessed Url (404)",     "gss", MediaSummary::getGuessedUrl,     MediaInfo::getGuessedUrl,     MediaErrorLogCop.Kind.GuessedUrl),
		NotAnItem     ("Not An Item (404)",     "nai", MediaSummary::getNotAnItem,      MediaInfo::getNotAnItem,      MediaErrorLogCop.Kind.NotAnItem),
		NoSuchItem    ("No Such Item (404)",    "nsi", MediaSummary::getNoSuchItem,     MediaInfo::getNoSuchItem,     MediaErrorLogCop.Kind.NoSuchItem),
		Moved         ("Moved (301)",           "mv",  MediaSummary::getMoved,          MediaInfo::getMoved,          null),
		IsNull        ("Is Null (404)",         "in",  MediaSummary::getIsNull,         MediaInfo::getIsNull,         MediaErrorLogCop.Kind.IsNull),
		NotComputable ("Not Computable (404)",  "nc",  MediaSummary::getNotComputable,  MediaInfo::getNotComputable,  MediaErrorLogCop.Kind.NotComputable),
		NotModified   ("Not Modified (304)",    "nm",  MediaSummary::getNotModified,    MediaInfo::getNotModified,    null),
		Delivered     ("Delivered (200/301)",   "del", MediaSummary::getDelivered,      MediaInfo::getDelivered,      null),
		Ratio("log<sub>10</sub> Not Modified / Delivered", "ratio",
			s ->
					out -> out.writeRatio(s.getNotModified(), s.getDelivered()),
			(i,cop) ->
					out -> out.writeRatio(i.getNotModified(), i.getDelivered()));

		final String name;
		final String shortName;
		final Function<MediaSummary,Out.Consumer> summary;
		final BiFunction<MediaInfo,ConsoleCop<?>,Out.Consumer> info;

		Column(
				final String name,
				final String shortName,
				final Function<MediaSummary,Out.Consumer> summary,
				final BiFunction<MediaInfo,ConsoleCop<?>,Out.Consumer> info)
		{
			this.name = name;
			this.shortName = shortName;
			this.summary = summary;
			this.info = info;
		}

		Column(
				final String name,
				final String shortName,
				final ToIntFunction<MediaSummary> summary,
				final ToIntFunction<MediaInfo> info,
				final MediaErrorLogCop.Kind errorKind)
		{
			this.name = name;
			this.shortName = shortName;
			this.summary = s -> out -> out.writeStatic(Format.formatAndHide(0, summary.applyAsInt(s)));
			this.info = (i,cop) -> out ->
			{
				if(errorKind!=null)
				{
					out.writeStatic("<a href=\"");
					out.write(cop.toMediaErrorLog(errorKind, i.getPath()));
					out.writeStatic("\">");
				}
				out.writeStatic(Format.formatAndHide(0, info.applyAsInt(i)));
				if(errorKind!=null)
					out.writeStatic("</a>");
			};
		}
	}

	private static void collapse(final TreeSet<String> contentTypes, final String r, final String a, final String b)
	{
		if(contentTypes.contains(a) && contentTypes.contains(b))
		{
			contentTypes.remove(a);
			contentTypes.remove(b);
			contentTypes.add(r);
		}
	}

	static void printContentTypes(final OutBasic out, final Collection<String> contentTypes)
	{
		final TreeSet<String> sorted = new TreeSet<>(contentTypes);
		collapse(sorted, "image/[p]jpeg", "image/jpeg", "image/pjpeg");
		collapse(sorted, "image/[x-]png", "image/png", "image/x-png");

		String prefix = null;
		boolean first = true;
		for(final String contentType : sorted)
		{
			if(first)
				first = false;
			else
				out.write(", ");

			if(prefix!=null && contentType.startsWith(prefix))
			{
				out.write('~');
				out.write(contentType.substring(prefix.length()-1));
			}
			else
			{
				out.write(contentType);
				final int pos = contentType.indexOf('/');
				if(pos>1)
					prefix = contentType.substring(0, pos+1);
			}
		}
	}

	MediaErrorLogCop toNoSuchPath()
	{
		return MediaErrorLogCop.noSuchPath(args);
	}
}
