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

import static java.util.Collections.unmodifiableCollection;

import com.exedio.cope.Model;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cops.Cop;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;
import com.exedio.cops.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract class ConsoleCop<S> extends Cop
{
	enum DatePrecision
	{
		m ("HH:mm", "Minutes"),
		s ("HH:mm:ss", "Seconds"),
		ms("HH:mm:ss'<small>'.SSS'</small>'", "Milliseconds");

		final String pattern;
		final String menuText;

		DatePrecision(final String pattern, final String menuText)
		{
			this.pattern = pattern;
			this.menuText = menuText;
		}
	}

	protected static final class Args
	{
		private static final String AUTO_REFRESH = "ar";
		private static final String DATE_PRECISION = "dp";
		private static final String MEDIA_URL_PREFIX = "mup";

		final App app;
		final int autoRefresh;
		final DatePrecision datePrecision;
		final String mediaURLPrefix;

		Args(
				final App app,
				final int autoRefresh,
				final DatePrecision datePrecision,
				final String mediaURLPrefix)
		{
			this.app = app;
			this.autoRefresh = autoRefresh;
			this.datePrecision = datePrecision;
			this.mediaURLPrefix = mediaURLPrefix;
		}

		Args(
				final App app,
				final HttpServletRequest request,
				final ConsoleServlet servlet)
		{
			this.app = app;
			this.autoRefresh = getIntParameter(request, AUTO_REFRESH, 0);
			this.datePrecision = getEnumParameter(request, DATE_PRECISION, DatePrecision.m);
			this.mediaURLPrefix = request.getParameter(MEDIA_URL_PREFIX);
			securityCheckMediaURLPrefix(servlet, request);
		}

		void addParameters(final ConsoleCop<?> cop)
		{
			cop.addParameter(AUTO_REFRESH, autoRefresh, 0);
			cop.addParameter(DATE_PRECISION, datePrecision, DatePrecision.m);
			cop.addParameter(MEDIA_URL_PREFIX, mediaURLPrefix);
		}

		/**
		 * BEWARE !!!
		 * Without this check this is probably a security vulnerability.
		 */
		private void securityCheckMediaURLPrefix(final ConsoleServlet servlet, final HttpServletRequest request)
		{
			final List<String> mediaURLPrefixes = servlet.getMediaURLPrefixes(request);
			if(mediaURLPrefix!=null &&
				!mediaURLPrefixes.contains(mediaURLPrefix))
				throw new RuntimeException(">" + mediaURLPrefix + "<  " + mediaURLPrefixes);
		}

		Args toAutoRefresh(final int autoRefresh)
		{
			return new Args(app, autoRefresh, datePrecision, mediaURLPrefix);
		}

		Args toDatePrecision(final DatePrecision datePrecision)
		{
			return new Args(app, autoRefresh, datePrecision, mediaURLPrefix);
		}

		Args toMediaURLPrefix(final String mediaURLPrefix)
		{
			return new Args(app, autoRefresh, datePrecision, mediaURLPrefix);
		}
	}

	private static final String NAME_POSTFIX = ".html";
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	final String name;
	final Args args;
	final App app;
	@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
	static final int[] AUTO_REFRESHS = {0, 2, 5, 15, 60};

	protected ConsoleCop(final String tab, final String name, final Args args)
	{
		super(tab + NAME_POSTFIX);
		this.name = name;
		this.args = args;
		//noinspection ThisEscapedInObjectConstruction
		args.addParameters(this);
		this.app = args.app;
	}

	long start = 0;

	/**
	 * @param request used in subclasses
	 * @param model used in subclasses
	 */
	void initialize(final HttpServletRequest request, final Model model)
	{
		start = System.currentTimeMillis();
	}

	@Nonnull
	protected abstract ConsoleCop<S> newArgs(final Args args);

	final ConsoleCop<S> toAutoRefresh(final int autoRefresh)
	{
		return newArgs(args.toAutoRefresh(autoRefresh));
	}

	final ConsoleCop<S> toDatePrecision(final DatePrecision datePrecision)
	{
		return newArgs(args.toDatePrecision(datePrecision));
	}

	final ConsoleCop<S> toMediaURLPrefix(final String mediaURLPrefix)
	{
		return newArgs(args.toMediaURLPrefix(mediaURLPrefix));
	}

	MediaCop toMedia(final MediaPath media)
	{
		return new MediaCop(args, media);
	}

	MediaErrorLogCop toMediaErrorLog(
			final MediaErrorLogCop.Kind kind,
			final MediaPath media)
	{
		return new MediaErrorLogCop(args, kind, media);
	}

	int getResponseStatus()
	{
		return HttpServletResponse.SC_OK;
	}

	final ConsoleCop<?>[][] getMenu()
	{
		final TestCop.TestArgs testArgs = new TestCop.TestArgs();
		return
			new ConsoleCop<?>[][]{
				new ConsoleCop<?>[]{
					new ConnectCop(args),
					new ConnectTokenCop(args),
					new EnvironmentCop(args),
				},
				new ConsoleCop<?>[]{
					new SchemaCop(args),
					new UnsupportedConstraintCop(args, testArgs),
					new UnsupportedCheckConstraintByTableCop(args, testArgs),
					new MultiTableCheckConstraintCop(args, testArgs),
					new UpdateCounterCop(args, testArgs),
					new SequenceCop(args, testArgs),
					new TypeColumnCop(args, testArgs),
					new TypeCompletenessCop(args, testArgs),
					new CopyConstraintCop(args, testArgs),
					new EnumSingletonCop(args, testArgs),
					new HashConstraintCop(args, testArgs),
					new CustomQueryConstraintCop(args, testArgs),
				},
				new ConsoleCop<?>[]{
					new OptionalFieldCop(args, testArgs),
					new EmptyStringFieldCop(args, testArgs),
					new MinLengthStringFieldCop(args, testArgs),
					new TypeFieldCop(args, testArgs),
					new FeatureFieldCop(args, testArgs),
					new DispatcherFailureDeprecatedCop(args, testArgs),
					new CharacterNulCop(args, testArgs),
					new SavepointCop(args),
					new PurgeCop(args),
					new RevisionCop(args),
					new RevisionSheetCop(args),
				},
				new ConsoleCop<?>[]{
					new DatabaseLogCop(args),
					new DatabaseMetricsCop(args),
				},
				new ConsoleCop<?>[]{
					new TransactionCop(args),
					new ConnectionPoolCop(args),
					new ItemCacheCop(args),
					new QueryCacheCop(args),
					new ThreadCop(args),
					new DataVaultCop(args),
					new DataVaultTrailCop(args, testArgs),
					new ChangeListenerCop(args),
					new ClusterCop(args),
				},
				new ConsoleCop<?>[]{
					new MediaStatsCop(args, MediaStatsCop.Variant.all),
					new MediaStatsCop(args, MediaStatsCop.Variant.guessingPrevented),
					new MediaStatsCop(args, MediaStatsCop.Variant.fingerprint),
					new MediaTestableCop(args, testArgs),
					new MediaTypeCop(args, testArgs),
				},
				new ConsoleCop<?>[]{
					new VmCop(args, false, false),
					new OutOfMemoryErrorCop(args),
					new RegisteredDriversCop(args, false, false, false),
					new EnumsCop(args),
					new HiddenCop(args),
					new DataFieldCop(args),
					new SuspicionsCop(args),
					new SerializationCheckCop(args),
					new HashCop(args),
				},
				new ConsoleCop<?>[]{
					new ChecklistsCop(args),
				}};
	}

	String[] getHeadingHelp()
	{
		return EMPTY_STRING_ARRAY;
	}

	final String getStart()
	{
		if(start==0)
			throw new RuntimeException();

		//noinspection SimpleDateFormatWithoutLocale timezone is in output, reports server time
		return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z (z)").format(new Date(start));
	}

	final long getDuration()
	{
		if(start==0)
			throw new RuntimeException();

		return System.currentTimeMillis() - start;
	}

	/**
	 * @see ConsoleServlet#stylesheetBody
	 */
	boolean hasJsComponent()
	{
		return false;
	}

	ChecklistIcon getChecklistIcon()
	{
		return null;
	}

	/**
	 * TODO get rid of unsafe-inline
	 */
	boolean requiresUnsafeInlineStyle()
	{
		return false;
	}

	/**
	 * TODO get rid of unsafe-inline
	 */
	boolean requiresUnsafeInlineScript()
	{
		return false;
	}

	String getExternalImgSrc()
	{
		return null;
	}

	/**
	 * @param out used in subclasses
	 */
	void writeHead(final Out out)
	{
		// default implementation does nothing
	}

	abstract void writeBody(Out out);

	private final LinkedHashSet<Resource> onDomReady = new LinkedHashSet<>();

	final void onDomReady(final Resource resource)
	{
		onDomReady.add(resource);
	}

	final Iterable<Resource> onDomReady()
	{
		return unmodifiableCollection(onDomReady);
	}


	boolean isAjax()
	{
		return false;
	}

	void writeAjax(@SuppressWarnings("unused") final Out out)
	{
		throw new RuntimeException(getClass().getName());
	}

	static final ConsoleCop<?> getCop(
			final App app,
			final Model model,
			final HttpServletRequest request,
			final ConsoleServlet servlet)
	{
		final Args args = new Args(app, request, servlet);
		final String pathInfo = request.getPathInfo();

		if("/".equals(pathInfo))
			return new ConnectCop(args);

		if(pathInfo==null || !pathInfo.startsWith("/") || !pathInfo.endsWith(NAME_POSTFIX))
			return new NotFound(args, pathInfo);

		final String tab = pathInfo.substring(1, pathInfo.length()-NAME_POSTFIX.length());
		switch(tab)
		{
			case PurgeCop.TAB:
				return new PurgeCop(args);
			case SchemaCop.TAB:
				return SchemaCop.getSchemaCop(args, request);
			case SavepointCop.TAB:
				return new SavepointCop(args);
			case UnsupportedConstraintCop.TAB:
				return UnsupportedConstraintCop.getUnsupportedConstraintCop(args, new TestCop.TestArgs(request), request);
			case UnsupportedCheckConstraintByTableCop.TAB:
				return new UnsupportedCheckConstraintByTableCop(args, new TestCop.TestArgs(request));
			case MultiTableCheckConstraintCop.TAB:
				return new MultiTableCheckConstraintCop(args, new TestCop.TestArgs(request));
			case TypeColumnCop.TAB:
				return new TypeColumnCop(args, new TestCop.TestArgs(request));
			case TypeCompletenessCop.TAB:
				return new TypeCompletenessCop(args, new TestCop.TestArgs(request));
			case UpdateCounterCop.TAB:
				return new UpdateCounterCop(args, new TestCop.TestArgs(request));
			case CopyConstraintCop.TAB:
				return new CopyConstraintCop(args, new TestCop.TestArgs(request));
			case EnumSingletonCop.TAB:
				return new EnumSingletonCop(args, new TestCop.TestArgs(request));
			case DispatcherFailureDeprecatedCop.TAB:
				return new DispatcherFailureDeprecatedCop(args, new TestCop.TestArgs(request));
			case CharacterNulCop.TAB:
				return new CharacterNulCop(args, new TestCop.TestArgs(request));
			case ConnectCop.TAB:
				return new ConnectCop(args);
			case ConnectTokenCop.TAB:
				return new ConnectTokenCop(args);
			case OptionalFieldCop.TAB:
				return new OptionalFieldCop(args, new TestCop.TestArgs(request));
			case EmptyStringFieldCop.TAB:
				return new EmptyStringFieldCop(args, new TestCop.TestArgs(request));
			case MinLengthStringFieldCop.TAB:
				return new MinLengthStringFieldCop(args, new TestCop.TestArgs(request));
			case TypeFieldCop.TAB:
				return TypeFieldCop.getTypeFieldCop(args, new TestCop.TestArgs(request), request);
			case FeatureFieldCop.TAB:
				return FeatureFieldCop.getFeatureFieldCop(args, new TestCop.TestArgs(request), request);
			case RevisionCop.TAB:
				return new RevisionCop(args, request);
			case RevisionSheetCop.TAB:
				return new RevisionSheetCop(args);
			case ConnectionPoolCop.TAB:
				return new ConnectionPoolCop(args);
			case TransactionCop.TAB:
				return new TransactionCop(args, request);
			case DatabaseLogCop.TAB:
				return new DatabaseLogCop(args);
			case DatabaseMetricsCop.TAB:
				return new DatabaseMetricsCop(args);
			case ItemCacheCop.TAB:
				return ItemCacheCop.getItemCacheCop(args, request);
			case QueryCacheCop.TAB:
				return QueryCacheCop.getQueryCacheCop(args, request);
			case DataVaultCop.TAB:
				return new DataVaultCop(args);
			case DataVaultTrailCop.TAB:
				return DataVaultTrailCop.getDataVaultTrailCop(args, new TestCop.TestArgs(request), request);
			case SequenceCop.TAB:
				return new SequenceCop(args, new TestCop.TestArgs(request));
			case SuspicionsCop.TAB:
				return new SuspicionsCop(args);
			case SerializationCheckCop.TAB:
				return new SerializationCheckCop(args);
			case DataFieldCop.TAB:
				return new DataFieldCop(args);
			case MediaStatsCop.TAB_ALL:
				return MediaStatsCop.getMediaStatsCop(args, MediaStatsCop.Variant.all, request);
			case MediaStatsCop.TAB_GUESSUNG_PREVENTED:
				return MediaStatsCop.getMediaStatsCop(args, MediaStatsCop.Variant.guessingPrevented, request);
			case MediaStatsCop.TAB_FINGER_PRINTING:
				return MediaStatsCop.getMediaStatsCop(args, MediaStatsCop.Variant.fingerprint, request);
			case MediaErrorLogCop.TAB:
				return MediaErrorLogCop.getMediaErrorLogCop(model, args, request);
			case MediaTestableCop.TAB:
				return new MediaTestableCop(args, new TestCop.TestArgs(request));
			case MediaTypeCop.TAB:
				return new MediaTypeCop(args, new TestCop.TestArgs(request));
			case HashConstraintCop.TAB:
				return new HashConstraintCop(args, new TestCop.TestArgs(request));
			case CustomQueryConstraintCop.TAB:
				return new CustomQueryConstraintCop(args, new TestCop.TestArgs(request));
			case ClusterCop.TAB:
				return new ClusterCop(args);
			case ThreadCop.TAB:
				return new ThreadCop(args);
			case VmCop.TAB:
				return VmCop.getVmCop(args, request);
			case OutOfMemoryErrorCop.TAB:
				return OutOfMemoryErrorCop.getOutOfMemoryErrorCop(args);
			case RegisteredDriversCop.TAB:
				return RegisteredDriversCop.getRegisteredDriversCop(args, request);
			case EnumsCop.TAB:
				return new EnumsCop(args);
			case EnumCop.TAB:
				return EnumCop.getEnumCop(args, request);
			case EnvironmentCop.TAB:
				return new EnvironmentCop(args);
			case HashCop.TAB:
				return new HashCop(args);
			case HiddenCop.TAB:
				return new HiddenCop(args);
			case ChangeListenerCop.TAB:
				return new ChangeListenerCop(args, request);
			case ChecklistsCop.TAB:
				return new ChecklistsCop(args);
		}

		final MediaCop mediaCop = MediaCop.getMediaCop(model, args, request);
		if(mediaCop!=null)
			return mediaCop;

		return new NotFound(args, pathInfo);
	}

	private static final class NotFound extends ConsoleCop<Void>
	{
		private final String pathInfo;

		NotFound(final Args args, final String pathInfo)
		{
			super("Not Found", "Not Found", args);
			this.pathInfo = pathInfo;
		}

		@Override
		protected ConsoleCop<Void> newArgs(final Args args)
		{
			return new NotFound(args, pathInfo);
		}

		@Override
		int getResponseStatus()
		{
			return HttpServletResponse.SC_NOT_FOUND;
		}

		@Override
		void writeBody(final Out out)
		{
			Console_Jspm.writeNotFound(out, pathInfo);
		}
	}

	static void writePager(final Out out, final Pageable cop)
	{
		final Pager pager = cop.getPager();
		if(pager.isNeeded())
		{
			out.writeRaw("<tr><td colspan=\"0\" class=\"pager\"><div class=\"right\">");

			out.write(pager.getFrom());
			out.writeRaw('-');
			out.write(pager.getTo());
			out.writeRaw(" of ");
			out.write(pager.getTotal());

			out.writeRaw("<br>per page:");
			for(final Pager newLimit : pager.newLimits())
				Console_Jspm.writePagerButton(out, cop, newLimit, String.valueOf(newLimit.getLimit()), "selected");

			out.writeRaw("</div>");

			Console_Jspm.writePagerButton(out, cop, pager.first(),    "<<", "disabled");
			Console_Jspm.writePagerButton(out, cop, pager.previous(), "<" , "disabled");
			Console_Jspm.writePagerButton(out, cop, pager.next(),     ">" , "disabled");
			Console_Jspm.writePagerButton(out, cop, pager.last(),     ">>", "disabled");

			out.writeRaw(" page ");
			out.write(pager.getPage());
			out.writeRaw(" of ");
			out.write(pager.getTotalPages());
			out.writeRaw("<br>");

			if(pager.hasBeforeNewPages())
				out.writeStatic("...");
			for(final Pager newPage : pager.newPages())
				Console_Jspm.writePagerButton(out, cop, newPage, String.valueOf(newPage.getPage()), "selected");
			if(pager.hasAfterNewPages())
				out.writeStatic("...");

			out.writeRaw("</td></tr>");
		}
	}

	S store()
	{
		return app.store(this);
	}

	/**
	 * Must be implemented when using {@link #store()}
	 */
	S initialStore()
	{
		throw new IllegalArgumentException(getClass().getName());
	}

	@SuppressWarnings("ResultOfMethodCallIgnored") // OK: intended side effect
	protected final void failIfNotConnected()
	{
		app.model.getConnectProperties();
	}
}
