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

import static java.lang.Boolean.parseBoolean;

import com.exedio.cope.DataField;
import com.exedio.cope.DataFieldVaultInfo;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.DataFieldVaultSummary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

final class DataVaultCop extends ConsoleCop<Void>
{
	static final String TAB = "datavault";

	static final String SET_MARK_PUT_SUBMIT = "setMarkPut";
	static final String SET_MARK_PUT_BUCKET = "bucket";
	static final String SET_MARK_PUT_VALUE = "value";

	DataVaultCop(final Args args)
	{
		super(TAB, "Data Vaults", args);
	}

	@Override
	protected DataVaultCop newArgs(final Args args)
	{
		return new DataVaultCop(args);
	}

	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);
		if(isPost(request))
		{
			if(request.getParameter(SET_MARK_PUT_SUBMIT)!=null)
				model.setVaultRequiredToMarkPut(
						request.getParameter(SET_MARK_PUT_BUCKET),
						parseBoolean(request.getParameter(SET_MARK_PUT_VALUE)));
		}
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = out.model;
		if(!model.isConnected())
		{
			Console_Jspm.writeNotConnected(out, this);
			return;
		}

		final ArrayList<DataFieldVaultInfo> infos = new ArrayList<>();
		final LinkedHashMap<String, Bucket> vaults = new LinkedHashMap<>();
		for(final Type<?> type : model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof DataField)
				{
					final DataField field = (DataField)feature;
					final DataFieldVaultInfo info = field.getVaultInfo();
					if(info!=null)
						infos.add(info);
					final String key = info!=null ? info.getServiceKey() : null;
					vaults.computeIfAbsent(key, s -> new Bucket(info)).add(field);
				}

		DataVault_Jspm.writeBody(out, this, vaults.values(), infos,
				new DataFieldVaultSummary(infos.toArray(EMPTY_INFO)));
	}

	private static final DataFieldVaultInfo[] EMPTY_INFO = {};

	static final class Bucket
	{
		final boolean isBlob;
		final String bucket;
		final String service;
		final boolean markPut;
		private final ArrayList<DataField> fields = new ArrayList<>();
		private long maxSize = 0;

		Bucket(final DataFieldVaultInfo info)
		{
			if(info==null)
			{
				this.isBlob = true;
				this.bucket = null;
				this.service = null;
				this.markPut = false;
			}
			else
			{
				this.isBlob = false;
				this.bucket = info.getServiceKey();
				this.service = info.getService();
				this.markPut = info.getField().getType().getModel().isVaultRequiredToMarkPut(bucket);
			}
		}

		void add(final DataField field)
		{
			fields.add(field);
			final long size = field.getMaximumLength();
			if(maxSize<size)
				maxSize = size;
		}

		List<DataField> getFields()
		{
			return Collections.unmodifiableList(fields);
		}

		int getCount()
		{
			return fields.size();
		}

		long getMaxSize()
		{
			return maxSize;
		}
	}
}
