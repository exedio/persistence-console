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

import static com.exedio.cope.SetValue.map;
import static io.micrometer.core.instrument.Metrics.globalRegistry;
import static java.lang.System.nanoTime;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ChangeEvent;
import com.exedio.cope.ChangeListener;
import com.exedio.cope.DataField;
import com.exedio.cope.Feature;
import com.exedio.cope.Transaction;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.UniqueHashedMedia;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serial;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("ResultOfObjectAllocationIgnored") // persistent object
public final class ExampleServlet extends CopsServlet
{
	@Serial
	private static final long serialVersionUID = 1l;

	static final String CREATE_SAMPLE_DATA = "createSampleData";
	static final String REMOVE_REVISION_MUTEX = "removeRevisionMutex";

	static final String CONNECT_NAME   = "connect.name";
	static final String CONNECT_COND   = "connect.conditional";
	static final String CONNECT_SUBMIT = "connect.submit";

	static final String TRANSACTION_NAME   = "transaction.name";
	static final String TRANSACTION_ITEMS  = "transaction.items";
	static final String TRANSACTION_SLEEP  = "transaction.sleep";
	static final String TRANSACTION_HOOK_PRE       = "transaction.preCommitHooks";
	static final String TRANSACTION_HOOK_POST      = "transaction.postCommitHooks";
	static final String TRANSACTION_HOOK_DUPLICATE = "transaction.duplicateCommitHooks";
	static final String TRANSACTION_SUBMIT = "transaction.submit";

	static final String ITEM_CACHE_REPLACE = "itemCache.replace";
	static final String QUERY_CACHE_PARAMETER = "queryCache.parameter";
	static final String QUERY_CACHE_SEARCH = "queryCache.search";

	static final String CHANGE_LISTENER_ADD      = "changeListener.add";
	static final String CHANGE_LISTENER_ADD_FAIL = "changeListener.addFail";
	static final String CHANGE_LISTENER_COUNT    = "changeListener.count";

	static final String NUL_CHARACTER    = "nulChar";
	static final String BREAK_MEDIA_HASH = "breakMediaHash";

	static final String TYPE_FIELD_FEATURE = "typeField.feature";
	static final String TYPE_FIELD_STRING  = "typeField.string";
	static final String TYPE_FIELD_SUBMIT  = "typeField.submit";

	static final String FEATURE_FIELD_FEATURE = "featureField.feature";
	static final String FEATURE_FIELD_STRING  = "featureField.string";
	static final String FEATURE_FIELD_SUBMIT  = "featureField.submit";

	private final ArrayList<ConnectToken> connectTokens = new ArrayList<>();
	static final AtomicInteger changeListenerNumber = new AtomicInteger(0);

	@Override
	protected void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		//System.out.println("request ---" + request.getMethod() + "---" + request.getContextPath() + "---" + request.getServletPath() + "---" + request.getPathInfo() + "---" + request.getQueryString() + "---");

		if(Cop.isPost(request))
		{
			if(request.getParameter(CREATE_SAMPLE_DATA)!=null)
				createSampleData();
			else if(request.getParameter(REMOVE_REVISION_MUTEX)!=null)
				Revisions.removeMutex(Main.model);
			else if(request.getParameter(CONNECT_SUBMIT)!=null)
			{
				final String name = replaceNullName(request.getParameter(CONNECT_NAME));
				if(request.getParameter(CONNECT_COND)!=null)
					//noinspection resource OK: ConnectTokens are registered for later return
					ConnectToken.issueIfConnected(Main.model, name);
				else
					connect(name);
			}
			else if(request.getParameter(TRANSACTION_SUBMIT)!=null)
			{
				doTransaction(
						request.getParameter(TRANSACTION_NAME),
						Integer.parseInt(request.getParameter(TRANSACTION_ITEMS)),
						Integer.parseInt(request.getParameter(TRANSACTION_SLEEP)),
						Integer.parseInt(request.getParameter(TRANSACTION_HOOK_PRE)),
						Integer.parseInt(request.getParameter(TRANSACTION_HOOK_POST)),
						request.getParameter(TRANSACTION_HOOK_DUPLICATE)!=null);
			}
			else if(request.getParameter(ITEM_CACHE_REPLACE)!=null)
			{
				replaceItemCache();
			}
			else if(request.getParameter(QUERY_CACHE_SEARCH)!=null)
			{
				try(TransactionTry tx = Main.model.startTransactionTry("queryCache"))
				{
					AnItem.TYPE.search(AnItem.aField.equal(request.getParameter(QUERY_CACHE_PARAMETER)));
					tx.commit();
				}
			}
			else if(request.getParameter(CHANGE_LISTENER_ADD)!=null)
			{
				final int changeListenerCount = Integer.parseInt(request.getParameter(CHANGE_LISTENER_COUNT));
				for(int i = 0; i<changeListenerCount; i++)
					Main.model.addChangeListener(newChangeListener(false));
			}
			else if(request.getParameter(CHANGE_LISTENER_ADD_FAIL)!=null)
			{
				final int changeListenerCount = Integer.parseInt(request.getParameter(CHANGE_LISTENER_COUNT));
				for(int i = 0; i<changeListenerCount; i++)
					Main.model.addChangeListener(newChangeListener(true));
			}
			else if(request.getParameter(NUL_CHARACTER)!=null)
			{
				try(TransactionTry tx = Main.model.startTransactionTry("nulChar"))
				{
					new AnItem("\0start" , AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.red   );
					new AnItem("mid\0dle", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.green );
					new AnItem("end\0"   , AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.yellow);
					tx.commit();
				}
			}
			else if(request.getParameter(BREAK_MEDIA_HASH)!=null)
			{
				try(TransactionTry tx = Main.model.startTransactionTry("breakMediaHash"))
				{
					final UniqueHashedMedia feature = UniqueHashedMediaItem.feature;
					final MessageDigest digest = MessageDigestUtil.getInstance(feature.getMessageDigestAlgorithm());
					UniqueHashedMediaItem.TYPE.newItem(
							map(feature.getMedia(), Media.toValue(resource("test.png"), "image/png")),
							map(feature.getHash(), Hex.encodeLower(
											digest.digest(("" + nanoTime()).getBytes(US_ASCII)))));
					tx.commit();
				}
			}
			else if(request.getParameter(TYPE_FIELD_SUBMIT)!=null)
			{
				try(TransactionTry tx = Main.model.startTransactionTry("create type item"))
				{
					new TypeItem(
							replaceNullName(request.getParameter(TYPE_FIELD_FEATURE)),
							replaceNullName(request.getParameter(TYPE_FIELD_STRING)));
					tx.commit();
				}
			}
			else if(request.getParameter(FEATURE_FIELD_SUBMIT)!=null)
			{
				try(TransactionTry tx = Main.model.startTransactionTry("create feature"))
				{
					new FeatureItem(
						replaceNullName(request.getParameter(FEATURE_FIELD_FEATURE)),
						replaceNullName(request.getParameter(FEATURE_FIELD_STRING)));
					tx.commit();
				}
			}
		}

		final Out out = new Out(new PrintStream(response.getOutputStream(), false, UTF_8));
		Example_Jspm.write(out);
		out.close();
	}

	private void connect(final String name)
	{
		connectTokens.add(ConnectToken.issue(Main.model, name));
	}

	private void createSampleData()
	{
		final Class<?> thisClass = ExampleServlet.class;
		connect(thisClass.getName() + "#createSampleData");
		Main.model.createSchema();
		try(TransactionTry tx = Main.model.startTransactionTry(thisClass.getName() + "#createSampleData"))
		{
			new AnItem("aField1", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue35);
			new AnItem("aField2", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue35);
			new ASubItem("aField1s", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue35, "aSubField1s");
			new ASubItem("aField2s", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue35, "aSubField2s");
			new ASubItem("aField3s", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue35, "aSubField3s");
			new OptionalItem("mandatory", "optional", "optionalOk");
			new OptionalItem("mandatory", "optional", null);
			new StringLengthItem("empty", "emptyOk", "normal", "normalOk", "min10xxxxx", "min10Okxxx");
			new StringLengthItem("empty", "",        "normal", "x",        "min10xxxxx", "min10Okxxx");
			EnumSingletonComplete.createSampleData();
			EnumSingletonIncomplete.createSampleData();
			new AMediaItem();
			new AMediaItem().setContent(resource("test.png"), "image/png");
			new AMediaItem().setContent(resource("test.png"), "unknownma/unknownmi");
			new AMediaItem().setContent(resource("test.png"), "image/jpeg"); // wrong content type by intention
			new AMediaItem().setVault  (resource("test.png"), "image/png");
			new AMediaItem().setSecret (resource("test.png"), "image/png");
			new AMediaItem().setFinger (resource("test.png"), "image/png");
			{
				final AMediaItem item = new AMediaItem();
				item.setContent(resource("test.png"), "image/png");
				AMediaItem.content.getLastModified().set(item, new Date(requireNonNull(item.getContentLastModified()).getTime()-(1000l*60*60*7))); // 7 hours
			}
			{
				final AMediaItem item = new AMediaItem();
				item.setContent(resource("test.png"), "image/png");
				AMediaItem.content.getLastModified().set(item, new Date(requireNonNull(item.getContentLastModified()).getTime()-(1000l*60*60*24*91))); // 91 days
			}
			new AMediaItem().setName("someName");
			new AMediaItem().setName("someName error");
			new UniqueHashedMediaItem(Media.toValue(resource("test.png"), "image/png"));
			new TypeItem(AnItem.TYPE, AnItem.TYPE);
			new TypeItem(UniqueHashedMediaItem.TYPE, ASubItem.TYPE);
			new TypeItem((Type<?>)null, null);
			new FeatureItem(FeatureItem.intField1, FeatureItem.stringField1);
			new FeatureItem(FeatureItem.intField2, FeatureItem.stringField2);
			new FeatureItem((Feature)null, null);

			tx.commit();
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		Revisions.revisions(Main.model);
		{
			final Tags tags = Tags.of("model", Main.model.toString()).and("type", "ExampleDummyType");
			Counter.builder("com.exedio.cope.ItemCache.gets").tag("result", "hit").tags(tags).register(globalRegistry).increment(44.4);
			Counter.builder("com.exedio.cope.ItemCache.gets").tag("result", "miss").tags(tags).register(globalRegistry).increment(55);
			Counter.builder("com.exedio.cope.ItemCache.evictions").tags(tags).register(globalRegistry).increment(66);
			Counter.builder("com.exedio.cope.ItemCache.invalidations").tag("effect", "actual").tags(tags).register(globalRegistry).increment(77);
			// deliberately skip futile
			Counter.builder("com.exedio.cope.ItemCache.stamp.hit").tags(tags).register(globalRegistry).increment(88);
			Counter.builder("com.exedio.cope.ItemCache.stamp.purge").tags(tags).register(globalRegistry).increment(99);
			Counter.builder("com.exedio.cope.ItemCache.exampleMeter").tags(tags).register(globalRegistry).increment(99);
			Counter.builder("com.exedio.cope.ItemCache.exampleMeter2").tag("k", "v").tags(tags).register(globalRegistry).increment(99);
		}
		Counter.builder("com.exedio.cope.ItemCache.wrongModelCounter").
				tags("model", "wrongModel").
				tags("type", "wrongModelType").
				register(globalRegistry).increment(555);
		Counter.builder("com.exedio.cope.ItemCache.exampleMeterGlobal").
				tags("model", Main.model.toString()).
				register(globalRegistry).increment(777.777);
		Counter.builder("com.exedio.cope.ItemCache.exampleMeterGlobal2").
				tags("model", Main.model.toString()).
				tag("k", "v").
				description("Some useful description").
				register(globalRegistry).increment(888);
	}

	@SuppressWarnings("CodeBlock2Expr") // OK: explicit identity of hooks needed for testing duplicates
	private static void doTransaction(
			final String name,
			final int items,
			final long sleep,
			final int preCommitHooks,
			final int postCommitHooks,
			final boolean duplicateCommitHooks)
	{
		try(TransactionTry tx = Main.model.startTransactionTry(replaceNullName(name)))
		{
			for(int i = 0; i<items; i++)
				new AnItem(name + "#" + i, AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue35);

			final Runnable duplicatePre = () ->
			{
				System.out.println("PRE COMMIT HOOK DUPLICATE");
			};
			final Runnable duplicatePost = () ->
			{
				System.out.println("POST COMMIT HOOK DUPLICATE");
			};
			for(int i = 0; i<preCommitHooks; i++)
			{
				final int currentI = i;
				Main.model.addPreCommitHookIfAbsent(duplicateCommitHooks ? duplicatePre : () ->
				{
					System.out.println("PRE COMMIT HOOK " + currentI);
				});
			}
			for(int i = 0; i<postCommitHooks; i++)
			{
				final int currentI = i;
				Main.model.addPostCommitHookIfAbsent(duplicateCommitHooks ? duplicatePost : () ->
				{
					System.out.println("POST COMMIT HOOK " + currentI);
				});
			}

			if(sleep>0)
				Thread.sleep(sleep);
			tx.commit();
		}
		catch(final InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static void replaceItemCache()
	{
		try(TransactionTry tx = Main.model.startTransactionTry("ItemCache create"))
		{
			for(int i = 0; i<30000; i++)
				new AnItem("ItemCache#" + i, AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue35);
			tx.commit();
		}
		try(TransactionTry tx = Main.model.startTransactionTry("ItemCache read"))
		{
			for(final AnItem i : AnItem.TYPE.search())
				i.getAField();
			tx.commit();
		}
	}

	private static ChangeListener newChangeListener(final boolean toStringFails)
	{
		return new ChangeListener()
		{
			private final int count = changeListenerNumber.getAndIncrement();

			@Override
			public void onChange(final ChangeEvent event)
			{
				// do nothing
			}

			@Override
			public String toString()
			{
				if(toStringFails)
					throw new RuntimeException("Exception in toString of ChangeListener " + count);
				else
					return "toString of ChangeListener " + count;
			}
		};
	}

	private static String replaceNullName(final String name)
	{
		return "nullName".equals(name) ? null : name;
	}

	private static InputStream resource(final String name)
	{
		final InputStream result = ExampleServlet.class.getResourceAsStream(name);
		if(result==null)
			throw new IllegalArgumentException(name);
		return result;
	}

	@Override
	public void destroy()
	{
		for(final Iterator<ConnectToken> i = connectTokens.iterator(); i.hasNext(); )
		{
			//noinspection resource OK: ConnectTokens are registered for later return
			i.next().returnItConditionally();
			i.remove();
		}
		super.destroy();
	}


	private static final String[] ACCEPTED = {
			// Model.class and Sequence.class not needed, as there are gauges only, no counters or timers
			Transaction.class.getName() + '.',
			"com.exedio.cope.QueryCache.",
			"com.exedio.cope.ItemCache.",
			"com.exedio.cope.Cluster.",
			ChangeListener.class.getName() + '.',
			DataField.class.getName() + '.',
			"com.exedio.cope.pattern.MediaPath."};

	private static final SimpleMeterRegistry REGISTRY = new SimpleMeterRegistry();

	static
	{
		// Info api requires micrometer registry.
		// We must call Metrics.globalRegistry.add(MeterRegistry). If we don't,
		// counters exposed by info api will just stay at zero. This includes for
		// example:
		// - TransactionCounters#get*()
		// - DataFieldVaultInfo#get*Count()
		// - ChangeListenerInfo#getCleared(), #getRemoved(), #getFailed()
		// - ChangeListenerDispatcherInfo#getOverflow(), #getException()
		// - ItemCacheInfo#getHits(), #getMisses(), #getConcurrentLoads(), #getReplacementsL(),
		//     #getInvalidationsOrdered(), #getInvalidationsDone(), #getStampsHits(), #getStampsPurged()
		// - QueryCacheInfo#getHits(), #getMisses(), #getReplacements(), #getInvalidations(),
		//     #getConcurrentLoads(), #getStampsHits(), #getStampsPurged()
		// - ClusterSenderInfo#getInvalidationSplit()
		// - ClusterListenerInfo#getException(), #getMissingMagic(), #getWrongSecret(), #getFromMyself()

		REGISTRY.config().meterFilter(new MeterFilter()
		{
			@Override
			public MeterFilterReply accept(final Meter.Id id)
			{
				return isAccepted(id.getName())
						? MeterFilterReply.ACCEPT
						: MeterFilterReply.DENY;
			}

			private static boolean isAccepted(final String name)
			{
				for(final String s : ACCEPTED)
					if(name.startsWith(s))
						return true;
				return false;
			}
		});
		globalRegistry.add(REGISTRY);
	}
}
