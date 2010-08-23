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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.misc.ServletUtil;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;

public final class InitServlet extends CopsServlet
{
	private static final long serialVersionUID = 1l;

	static final String CREATE_SAMPLE_DATA = "createSampleData";

	private ConnectToken connectToken = null;

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
			Main.model.startTransaction(thisClass.getName());
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

	@Override
	public void destroy()
	{
		connectToken.returnIt();
		connectToken = null;
		super.destroy();
	}
}
