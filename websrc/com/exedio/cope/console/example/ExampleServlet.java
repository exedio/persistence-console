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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.ChangeEvent;
import com.exedio.cope.ChangeListener;
import com.exedio.cope.Feature;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ExampleServlet extends CopsServlet
{
	private static final long serialVersionUID = 1l;

	static final String CREATE_SAMPLE_DATA = "createSampleData";
	static final String REMOVE_REVISION_MUTEX = "removeRevisionMutex";

	static final String CONNECT_NAME   = "connect.name";
	static final String CONNECT_COND   = "connect.conditional";
	static final String CONNECT_SUBMIT = "connect.submit";

	static final String TRANSACTION_NAME   = "transaction.name";
	static final String TRANSACTION_ITEMS  = "transaction.items";
	static final String TRANSACTION_SLEEP  = "transaction.sleep";
	static final String TRANSACTION_POSTHK = "transaction.postCommitHooks";
	static final String TRANSACTION_SUBMIT = "transaction.submit";

	static final String ITEM_CACHE_REPLACE = "itemCache.replace";

	static final String CHANGE_LISTENER_ADD      = "changeListener.add";
	static final String CHANGE_LISTENER_ADD_FAIL = "changeListener.addFail";
	static final String CHANGE_LISTENER_COUNT    = "changeListener.count";

	static final String NUL_CHARACTER    = "nulChar";

	static final String FEATURE_FIELD_FEATURE = "featureField.feature";
	static final String FEATURE_FIELD_STRING  = "featureField.string";
	static final String FEATURE_FIELD_SUBMIT  = "featureField.submit";

	@SuppressFBWarnings("SE_BAD_FIELD")
	private final ArrayList<ConnectToken> connectTokens = new ArrayList<ConnectToken>();
	static int changeListenerNumber = 0;
	static int modificationListenerNumber = 0;

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
						Integer.parseInt(request.getParameter(TRANSACTION_POSTHK)));
			}
			else if(request.getParameter(ITEM_CACHE_REPLACE)!=null)
			{
				replaceItemCache();
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

		final Out out = new Out(new PrintStream(response.getOutputStream(), false, UTF_8.name()));
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
			new AnItem("aField1", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue);
			new AnItem("aField2", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue);
			new ASubItem("aField1s", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue, "aSubField1s");
			new ASubItem("aField2s", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue, "aSubField2s");
			new ASubItem("aField3s", AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue, "aSubField3s");
			new OptionalItem("mandatory", "optional", "optionalOk");
			new OptionalItem("mandatory", "optional", null);
			new StringLengthItem("empty", "emptyOk", "normal", "normalOk", "min10xxxxx", "min10Okxxx");
			new StringLengthItem("empty", "",        "normal", "x",        "min10xxxxx", "min10Okxxx");
			new AMediaItem();
			new AMediaItem().setContent(resource("test.png"), "image/png");
			new AMediaItem().setContent(resource("test.png"), "unknownma/unknownmi");
			new AMediaItem().setContent(resource("test.png"), "image/jpeg"); // wrong content type by intention
			new AMediaItem().setSecret (resource("test.png"), "image/png");
			new AMediaItem().setFinger (resource("test.png"), "image/png");
			{
				final AMediaItem item = new AMediaItem();
				item.setContent(resource("test.png"), "image/png");
				AMediaItem.content.getLastModified().set(item, new Date(item.getContentLastModified().getTime()-(1000l*60*60*7))); // 7 hours
			}
			{
				final AMediaItem item = new AMediaItem();
				item.setContent(resource("test.png"), "image/png");
				AMediaItem.content.getLastModified().set(item, new Date(item.getContentLastModified().getTime()-(1000l*60*60*24*91))); // 91 days
			}
			new AMediaItem().setName("someName");
			new AMediaItem().setName("someName error");
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
	}

	private static void doTransaction(
			final String name,
			final int items,
			final long sleep,
			final int postCommitHooks)
	{
		try(TransactionTry tx = Main.model.startTransactionTry(replaceNullName(name)))
		{
			for(int i = 0; i<items; i++)
				new AnItem(name + "#" + i, AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue);

			for(int i = 0; i<postCommitHooks; i++)
			{
				final int currentI = i;
				Main.model.addPostCommitHook(() ->
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
				new AnItem("ItemCache#" + i, AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue);
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
			private final int count = changeListenerNumber++;

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
			i.next().returnItConditionally();
			i.remove();
		}
		super.destroy();
	}
}
