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

import static com.exedio.cope.console.Console_Jspm.writeJsComponentMountPoint;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Hash;
import com.exedio.cope.pattern.HashAlgorithm;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

final class HashCop extends ConsoleCop<Void>
{
	static final String TAB = "hash";

	HashCop(final Args args)
	{
		super(TAB, "Hashes", args);
	}

	@Override
	protected HashCop newArgs(final Args args)
	{
		return new HashCop(args);
	}

	@Override
	boolean hasJsComponent()
	{
		return true;
	}

	@Override
	void writeBody(final Out out)
	{
		writeJsComponentMountPoint(out, "hashes");
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	static List<HashesResponse> hashes(final Model model)
	{
		final ArrayList<HashesResponse> hashes = new ArrayList<>();
		for(final Type<?> type : model.getTypes())
			for(final Feature f : type.getDeclaredFeatures())
				if(f instanceof Hash)
					hashes.add(new HashesResponse((Hash)f));
		return hashes;
	}

	private record HashesResponse(
			String type,
			String name,
			int plainTextLimit,
			String plainTextValidator,
			String algorithmID,
			String algorithmDescription)
	{
		HashesResponse(final Hash hash)
		{
			this(
					hash.getType().getID(),
					hash.getName(),
					hash.getPlainTextLimit(),
					Optional.ofNullable(hash.getPlainTextValidator()).map(Object::toString).orElse(null),
					hash.getAlgorithm2().getID(),
					hash.getAlgorithm2().getDescription());
		}
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	static DoHashResponse doHash(final Model model, final DoHashRequest request) throws ApiTextException
	{
		final HashAlgorithm algorithm = request.get(model).getAlgorithm2();
		final String plainText = request.plainText;

		final long start = System.nanoTime();
		final String hashResult = algorithm.hash(plainText);
		final long end = System.nanoTime();

		return new DoHashResponse(hashResult, end - start);
	}

	record DoHashRequest(
			@JsonProperty(required=true) String type,
			@JsonProperty(required=true) String name,
			@JsonProperty(required=true) String plainText)
	{
		@JsonCreator
		DoHashRequest
		{ }

		@Nonnull
		Hash get(final Model model) throws ApiTextException
		{
			return Api.resolveFeature(model, type, name, Hash.class);
		}
	}

	private record DoHashResponse(
			String hash,
			long elapsedNanos)
	{ }
}
