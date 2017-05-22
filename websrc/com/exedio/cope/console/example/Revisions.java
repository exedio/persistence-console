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
import com.exedio.cope.Revision;
import com.exedio.cope.RevisionInfo;
import com.exedio.cope.RevisionInfoCreate;
import com.exedio.cope.RevisionInfoMutex;
import com.exedio.cope.RevisionInfoRevise;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.misc.DirectRevisionsFactory;
import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Revisions
{
	private static final int END_OF_APPLICATION = 7;

	static com.exedio.cope.Revisions.Factory revisions(final int length)
	{
		final ArrayList<Revision> result = new ArrayList<>();

		int i = 0;
		int revision = length;
		result.add(
			new Revision(revision--,
					"not yet applied",
					"drop table \"Item\""));
		result.add(
			new Revision(revision--,
					"not yet applied2",
					"drop table \"Item\""));
		result.add(
			new Revision(revision--,
					"already applied together with its predecessor at the same time",
					"create table Mail( " +
					"this integer," +
					"created bigint, " +
					"body blob," +
					"toSend integer, " +
					"sentDate bigint," +
					"failedDate bigint, " +
					"exceptionStacktrace text character set utf8 binary, " +
					"constraint Mail_Pk primary key(this), " +
					"constraint Mail_this_CkPk check((this>=-2147483647) AND (this<=2147483647)), " +
					"constraint Mail_created_Ck check((created IS NOT NULL) AND ((created>=-9223372036854775808) AND (created<=9223372036854775807))), " +
					"constraint Mail_body_Ck check((LENGTH(body)<=100000) OR (body IS NULL)), " +
					"constraint Mail_toSend_Ck check((toSend IS NOT NULL) AND (toSend IN (0,1))), " +
					"constraint Mail_sentDate_Ck check(((sentDate>=-9223372036854775808) AND (sentDate<=9223372036854775807)) OR (sentDate IS NULL)), " +
					"constraint Mail_failedDate_Ck check(((failedDate>=-9223372036854775808) AND (failedDate<=9223372036854775807)) OR (failedDate IS NULL)), " +
					"constraint Mail_excepStack_Ck check((LENGTH(exceptionStacktrace)<=1500) OR (exceptionStacktrace IS NULL)))"));
		result.add(
			new Revision(revision--, "with two sql statements",
					"alter table Article add column imageContentType varchar(61) character set utf8 binary",
					"update Article set imageContentType='image/jpeg' where image is not null"));
		result.add(
			new Revision(revision--,
					"before change of environment",
					"drop table \"Item\""));

		for(; revision>=END_OF_APPLICATION; i++, revision--)
		{
			final String[] body = new String[(i%4) + 1];
			for(int j = 0; j<body.length; j++)
				body[j] = "sql " + revision + "/" + j;
			result.add(new Revision(revision, "comment " + revision, body));
		}
		return DirectRevisionsFactory.make(new com.exedio.cope.Revisions(result.toArray(new Revision[result.size()])));
	}

	@SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
	static void revisions(final Model model)
	{
		final java.util.Properties dbinfo = model.getEnvironmentInfo().asProperties();
		final HashMap<String, String> environment = new HashMap<>();
		for(final Map.Entry<Object, Object> entry : dbinfo.entrySet())
			environment.put((String)entry.getKey(), (String)entry.getValue());
		final String environmentKeyRemoved = "key.removed";
		environment.put(environmentKeyRemoved, "Removed value");

		final Iterator<Revision> revisions = model.getRevisions().getList().iterator();

		try(
			Connection con = SchemaInfo.newConnection(model);
			PreparedStatement stat = con.prepareStatement(
				"INSERT INTO " + q(model, "while") + " (" + q(model, "v") + "," + q(model, "i") + ") VALUES (?,?)"))
		{
			// skip first two revisions not yet applied
			revisions.next();
			revisions.next();

			Revision lastRevision = null;
			for(int i = 0; revisions.hasNext(); i++)
			{
				final Revision revision = revisions.next();
				lastRevision = revision;
				final ArrayList<RevisionInfoRevise.Body> body = new ArrayList<>();
				int j = 0;
				for(final String sql : revision.getBody())
				{
					body.add(new RevisionInfoRevise.Body(sql, (100*i)+j+1000, (100*i)+(10*j)+10000));
					j++;
				}
				save(stat, new RevisionInfoRevise(
						revision.getNumber(),
						"SAVEPOINT REVISE " + revision.getNumber() + " END",
						new Date(),
						environment,
						revision.getComment(),
						body.toArray(new RevisionInfoRevise.Body[body.size()])));

				if("before change of environment".equals(revision.getComment()))
				{
					environment.put("database.name",    environment.get("database.name")    + " - Changed");
					environment.put("database.version", environment.get("database.version") + " - Changed");
					environment.put("key.added", "Added value");
					environment.remove(environmentKeyRemoved);
				}
			}
			{
				save(stat, new RevisionInfoCreate(
						lastRevision.getNumber() - 1,
						new Date(),
						environment));
				for(int revisionNumber = lastRevision.getNumber() - 2; revisionNumber>0; revisionNumber--)
				{
					save(stat, new RevisionInfoRevise(
							revisionNumber,
							null, // savepoint
							new Date(),
							environment,
							"not in application " + revisionNumber,
							new RevisionInfoRevise.Body("sql 1", 12, 102),
							new RevisionInfoRevise.Body("sql 2", 13, 103)));
				}
			}
			{
				save(stat, new RevisionInfoMutex(
						"SAVEPOINT MUTEX",
						new Date(),
						environment,
						63, 60));
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, "create");
		}
	}

	@SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
	static void removeMutex(final Model model)
	{
		try(
			Connection con = SchemaInfo.newConnection(model);
			PreparedStatement stat = con.prepareStatement(
					"DELETE FROM " + q(model, "while") + " WHERE " + q(model, "v") + "=?"))
		{
			stat.setInt(1, -1);
			stat.execute();
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, "create");
		}
	}

	private static String q(final Model model, final String name)
	{
		return SchemaInfo.quoteName(model, name);
	}

	private static void save(final PreparedStatement stat, final RevisionInfo info) throws SQLException
	{
		stat.setInt(1, info.getNumber());
		stat.setBytes(2, info.toBytes());
		stat.execute();
	}

	private Revisions()
	{
		// prevent instantiation
	}
}
