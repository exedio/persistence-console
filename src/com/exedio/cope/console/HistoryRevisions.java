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

import com.exedio.cope.Revision;
import com.exedio.cope.Revisions;

/**
 * Currently works for MySQL only.
 */
final class HistoryRevisions
{
	static final Revisions REVISIONS =
		new Revisions(
			new Revision(3, "add HistoryPurge",
				"create table `HistoryPurge`(" +
				"`this` integer," +
				"`type` varchar(80) character set utf8 binary," +
				"`limit` bigint,`finished` bigint," +
				"`rows` integer," +
				"`elapsed` integer," +
				"constraint `HistoryPurge_Pk` primary key(`this`)," +
				"constraint `HistoryPurge_this_CkPk` check((`this`>=0) AND (`this`<=2147483647))," +
				"constraint `HistoryPurge_type_Ck` check((`type` IS NOT NULL) AND (CHAR_LENGTH(`type`)<=80))," +
				"constraint `HistoryPurge_limit_Ck` check((`limit` IS NOT NULL) AND ((`limit`>=-9223372036854775808) AND (`limit`<=9223372036854775807)))," +
				"constraint `HistoryPurge_finished_Ck` check((`finished` IS NOT NULL) AND ((`finished`>=-9223372036854775808) AND (`finished`<=9223372036854775807)))," +
				"constraint `HistoryPurge_rows_Ck` check((`rows` IS NOT NULL) AND ((`rows`>=0) AND (`rows`<=2147483647)))," +
				"constraint `HistoryPurge_elapsed_Ck` check((`elapsed` IS NOT NULL) AND ((`elapsed`>=0) AND (`elapsed`<=2147483647))))" +
				" engine=innodb"),
			new Revision(2, "add MediaInfo.get[RedirectFrom|Moved]",
				"alter table `HistoryModel`" +
					" add column `mediasRedirectFrom` integer," +
					" add column `mediasMoved` integer",
				"alter table `HistoryMedia`" +
					" add column `redirectFrom` integer," +
					" add column `moved` integer"),
			new Revision(1, "item cache renames \"cleanup\" to \"replacement\"",
				"alter table `HistoryModel`" +
					" change `connectPoolInvaliFromIdle` `connectioPoolInvalidOnGet` integer," +
					" change `connectPoolInvaliIntoIdle` `connectioPoolInvalidOnPut` integer," +
					" change `itemCacheNumberOfCleanups` `itemCacheReplacementRuns` integer," +
					" change `itemCacheItemsCleanedUp` `itemCacheReplacements` integer",
				"alter table `HistoryItemCache`" +
					" change `numberOfCleanups` `replacementRuns` integer," +
					" change `itemsCleanedUp` `replacements` integer," +
					" change `lastCleanup` `lastReplacementRun` bigint")
		);
	
	private HistoryRevisions()
	{
		// prevent instantiation
	}
}