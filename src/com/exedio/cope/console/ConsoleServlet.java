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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.Model;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;
import com.exedio.cops.Resource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The servlet providing the COPE Console application.
 *
 * In order to use it, you have to deploy the servlet in your <tt>web.xml</tt>,
 * providing the name of the cope model via an init-parameter.
 * Typically, your <tt>web.xml</tt> would contain a snippet like this:
 *
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;console&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.exedio.cope.console.ConsoleServlet&lt;/servlet-class&gt;
 *    &lt;init-param&gt;
 *       &lt;param-name&gt;model&lt;/param-name&gt;
 *       &lt;param-value&gt;{@link com.exedio.cope.Model com.bigbusiness.shop.Main#model}&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;console&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/console/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 *
 * @author Ralf Wiebicke
 */
public class ConsoleServlet extends CopsServlet
{
	/**
	 * May be overridden by subclasses to specify allowed prefixes of media urls.
	 * These prefixes will be prepended to results of {@link com.exedio.cope.pattern.MediaPath.Locator#getPath()}.
	 * The default implementation returns an empty list.
	 * @param request may be used for getting {@link HttpServletRequest#getContextPath()}.
	 */
	public List<String> getMediaURLPrefixes(final HttpServletRequest request)
	{
		return Collections.<String>emptyList();
	}


	private static final long serialVersionUID = 1l;

	@SuppressFBWarnings({"SE_BAD_FIELD","MSF_MUTABLE_SERVLET_FIELD","MTIA_SUSPECT_SERVLET_INSTANCE_FIELD"})
	private Stores stores = null;
	@SuppressFBWarnings({"SE_BAD_FIELD","MSF_MUTABLE_SERVLET_FIELD","MTIA_SUSPECT_SERVLET_INSTANCE_FIELD"})
	private ConnectToken connectToken = null;
	@SuppressFBWarnings({"MSF_MUTABLE_SERVLET_FIELD","MTIA_SUSPECT_SERVLET_INSTANCE_FIELD"})
	private Model model = null;

	static final Resource stylesheet = new Resource("console.css");
	static final Resource script     = new Resource("console.js");
	static final Resource schemaScript = new Resource("schema.js");
	static final Resource logo = new Resource("logo.png");
	static final Resource shortcutIcon = new Resource("shortcutIcon.png");
	static final Resource checkFalse = new Resource("checkfalse.png");
	static final Resource checkTrue  = new Resource("checktrue.png");
	static final Resource nodeFalse = new Resource("nodefalse.png");
	static final Resource nodeTrue  = new Resource("nodetrue.png");
	static final Resource nodeWarningFalse = new Resource("nodewarningfalse.png");
	static final Resource nodeWarningTrue  = new Resource("nodewarningtrue.png");
	static final Resource nodeErrorFalse = new Resource("nodeerrorfalse.png");
	static final Resource nodeErrorTrue  = new Resource("nodeerrortrue.png");
	static final Resource nodeLeaf        = new Resource("nodeleaf.png");
	static final Resource nodeLeafWarning = new Resource("nodewarningleaf.png");
	static final Resource nodeLeafError   = new Resource("nodeerrorleaf.png");
	static final Resource ok      = new Resource("silk_accept.png");
	static final Resource okGrey  = new Resource("silk_accept_grey.png");
	static final Resource warning = new Resource("silk_error.png");
	static final Resource error   = new Resource("silk_exclamation.png");
	static final Resource help    = new Resource("silk_help.png");
	static final Resource imagebackground = new Resource("imagebackground.png");

	@Override
	public final void init() throws ServletException
	{
		super.init();

		if(model!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}

		stores = new Stores();
		model = ServletUtilX.getConnectedModel(this);
	}

	@Override
	public final void destroy()
	{
		if(connectToken!=null)
		{
			connectToken.returnItConditionally();
			connectToken = null;
		}
		model = null;
		stores = null;
		super.destroy();
	}

	final void connect()
	{
		if(connectToken==null || connectToken.isReturned())
			connectToken = ConnectToken.issue(model, "servlet \"" + getServletName() + "\" (" + toString() + ')');
	}

	final boolean willBeReturned(final ConnectToken token)
	{
		return connectToken==token;
	}

	static final String CONNECT = "connectByToken";

	@Override
	protected final void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		final Model model;
		{
			{
				model = this.model;
			}

			final ConsoleCop<?> cop = ConsoleCop.getCop(stores, model, request, this);
			cop.initialize(request, model);
			response.setStatus(cop.getResponseStatus());
			final boolean ajax = Cop.isPost(request) && cop.isAjax(); // must use POST for security
			if(ajax)
				response.setContentType("text/xml; charset="+ UTF_8.name());
			final Out out = new Out(request, model, this, cop.args, new PrintStream(response.getOutputStream(), false, UTF_8.name()));
			if(ajax)
				cop.writeAjax(out);
			else
			{
				if(Cop.isPost(request) && request.getParameter(CONNECT)!=null)
					connect();

				final Principal principal = request.getUserPrincipal();
				final String authentication = principal!=null ? principal.getName() : null;
				String hostname = null;
				try
				{
					hostname = InetAddress.getLocalHost().getHostName();
				}
				catch(final UnknownHostException e)
				{
					// leave hostname==null
				}

				Console_Jspm.write(
						out, response, cop,
						this.model.toString(),
						this.model.getInitializeDate(),
						authentication, hostname
						);
			}
			out.close();
		}
	}
}
