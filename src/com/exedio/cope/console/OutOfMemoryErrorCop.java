/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.exedio.cope.console;

import com.exedio.cope.Model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OutOfMemoryErrorCop extends ConsoleCop<Void>
{
	static final String TAB = "outOfMemoryError";

	static final String LOG_OUT  = "logOut";
	static final String LOG_ERR  = "logErr";
	static final String THROW    = "throw";
	static final String ALLOCATE_SIZE   = "allocate.size";
	static final String ALLOCATE_NUMBER = "allocate.number";
	static final String ALLOCATE        = "allocate";
	static final String ALLOCATE_CLEAR  = "allocate.clear";

	OutOfMemoryErrorCop(final Args args)
	{
		super(TAB, "Out Of Memory Error", args);
	}

	static OutOfMemoryErrorCop getOutOfMemoryErrorCop(final Args args)
	{
		return new OutOfMemoryErrorCop(args);
	}

	@Override
	protected OutOfMemoryErrorCop newArgs(final Args args)
	{
		return new OutOfMemoryErrorCop(args);
	}

	private static final List<double[]> memoryLeak =
			Collections.synchronizedList(new ArrayList<>());

	@Override
	void doPost(final HttpServletRequest request, final Model model)
	{
		if(request.getParameter(LOG_OUT)!=null)
			System.out.println(OutOfMemoryError.class.getName());
		if(request.getParameter(LOG_ERR)!=null)
			System.err.println(OutOfMemoryError.class.getName());
		if(request.getParameter(THROW)!=null)
			throw new OutOfMemoryError();
		if(request.getParameter(ALLOCATE)!=null)
		{
			final int size   = getIntParameter(request, ALLOCATE_SIZE  , 0);
			final int number = getIntParameter(request, ALLOCATE_NUMBER, 0);
			logger.warn("Allocating double[{}] times {}.", size, number);
			for(int i = 0; i<number; i++)
				memoryLeak.add(new double[size]);
			logger.info("Allocated  double[{}] times {}.", size, number);
		}
		if(request.getParameter(ALLOCATE_CLEAR)!=null)
		{
			logger.info("Clearing {} entries.", memoryLeak.size());
			memoryLeak.clear();
			logger.info("Cleared.");
		}
	}

	@Override
	void writeBody(final Out out)
	{
		OutOfMemoryError_Jspm.writeBody(out, memoryLeak.size());
	}

	private static final Logger logger = LoggerFactory.getLogger(OutOfMemoryErrorCop.class);
}
