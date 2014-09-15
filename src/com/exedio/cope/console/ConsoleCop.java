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
import com.exedio.cope.console.Stores.Store;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cops.Cop;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract class ConsoleCop<S> extends Cop
{
	enum DatePrecision
	{
		m ("HH:mm"),
		s ("HH:mm:ss"),
		ms("HH:mm:ss'<small>'.SSS'</small>'");

		final String pattern;

		DatePrecision(final String pattern)
		{
			this.pattern = pattern;
		}
	}

	protected static final class Args
	{
		private static final String AUTO_REFRESH = "ar";
		private static final String DATE_PRECISION = "dp";
		private static final String MEDIA_URL_PREFIX = "mup";

		final Stores stores;
		final int autoRefresh;
		final DatePrecision datePrecision;
		final String mediaURLPrefix;

		Args(
				final Stores stores,
				final int autoRefresh,
				final DatePrecision datePrecision,
				final String mediaURLPrefix)
		{
			this.stores = stores;
			this.autoRefresh = autoRefresh;
			this.datePrecision = datePrecision;
			this.mediaURLPrefix = mediaURLPrefix;
		}

		Args(
				final Stores stores,
				final HttpServletRequest request,
				final ConsoleServlet servlet)
		{
			this.stores = stores;
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
			return new Args(stores, autoRefresh, datePrecision, mediaURLPrefix);
		}

		Args toDatePrecision(final DatePrecision datePrecision)
		{
			return new Args(stores, autoRefresh, datePrecision, mediaURLPrefix);
		}

		Args toMediaURLPrefix(final String mediaURLPrefix)
		{
			return new Args(stores, autoRefresh, datePrecision, mediaURLPrefix);
		}
	}

	private static final String NAME_POSTFIX = ".html";
	final String name;
	final Args args;
	static final int[] AUTO_REFRESHS = new int[]{0, 2, 5, 15, 60};

	protected ConsoleCop(final String tab, final String name, final Args args)
	{
		super(tab + NAME_POSTFIX);
		this.name = name;
		this.args = args;
		args.addParameters(this);
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

	int getResponseStatus()
	{
		return HttpServletResponse.SC_OK;
	}

	final ConsoleCop<?>[] getTabs()
	{
		final TestCop.TestArgs testArgs = new TestCop.TestArgs();
		return
			new ConsoleCop<?>[]{
					new ConnectCop(args),
					new ConnectTokenCop(args),
					new PurgeCop(args),
					new SchemaCop(args),
					new SavepointCop(args),
					new UnsupportedConstraintCop(args, testArgs),
					new UnsupportedCheckConstraintCop(args, testArgs),
					new UpdateCounterCop(args, testArgs),
					new SequenceCop(args, testArgs),
					new TypeColumnCop(args, testArgs),
					new TypeCompletenessCop(args, testArgs),
					new CopyConstraintCop(args, testArgs),
					new OptionalFieldCop(args, testArgs),
					new MinLengthStringFieldCop(args, testArgs),
					new FeatureFieldCop(args, testArgs),
					new RevisionCop(args),
					new RevisionSheetCop(args),
					new DatabaseLogCop(args),
					new ConnectionPoolCop(args),
					new TransactionCop(args),
					new ItemCacheCop(args),
					new QueryCacheCop(args),
					new SerializationCheckCop(args),
					new DataFieldCop(args),
					new MediaStatsCop(args),
					new MediaTestableCop(args, testArgs),
					new MediaTypeCop(args, testArgs),
					new ClusterCop(args),
					new ThreadCop(args),
					new VmCop(args, false, false),
					new EnumsCop(args),
					new EnvironmentCop(args),
					new HashCop(args),
					new HiddenCop(args),
					new ChangeListenerCop(args),
				};
	}

	final String getStart()
	{
		if(start==0)
			throw new RuntimeException();

		return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z (z)").format(new Date(start));
	}

	final long getDuration()
	{
		if(start==0)
			throw new RuntimeException();

		return System.currentTimeMillis() - start;
	}

	/**
	 * @param out used in subclasses
	 */
	void writeHead(final Out out)
	{
		// default implementation does nothing
	}

	abstract void writeBody(Out out);


	boolean isAjax()
	{
		return false;
	}

	void writeAjax(@SuppressWarnings("unused") final Out out)
	{
		throw new RuntimeException(getClass().getName());
	}

	static final String TAB_CONNECT = "connect";
	static final String TAB_CONNECT_TOKEN = "connectToken";
	static final String TAB_PURGE = "purge";
	static final String TAB_SCHEMA = "schema";
	static final String TAB_SAVEPOINT = "savepoint";
	static final String TAB_UNSUPPORTED_CONSTRAINTS = "unsupportedconstraints";
	static final String TAB_UNSUPPORTED_CHECK_CONSTRAINTS = "unsupportedcheckconstraints";
	static final String TAB_TYPE_COLUMNS = "typecolumns";
	static final String TAB_TYPE_COMPLETENESS = "typecompleteness";
	static final String TAB_UPDATE_COUNTERS = "updatecounters";
	static final String TAB_COPY_CONSTRAINTS = "copyconstraints";
	static final String TAB_OPTIONAL_FIELDS = "optional";
	static final String TAB_MIN_LENGTH_STRING_FIELDS = "minlength";
	static final String TAB_FEATURE_FIELD = "feature";
	static final String TAB_REVISION = "revision";
	static final String TAB_REVISION_SHEET = "revisionSheet";
	static final String TAB_DATBASE_LOG = "dblogs";
	static final String TAB_CONNECTION_POOL = "connections";
	static final String TAB_TRANSACTION = "transactions";
	static final String TAB_ITEM_CACHE = "itemcache";
	static final String TAB_QUERY_CACHE = "querycache";
	static final String TAB_SEQUENCE = "sequence";
	static final String TAB_SERIALIZATION_CHECK = "serialization";
	static final String TAB_DATA_FIELD = "datafield";
	static final String TAB_MEDIA_STATS = "mediastats";
	static final String TAB_MEDIA_TESTABLE = "mediatestable";
	static final String TAB_MEDIA_TYPE = "mediatype";
	static final String TAB_CLUSTER = "cluster";
	static final String TAB_THREAD = "thread";
	static final String TAB_VM = "vm";
	static final String TAB_ENUMS = "enums";
	static final String TAB_ENUM = "enum";
	static final String TAB_ENVIRONMENT = "environment";
	static final String TAB_HASH = "hash";
	static final String TAB_HIDDEN = "hidden";
	static final String TAB_CHANGE_LISTENER = "changelistener";

	static final ConsoleCop<?> getCop(
			final Stores stores,
			final Model model,
			final HttpServletRequest request,
			final ConsoleServlet servlet)
	{
		final Args args = new Args(stores, request, servlet);
		final String pathInfo = request.getPathInfo();

		if("/".equals(pathInfo))
			return new ConnectCop(args);

		if(pathInfo==null || !pathInfo.startsWith("/") || !pathInfo.endsWith(NAME_POSTFIX))
			return new NotFound(args, pathInfo);

		final String tab = pathInfo.substring(1, pathInfo.length()-NAME_POSTFIX.length());
		if(TAB_PURGE.equals(tab))
			return new PurgeCop(args);
		else if(TAB_SCHEMA.equals(tab))
			return SchemaCop.getSchemaCop(args, request);
		else if(TAB_SAVEPOINT.equals(tab))
			return new SavepointCop(args);
		if(TAB_UNSUPPORTED_CONSTRAINTS.equals(tab))
			return new UnsupportedConstraintCop(args, new TestCop.TestArgs(request));
		if(TAB_UNSUPPORTED_CHECK_CONSTRAINTS.equals(tab))
			return new UnsupportedCheckConstraintCop(args, new TestCop.TestArgs(request));
		if(TAB_TYPE_COLUMNS.equals(tab))
			return new TypeColumnCop(args, new TestCop.TestArgs(request));
		if(TAB_TYPE_COMPLETENESS.equals(tab))
			return new TypeCompletenessCop(args, new TestCop.TestArgs(request));
		if(TAB_UPDATE_COUNTERS.equals(tab))
			return new UpdateCounterCop(args, new TestCop.TestArgs(request));
		if(TAB_COPY_CONSTRAINTS.equals(tab))
			return new CopyConstraintCop(args, new TestCop.TestArgs(request));
		if(TAB_CONNECT.equals(tab))
			return new ConnectCop(args);
		if(TAB_CONNECT_TOKEN.equals(tab))
			return new ConnectTokenCop(args);
		if(TAB_OPTIONAL_FIELDS.equals(tab))
			return new OptionalFieldCop(args, new TestCop.TestArgs(request));
		if(TAB_MIN_LENGTH_STRING_FIELDS.equals(tab))
			return new MinLengthStringFieldCop(args, new TestCop.TestArgs(request));
		if(TAB_FEATURE_FIELD.equals(tab))
			return new FeatureFieldCop(args, new TestCop.TestArgs(request));
		if(TAB_REVISION.equals(tab))
			return new RevisionCop(args, request);
		if(TAB_REVISION_SHEET.equals(tab))
			return new RevisionSheetCop(args);
		if(TAB_CONNECTION_POOL.equals(tab))
			return new ConnectionPoolCop(args);
		if(TAB_TRANSACTION.equals(tab))
			return new TransactionCop(args, request);
		if(TAB_DATBASE_LOG.equals(tab))
			return new DatabaseLogCop(args);
		if(TAB_ITEM_CACHE.equals(tab))
			return new ItemCacheCop(args);
		if(TAB_QUERY_CACHE.equals(tab))
			return QueryCacheCop.getQueryCacheCop(args, request);
		if(TAB_SEQUENCE.equals(tab))
			return new SequenceCop(args, new TestCop.TestArgs(request));
		if(TAB_SERIALIZATION_CHECK.equals(tab))
			return new SerializationCheckCop(args);
		if(TAB_DATA_FIELD.equals(tab))
			return new DataFieldCop(args);
		if(TAB_MEDIA_STATS.equals(tab))
			return new MediaStatsCop(args);
		if(TAB_MEDIA_TESTABLE.equals(tab))
			return new MediaTestableCop(args, new TestCop.TestArgs(request));
		if(TAB_MEDIA_TYPE.equals(tab))
			return new MediaTypeCop(args, new TestCop.TestArgs(request));
		if(TAB_CLUSTER.equals(tab))
			return new ClusterCop(args);
		if(TAB_THREAD.equals(tab))
			return new ThreadCop(args);
		if(TAB_VM.equals(tab))
			return VmCop.getVmCop(args, request);
		if(TAB_ENUMS.equals(tab))
			return new EnumsCop(args);
		if(TAB_ENUM.equals(tab))
			return EnumCop.getEnumCop(args, request);
		if(TAB_ENVIRONMENT.equals(tab))
			return new EnvironmentCop(args);
		if(TAB_HASH.equals(tab))
			return new HashCop(args);
		if(TAB_HIDDEN.equals(tab))
			return new HiddenCop(args);
		if(TAB_CHANGE_LISTENER.equals(tab))
			return new ChangeListenerCop(args, request);

		final MediaCop mediaCop = MediaCop.getMediaCop(model, args, request);
		if(mediaCop!=null)
			return mediaCop;

		return new NotFound(args, pathInfo);
	}

	private static final class NotFound extends ConsoleCop<Void>
	{
		private final String pathInfo;

		protected NotFound(final Args args, final String pathInfo)
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
		final void writeBody(final Out out)
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

	Store<S> getStore()
	{
		return args.stores.getStore(this);
	}

	void putStore(final S value)
	{
		args.stores.putStore(this, value);
	}

	protected static final void failIfNotConnected(final Model model)
	{
		model.getConnectProperties();
	}
}
