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

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.ChangeEvent;
import com.exedio.cope.ChangeListener;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Transaction;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.util.ModificationListener;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;

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
	static final String TRANSACTION_SUBMIT = "transaction.submit";

	static final String ITEM_CACHE_REPLACE = "itemCache.replace";

	static final String CHANGE_LISTENER_ADD      = "changeListener.add";
	static final String CHANGE_LISTENER_ADD_FAIL = "changeListener.addFail";

	static final String MODIFICATION_LISTENER_ADD      = "modificationListener.add";
	static final String MODIFICATION_LISTENER_ADD_FAIL = "modificationListener.addFail";

	static final String FEATURE_FIELD_FEATURE = "featureField.feature";
	static final String FEATURE_FIELD_STRING  = "featureField.string";
	static final String FEATURE_FIELD_SUBMIT  = "featureField.submit";

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
						Integer.valueOf(request.getParameter(TRANSACTION_ITEMS)),
						Integer.valueOf(request.getParameter(TRANSACTION_SLEEP)));
			}
			else if(request.getParameter(ITEM_CACHE_REPLACE)!=null)
			{
				replaceItemCache();
			}
			else if(request.getParameter(CHANGE_LISTENER_ADD)!=null)
			{
				Main.model.addChangeListener(newChangeListener(false));
			}
			else if(request.getParameter(CHANGE_LISTENER_ADD_FAIL)!=null)
			{
				Main.model.addChangeListener(newChangeListener(true));
			}
			else if(request.getParameter(MODIFICATION_LISTENER_ADD)!=null)
			{
				Main.model.addModificationListener(newModificationListener(false));
			}
			else if(request.getParameter(MODIFICATION_LISTENER_ADD_FAIL)!=null)
			{
				Main.model.addModificationListener(newModificationListener(true));
			}
			else if(request.getParameter(FEATURE_FIELD_SUBMIT)!=null)
			{
				Main.model.startTransaction("create feature");
				try
				{
					new FeatureItem(
						replaceNullName(request.getParameter(FEATURE_FIELD_FEATURE)),
						replaceNullName(request.getParameter(FEATURE_FIELD_STRING)));
					Main.model.commit();
				}
				finally
				{
					Main.model.rollbackIfNotCommitted();
				}
			}
		}

		final Out out = new Out(new PrintStream(response.getOutputStream(), false, UTF8));
		Example_Jspm.write(out);
		out.close();
	}

	private void connect(final String name)
	{
		connectTokens.add(ConnectToken.issue(Main.model, name));
	}

	private void createSampleData()
	{
		final Class thisClass = ExampleServlet.class;
		connect(thisClass.getName() + "#createSampleData");
		Main.model.createSchema();
		try
		{
			Main.model.startTransaction(thisClass.getName() + "#createSampleData");
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
			new AMediaItem().setContent(thisClass.getResourceAsStream("test.png"), "image/png");
			new AMediaItem().setContent(thisClass.getResourceAsStream("test.png"), "unknownma/unknownmi");
			new AMediaItem().setContent(thisClass.getResourceAsStream("test.png"), "image/jpeg"); // wrong content type by intention
			new AMediaItem().setSecret (thisClass.getResourceAsStream("test.png"), "image/png");
			{
				final AMediaItem item = new AMediaItem();
				item.setContent(thisClass.getResourceAsStream("test.png"), "image/png");
				AMediaItem.content.getLastModified().set(item, new Date(item.getContentLastModified()-(1000l*60*60*7))); // 7 hours
			}
			{
				final AMediaItem item = new AMediaItem();
				item.setContent(thisClass.getResourceAsStream("test.png"), "image/png");
				AMediaItem.content.getLastModified().set(item, new Date(item.getContentLastModified()-(1000l*60*60*24*91))); // 91 days
			}
			new FeatureItem(FeatureItem.intField1, FeatureItem.stringField1);
			new FeatureItem(FeatureItem.intField2, FeatureItem.stringField2);
			new FeatureItem((Feature)null, null);

			Main.model.commit();
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			Main.model.rollbackIfNotCommitted();
		}
		Revisions.revisions(Main.model);
	}

	private static void doTransaction(final String name, final int items, final long sleep)
	{
		try
		{
			Main.model.startTransaction(replaceNullName(name));
			for(int i = 0; i<items; i++)
				new AnItem(name + "#" + i, AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue);
			if(sleep>0)
				Thread.sleep(sleep);
			Main.model.commit();
		}
		catch(final InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			Main.model.rollbackIfNotCommitted();
		}
	}

	private static void replaceItemCache()
	{
		try
		{
			Main.model.startTransaction("ItemCache create");
			for(int i = 0; i<30000; i++)
				new AnItem("ItemCache#" + i, AnItem.Letter.A, AnItem.Letter.A, AnItem.Color.blue);
			Main.model.commit();
			Main.model.startTransaction("ItemCache read");
			for(final AnItem i : AnItem.TYPE.search())
				i.getAField();
			Main.model.commit();
		}
		finally
		{
			Main.model.rollbackIfNotCommitted();
		}
	}

	private static ChangeListener newChangeListener(final boolean toStringFails)
	{
		return new ChangeListener()
		{
			private final int count = changeListenerNumber++;

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

	private static ModificationListener newModificationListener(final boolean toStringFails)
	{
		return new ModificationListener()
		{
			private final int count = modificationListenerNumber++;

			@Deprecated
			public void onModifyingCommit(final Collection<Item> modifiedItems, final Transaction transaction)
			{
				// do nothing
			}

			@Override
			public String toString()
			{
				if(toStringFails)
					throw new RuntimeException("Exception in toString of ModificationListener " + count);
				else
					return "toString of ModificationListener " + count;
			}
		};
	}

	private static String replaceNullName(final String name)
	{
		return "nullName".equals(name) ? null : name;
	}

	@Override
	public void destroy()
	{
		for(final Iterator<ConnectToken> i = connectTokens.iterator(); i.hasNext(); )
		{
			returnIfNeeded(i.next());
			i.remove();
		}
		super.destroy();
	}

	/**
	 * Not sure, whether this is a good idea.
	 */
	private void returnIfNeeded(final ConnectToken token)
	{
		if(!token.isReturned())
			token.returnIt();
	}
}
