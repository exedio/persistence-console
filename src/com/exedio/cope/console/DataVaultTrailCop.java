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

import com.exedio.cope.DataField;
import com.exedio.cope.Feature;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

final class DataVaultTrailCop extends TestCop<DataField>
{
	static final String TAB = "datavaulttrail";
	private static final String BUCKET = "bucket";

	final String bucket;

	private DataVaultTrailCop(final Args args, final TestArgs testArgs, final String bucket)
	{
		super(TAB, "Data Vault Trail", args, testArgs);
		this.bucket = bucket;
		addParameter(BUCKET, bucket);
	}

	DataVaultTrailCop(final Args args, final TestArgs testArgs)
	{
		this(args, testArgs, null);
	}

	static DataVaultTrailCop getDataVaultTrailCop(
			final Args args,
			final TestArgs testArgs,
			final HttpServletRequest request)
	{
		return new DataVaultTrailCop(
				args,
				testArgs,
				request.getParameter(BUCKET));
	}

	@Override
	protected DataVaultTrailCop newArgs(final Args args)
	{
		return new DataVaultTrailCop(args, testArgs, bucket);
	}

	@Override
	protected DataVaultTrailCop newTestArgs(final TestArgs testArgs)
	{
		return new DataVaultTrailCop(args, testArgs, bucket);
	}

	DataVaultTrailCop toBucket(final String bucket)
	{
		return new DataVaultTrailCop(args, testArgs, bucket);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Runs DataField#checkVaultTrail.",
			"IMPACT: " +
				"A failure here may cause DataField#getLength to fail. " +
				"Furthermore, DataField#startsWithIfSupported may return wrong results. " +
				"DANGER ZONE."
		};
	}

	@Override
	void writeIntro(final Out out)
	{
		final LinkedHashSet<String> buckets = new LinkedHashSet<>();

		for(final Type<?> type : app.model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof DataField)
				{
					final String bucket = ((DataField)feature).getVaultServiceKey();
					if(bucket!=null)
						buckets.add(bucket);
				}

		if(buckets.size()>1)
			DataVaultTrail_Jspm.writeIntro(this, buckets, out);
	}

	@Override
	boolean requiresConnect()
	{
		// connect is required by DataField#getVaultBucket() in #getItems
		return true;
	}

	@Override
	List<DataField> getItems()
	{
		final ArrayList<DataField> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof final DataField field)
				{
					final String bucket = field.getVaultServiceKey();
					if(this.bucket!=null
						? this.bucket.equals(bucket)
						: bucket!=null)
						result.add(field);
				}

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Field", "Bucket"};
	}

	@Override
	void writeValue(final Out out, final DataField field, final int h)
	{
		switch(h)
		{
			case 0 -> out.write(field.getID());
			case 1 -> out.write(field.getVaultServiceKey());
			default ->
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final DataField field)
	{
		return field.getID();
	}

	@Override
	DataField forID(final String id)
	{
		return (DataField)app.model.getFeature(id);
	}

	@Override
	long check(final DataField field)
	{
		try(TransactionTry tx = app.model.startTransactionTry("Console DataVaultTrailCop " + id))
		{
			return tx.commit(
					field.checkVaultTrail());
		}
	}

	@Override
	String getViolationSql(final DataField field)
	{
		return SchemaInfo.checkVaultTrail(field);
	}
}
