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

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.misc.ServletUtil;
import java.io.File;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class PropertiesInitializer implements ServletContextListener
{
	@Override
	public void contextInitialized(final ServletContextEvent sce)
	{
		final ServletContext context = sce.getServletContext();
		ConnectToken.setProperties(Main.model,
				ServletUtil.getConnectProperties(context));
		ConnectToken.setProperties(Main.reducedModel,
				new ConnectProperties(
						new File(context.getRealPath("WEB-INF/cope-reduced.properties"))
				)
		);
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce)
	{
		ConnectToken.removePropertiesVoid(Main.model);
		ConnectToken.removePropertiesVoid(Main.reducedModel);
	}
}
