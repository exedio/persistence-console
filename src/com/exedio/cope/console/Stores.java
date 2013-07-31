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

import java.util.Date;
import java.util.HashMap;

final class Stores
{
	private final HashMap<Class<? extends ConsoleCop>, Store<?>> stores =
			new HashMap<Class<? extends ConsoleCop>, Store<?>>();

	static class Store<S>
	{
		final S value;
		private final long date;

		Store(final S value)
		{
			this.value = value;
			this.date = System.currentTimeMillis();
		}

		Date getDate()
		{
			return new Date(date);
		}
	}

	Store getStore(final Class<? extends ConsoleCop> clazz)
	{
		synchronized(stores)
		{
			return stores.get(clazz);
		}
	}

	void putStore(final Class<? extends ConsoleCop> clazz, final Object value)
	{
		final Store<?> store = new Store<Object>(value);
		synchronized(stores)
		{
			stores.put(clazz, store);
		}
	}
}
