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
import java.util.Collection;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;

final class MediaStatsCop extends ConsoleCop<Void>
{
	private final Variant variant;
	final double fingerprintRampStep;

	static final String FINGER_OFFSET_RAMP_STEP = "fors";
	private static final double FINGER_OFFSET_RAMP_STEP_DEFAULT = 0.002;

	public MediaStatsCop(final Args args, final Variant variant)
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
		return (value==null) ? defaultValue : Double.parseDouble(value);
	}

	@Override
	protected MediaStatsCop newArgs(final Args args)
	{
		return new MediaStatsCop(args, variant, fingerprintRampStep);
	}

	enum Variant
	{
		all(TAB_MEDIA_STATS, "Media"),
		fingerprint(TAB_MEDIA_FINGERPRINTING, "Media Fingerprinting")
		{
			@Override boolean accepts(final MediaPath path)
			{
				return path.isUrlFingerPrinted();
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
	}

	static final String FINGER_OFFSET_RESET           = "fingerprintOffset.reset";
	static final String FINGER_OFFSET_SET_VALUE_PARAM = "fingerprintOffset.setValueAndResetRamp.param";
	static final String FINGER_OFFSET_SET_VALUE       = "fingerprintOffset.setValueAndResetRamp";
	static final String FINGER_OFFSET_RAMP_DOWN        = "fingerprintOffset.ramp.down";
	static final String FINGER_OFFSET_RAMP_VALUE       = "fingerprintOffset.ramp.value";
	static final String FINGER_OFFSET_RAMP_UP          = "fingerprintOffset.ramp.up";
	static final String FINGER_OFFSET_WARNING = "This operation invalidates media caches on all levels.\\nDo you really want to do this?";

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

	static String formatRamp(final double ramp)
	{
		return new DecimalFormat("0.0000").format(ramp);
	}

	@Override
	final void writeBody(final Out out)
	{
		final Model model = out.model;

		final ArrayList<MediaPath> medias = new ArrayList<MediaPath>();
		boolean isUrlGuessingPrevented = false;

		for(final Type<?> type : model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaPath)
				{
					final MediaPath path = (MediaPath)feature;
					if(variant.accepts(path))
						medias.add(path);
					if(!isUrlGuessingPrevented && path.isUrlGuessingPrevented())
						isUrlGuessingPrevented = true;
				}

		final boolean isUrlGuessingNotSecure =
			isUrlGuessingPrevented &&
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
			default:
				// disable warning about incomplete switch
				break;
		}
		Media_Jspm.writeBody(this, out, shortNames.length+1, isUrlGuessingNotSecure, infos, summary);
	}

	private static double limitRamp(final double ramp)
	{
		if(ramp<0.0)
			return 0.0;
		if(ramp>1.0)
			return 1.0;

		return ramp;
	}

	static void writeTableHeader(final Out out)
	{
		ColoredTable_Jspm.writeHeader(out, names, shortNames);
	}

	private static final String[] names = {
			"Redirect From <small>(301)</small>",
			"Exception <small>(500)</small>",
			"Invalid Special <small>(404)</small>",
			"Guessed Url <small>(404)</small>",
			"Not An Item <small>(404)</small>",
			"No Such Item <small>(404)</small>",
			"Moved <small>(301)</small>",
			"Is Null <small>(404)</small>",
			"Not Computable <small>(404)</small>",
			"Not Modified <small>(304)</small>",
			"Delivered <small>(200/301)</small>",
			"log<sub>10</sub> Not Modified / Delivered",
			"Type",
			"Name",
			"Description",
		};
	private static final String[] shortNames = {
			"rf",
			"ex",
			"is",
			"gss",
			"nai",
			"nsi",
			"mv",
			"in",
			"nc",
			"nm",
			"del",
			"ratio",
		};

	static final String[] format(final MediaSummary summary)
	{
		return format(new int[]{
				summary.getRedirectFrom(),
				summary.getException(),
				summary.getInvalidSpecial(),
				summary.getGuessedUrl(),
				summary.getNotAnItem(),
				summary.getNoSuchItem(),
				summary.getMoved(),
				summary.getIsNull(),
				summary.getNotComputable(),
				summary.getNotModified(),
				summary.getDelivered()
		});
	}

	static final String[] format(final MediaInfo info)
	{
		return format(new int[]{
				info.getRedirectFrom(),
				info.getException(),
				info.getInvalidSpecial(),
				info.getGuessedUrl(),
				info.getNotAnItem(),
				info.getNoSuchItem(),
				info.getMoved(),
				info.getIsNull(),
				info.getNotComputable(),
				info.getNotModified(),
				info.getDelivered()
		});
	}

	private static final String[] format(final int[] numbers)
	{
		final int length = numbers.length;
		final String[] result = new String[length];
		for(int i = 0; i<length; i++)
			result[i] = Format.formatAndHide(0, numbers[i]);
		return result;
	}

	private static final void collapse(final TreeSet<String> contentTypes, final String r, final String a, final String b)
	{
		if(contentTypes.contains(a) && contentTypes.contains(b))
		{
			contentTypes.remove(a);
			contentTypes.remove(b);
			contentTypes.add(r);
		}
	}

	static final void printContentTypes(final OutBasic out, final Collection<String> contentTypes)
	{
		final TreeSet<String> sorted = new TreeSet<String>(contentTypes);
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
}
