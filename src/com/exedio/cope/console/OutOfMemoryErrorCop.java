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

package com.exedio.copedemo.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

final class OutOfMemoryErrorCop extends AdminCop
{
	static final String PATH_INFO = "outOfMemoryError.html";

	static final String LOG_OUT  = "logOut";
	static final String LOG_ERR  = "logErr";
	static final String THROW    = "throw";
	static final String ALLOCATE_SIZE   = "allocate.size";
	static final String ALLOCATE_NUMBER = "allocate.number";
	static final String ALLOCATE        = "allocate";
	static final String ALLOCATE_CLEAR  = "allocate.clear";

	OutOfMemoryErrorCop()
	{
		super(PATH_INFO);
	}

	private static final List<double[]> memoryLeak =
			Collections.synchronizedList(new ArrayList<>());

	@Override
	protected void post(
			final HttpServletRequest request)
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
			System.out.println("mxsampler: Allocating double[" + size + "] times " + number + '.');
			for(int i = 0; i<number; i++)
				memoryLeak.add(new double[size]);
			System.out.println("mxsampler: Allocated  double[" + size + "] times " + number + '.');
		}
		if(request.getParameter(ALLOCATE_CLEAR)!=null)
		{
			System.out.println("mxsampler: Clearing " + memoryLeak.size() + " entries.");
			memoryLeak.clear();
			System.out.println("mxsampler: Cleared.");
		}
	}

	@Override
	void writeBody(final Out out, final HttpServletRequest request)
	{
		OutOfMemoryError_Jspm.writeBody(out, memoryLeak.size());
	}
}
