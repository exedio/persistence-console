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
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.ChangeEvent;
import com.exedio.cope.ChangeListener;
import com.exedio.cope.Item;
import com.exedio.cope.Transaction;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.misc.ServletUtil;
import com.exedio.cope.util.ModificationListener;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;

public final class InitServlet extends CopsServlet
{
	private static final long serialVersionUID = 1l;

	static final String CREATE_SAMPLE_DATA = "createSampleData";

	static final String TRANSACTION = "transaction";

	static final String CHANGE_LISTENER_ADD      = "changeListener.add";
	static final String CHANGE_LISTENER_ADD_FAIL = "changeListener.addFail";

	static final String MODIFICATION_LISTENER_ADD      = "modificationListener.add";
	static final String MODIFICATION_LISTENER_ADD_FAIL = "modificationListener.addFail";

	private ConnectToken connectToken = null;
	static int transactionNumber = 0;
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
			else if(request.getParameter(TRANSACTION)!=null)
			{
				try
				{
					final String name = InitServlet.class.getName() + "#transaction#" + (transactionNumber++);
					Main.model.startTransaction(name);
					new AnItem(name);
					Main.model.commit();
				}
				finally
				{
					Main.model.rollbackIfNotCommitted();
				}
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
		}

		final Out out = new Out(new PrintStream(response.getOutputStream(), false, UTF8));
		Init_Jspm.write(out);
		out.close();
	}

	@Override
	public void init() throws ServletException
	{
		super.init();

		final Class thisClass = InitServlet.class;
		connectToken = ServletUtil.connect(Main.model, getServletConfig(), thisClass.getName());
	}

	private void createSampleData()
	{
		final Class thisClass = InitServlet.class;
		Main.model.createSchema();
		try
		{
			Main.model.startTransaction(thisClass.getName() + "#createSampleData");
			new AnItem("aField1");
			new AnItem("aField2");
			new ASubItem("aField1s", "aSubField1s");
			new ASubItem("aField2s", "aSubField2s");
			new ASubItem("aField3s", "aSubField3s");
			new AMediaItem();
			new AMediaItem().setContent(thisClass.getResourceAsStream("test.png"), "image/png");
			new AMediaItem().setContent(thisClass.getResourceAsStream("test.png"), "unknownma/unknownmi");
			new AMediaItem().setContent(thisClass.getResourceAsStream("test.png"), "image/jpeg"); // wrong content type by intention
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

	@Override
	public void destroy()
	{
		connectToken.returnIt();
		connectToken = null;
		super.destroy();
	}
}
