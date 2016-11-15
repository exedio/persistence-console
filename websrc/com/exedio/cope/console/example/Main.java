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

import com.exedio.cope.Model;
import com.exedio.cope.revstat.RevisionStatistics;

public final class Main
{
	public static final Model model =
		Model.builder()
			.add(Revisions.revisions(64))
			.add(
				RevisionStatistics.types
			)
			.add(
				AnItem.TYPE,
				ASubItem.TYPE,
				LongName1.TYPE,
				LongName2.TYPE,
				LongName3.TYPE,
				LongName4.TYPE,
				LongName5.TYPE,
				OptionalItem.TYPE,
				OptionalNoneItem.TYPE,
				StringLengthItem.TYPE,
				AHashItem.TYPE,
				CopyItem.TYPE,
				CopyOriginItem.TYPE,
				AMediaItem.TYPE,
				AMediaSingleItem.TYPE,
				FeatureItem.TYPE,
				SequenceItem.TYPE
			)
			.build();

	static
	{
		model.enableSerialization(Main.class, "model");
	}

	public static final Model reducedModel =
		new Model(
				AReducedItem.TYPE
		);

	static
	{
		reducedModel.enableSerialization(Main.class, "reducedModel");
	}
}
