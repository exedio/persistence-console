/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.exedio.cope.Cope;
import com.exedio.cope.Model;

/**
 * Partial copy of {@link com.exedio.cope.misc.ServletUtil}.
 */
final class ServletUtilX
{
	private ServletUtilX()
	{
		// prevent instantiation
	}

	private interface Config
	{
		String getInitParameter(String name);
		String getName();
		ServletContext getServletContext();
		String getKind();
	}

	private static final Config wrap(final ServletConfig config)
	{
		return new Config()
		{
			public String getInitParameter(final String name)
			{
				return config.getInitParameter(name);
			}
			public String getName()
			{
				return config.getServletName();
			}
			public ServletContext getServletContext()
			{
				return config.getServletContext();
			}
			public String getKind()
			{
				return "servlet";
			}
		};
	}

	public static final Model getConnectedModel(final Servlet servlet)
	{
		return getModel(
				wrap(servlet.getServletConfig()),
				servlet);
	}

	private static final Model getModel(
					final Config config,
					final Object nameObject)
	{
		final String PARAMETER_MODEL = "model";
		final String initParam = config.getInitParameter(PARAMETER_MODEL);
		final String name = config.getName();
		final ServletContext context = config.getServletContext();

		final String description =
					config.getKind() + ' ' +
					'"' + name + '"' + ' ' +
					'(' + nameObject.getClass().getName() + '@' + System.identityHashCode(nameObject) + ')';
		//System.out.println("----------" + name + "---init-param---"+initParam+"---context-param---"+context.getInitParameter(PARAMETER_MODEL)+"---");
		final String modelName;
		final String modelNameSource;
		if(initParam==null)
		{
			final String contextParam = context.getInitParameter(PARAMETER_MODEL);
			if(contextParam==null)
				throw new IllegalArgumentException(description + ": neither init-param nor context-param '"+PARAMETER_MODEL+"' set");
			modelName = contextParam;
			modelNameSource = "context-param";
		}
		else
		{
			modelName = initParam;
			modelNameSource = "init-param";
		}

		final Model result;
		try
		{
			result = Cope.getModel(modelName);
		}
		catch(final IllegalArgumentException e)
		{
			throw new IllegalArgumentException(description + ", " + modelNameSource + ' ' + PARAMETER_MODEL + ':' + ' ' + e.getMessage(), e);
		}
		return result;
	}
}
