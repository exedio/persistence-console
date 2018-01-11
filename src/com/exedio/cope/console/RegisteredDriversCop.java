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

import java.sql.Driver;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;

final class RegisteredDriversCop extends ConsoleCop<Void>
{
	static final String TAB_REGISTERED_DRIVERS = "registereddrivers";

	private static final String SHOW_DRIVERS = "sd";
	private static final String SHOW_SERVICES = "ss";
	private static final String SHOW_SERVICES_INSTALLED = "ssi";

	final boolean showDrivers;
	final boolean showServices;
	final boolean showServicesInstalled;

	RegisteredDriversCop(
			final Args args,
			final boolean showDrivers,
			final boolean showServices,
			final boolean showServicesInstalled)
	{
		super(TAB_REGISTERED_DRIVERS, "Registered Drivers", args);
		this.showDrivers = showDrivers;
		this.showServices = showServices;
		this.showServicesInstalled = showServicesInstalled;

		addParameter(SHOW_DRIVERS, showDrivers);
		addParameter(SHOW_SERVICES, showServices);
		addParameter(SHOW_SERVICES_INSTALLED, showServicesInstalled);
	}

	static RegisteredDriversCop getRegisteredDriversCop(final Args args, final HttpServletRequest request)
	{
		return new RegisteredDriversCop(args,
				getBooleanParameter(request, SHOW_DRIVERS),
				getBooleanParameter(request, SHOW_SERVICES),
				getBooleanParameter(request, SHOW_SERVICES_INSTALLED));
	}

	@Override
	protected RegisteredDriversCop newArgs(final Args args)
	{
		return new RegisteredDriversCop(args, showDrivers, showServices, showServicesInstalled);
	}

	RegisteredDriversCop showDrivers()
	{
		return new RegisteredDriversCop(args, true, showServices, showServicesInstalled);
	}

	RegisteredDriversCop showServices()
	{
		return new RegisteredDriversCop(args, showDrivers, true, showServicesInstalled);
	}

	RegisteredDriversCop showServicesInstalled()
	{
		return new RegisteredDriversCop(args, showDrivers, showServices, true);
	}

	@Override
	void writeBody(final Out out)
	{
		RegisteredDrivers_Jspm.writeBody(out, this);
	}

	static Iterator<Driver> it(final Enumeration<Driver> x)
	{
		return new Iterator<Driver>(){
			@Override
			public boolean hasNext()
			{
				return x.hasMoreElements();
			}
			@Override
			@SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException") // OK: Enumeration#nextElement does throw NoSuchElementException
			public Driver next()
			{
				return x.nextElement();
			}
		};
	}
}
