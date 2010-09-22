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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.DataField;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;

final class DataFieldCop extends ConsoleCop
{
	DataFieldCop(final Args args)
	{
		super(ConsoleCop.TAB_DATA_FIELD, "data fields", args);
	}

	@Override
	protected DataFieldCop newArgs(final Args args)
	{
		return new DataFieldCop(args);
	}

	@Override
	final void writeBody(
			final Out out,
			final Model model,
			final HttpServletRequest request,
			final History history)
	{
		final ArrayList<DataField> fields = new ArrayList<DataField>();
		long lengthMax = 0;

		for(final Type<?> type : model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof DataField)
				{
					final DataField field = (DataField)feature;
					fields.add(field);
					if(lengthMax<field.getMaximumLength())
						lengthMax = field.getMaximumLength();
				}

		DataField_Jspm.writeBody(out, fields, lengthMax);
	}
}
