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
import com.exedio.cope.reflect.FeatureField;
import com.exedio.cope.reflect.TypeField;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;
import com.exedio.cops.Resource;
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
 * <p>
 * In order to use it, you have to deploy the servlet in your {@code web.xml},
 * providing the name of the cope model via an init-parameter.
 * Typically, your {@code web.xml} would contain a snippet like this:
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
@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
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
		return Collections.emptyList();
	}

	/**
	 * May be overridden by subclasses to specify which type fields are kept stable over model evolutions.
	 * Default returns true.
	 */
	protected boolean isStable(final TypeField<?> typeField)
	{
		return true;
	}

	/**
	 * May be overridden by subclasses to specify which feature fields are kept stable over model evolutions.
	 * Default returns true.
	 */
	protected boolean isStable(final FeatureField<?> featureField)
	{
		return true;
	}


	private static final long serialVersionUID = 1l;

	private Stores stores = null;
	private ConnectToken connectToken = null;
	private Model model = null;

	static final Resource stylesheet = new Resource("console.css");
	static final Resource propertiesScript = new Resource("properties.js");
	static final Resource schemaScript = new Resource("schema.js");
	static final Resource logo = new Resource("logo.png");
	static final Resource shortcutIcon = new Resource("shortcutIcon.png");

	static final Resource checkFalse = new Resource("checkfalse.png");
	static final Resource checkTrue  = new Resource("checktrue.png");

	static final Resource nodeFalse = new Resource("nodefalse.png");
	@SuppressWarnings("unused") // OK: url set by javascript
	static final Resource nodeTrue  = new Resource("nodetrue.png");

	static final Resource nodeWarningFalse = new Resource("nodewarningfalse.png");
	@SuppressWarnings("unused") // OK: url set by javascript
	static final Resource nodeWarningTrue  = new Resource("nodewarningtrue.png");

	static final Resource nodeErrorFalse = new Resource("nodeerrorfalse.png");
	@SuppressWarnings("unused") // OK: url set by javascript
	static final Resource nodeErrorTrue  = new Resource("nodeerrortrue.png");

	static final Resource nodeLeaf        = new Resource("nodeleaf.png");
	static final Resource nodeLeafWarning = new Resource("nodewarningleaf.png");
	static final Resource nodeLeafError   = new Resource("nodeerrorleaf.png");
	static final Resource ok      = new Resource("silk_accept.png");
	static final Resource okGrey  = new Resource("silk_accept_grey.png");
	static final Resource warning = new Resource("silk_error.png");
	static final Resource error   = new Resource("silk_exclamation.png");
	static final Resource unknown = new Resource("silk_help_yellow.png");
	static final Resource help    = new Resource("silk_help.png");
	static final Resource imagebackground = new Resource("imagebackground.png");
	static final Resource databaseEdit = new Resource("silk_database_edit.png");
	static final Resource databaseGear = new Resource("silk_database_gear.png");
	static final Resource helpOnDomReady = new Resource("helpOnDomReady.js");

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
			//noinspection ObjectToString OK: shows identity of servlet
			connectToken = ConnectToken.issue(model, "servlet \"" + getServletName() + "\" (" + this + ')');
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

			final String externalImgSrc = cop.getExternalImgSrc();
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy
			response.setHeader("Content-Security-Policy",
					"default-src 'none'; " +
					"style-src 'self'"  + (cop.requiresUnsafeInlineStyle ()?" 'unsafe-inline'":"") + "; " +
					"script-src 'self'" + (cop.requiresUnsafeInlineScript()?" 'unsafe-inline'":"") + "; " +
					"img-src 'self'" + (externalImgSrc!=null ? (" " + externalImgSrc) : "") + "; " +
					"connect-src 'self'; " +
					"frame-ancestors 'none'; " +
					"block-all-mixed-content; " +
					"base-uri 'none'");

			// Do not leak information to external servers, not even the (typically private) hostname.
			// We need the referer within the servlet, because typically there is a StrictRefererValidationFilter.
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Referrer-Policy
			response.setHeader("Referrer-Policy", "same-origin");

			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options
			response.setHeader("X-Content-Type-Options", "nosniff");

			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options
			response.setHeader("X-Frame-Options", "deny");

			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-XSS-Protection
			response.setHeader("X-XSS-Protection", "1; mode=block");

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
				catch(final UnknownHostException ignored)
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
