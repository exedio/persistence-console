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

import static java.nio.charset.StandardCharsets.US_ASCII;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

final class ConnectCop extends ConsoleCop<Void>
{
	static final String TAB = "connect";

	ConnectCop(final Args args)
	{
		super(TAB, "Connect", args);
	}

	@Override
	protected ConnectCop newArgs(final Args args)
	{
		return new ConnectCop(args);
	}

	@Override boolean requiresUnsafeInlineStyle() { return true; }

	@Override boolean requiresUnsafeInlineScript() { return true; }

	@Override
	void writeHead(final Out out)
	{
		Properties_Jspm.writeHead(out);
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = app.model;
		final ConnectProperties props = model.getConnectProperties();
		final String source = props.getSource();
		String sourceContent = null;
		final File f = new File(source);
		try(InputStreamReader r = new InputStreamReader(new FileInputStream(f), US_ASCII))
		{
			final StringBuilder bf = new StringBuilder();

			final char[] b = new char[20*1024];
			for(int len = r.read(b); len>=0; len = r.read(b))
				bf.append(b, 0, len);

			sourceContent = XMLEncoder.encode(bf.toString());
			for(final Properties.Field<?> field : props.getFields())
			{
				if(field.hasHiddenValue())
				{
					final String key = field.getKey();
					sourceContent = sourceContent.replaceAll(key+".*", key+"=<i>hidden</i>");
				}
			}
		}
		catch(final FileNotFoundException ignored)
		{
			// sourceContent is still null
		}
		catch(final IOException e)
		{
			throw new RuntimeException(source, e);
		}

		Connect_Jspm.writeBody(
				out,
				props,
				model,
				sourceContent);
	}
}
