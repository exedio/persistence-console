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
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;

final class VmCop extends ConsoleCop<Void>
{
	static final String TAB = "vm";

	private static final String DETAILED = "dt";
	private static final String ALL_PACKAGES = "ap";

	final boolean detailed;
	final boolean allPackages;

	VmCop(final Args args, final boolean detailed, final boolean allPackages)
	{
		super(TAB, "Java Packages", args);
		this.detailed = detailed;
		this.allPackages = allPackages;

		addParameter(DETAILED, detailed);
		addParameter(ALL_PACKAGES, allPackages);
	}

	static VmCop getVmCop(final Args args, final HttpServletRequest request)
	{
		return new VmCop(args, getBooleanParameter(request, DETAILED), getBooleanParameter(request, ALL_PACKAGES));
	}

	@Override
	protected VmCop newArgs(final Args args)
	{
		return new VmCop(args, detailed, allPackages);
	}

	VmCop toToggleDetailed()
	{
		return new VmCop(args, !detailed, allPackages);
	}

	VmCop toToggleAllPackages()
	{
		return new VmCop(args, detailed, !allPackages);
	}

	private static final Comparator<Package> COMPARATOR = Comparator.comparing(Package::getName);

	@Override
	void writeBody(final Out out)
	{
		final HashMap<String, TreeSet<Package>> jarMap = new HashMap<>();

		for(final Package pack : Package.getPackages())
		{
			if(!allPackages && !pack.getName().startsWith("com.exedio."))
				continue;

			if(pack.getSpecificationTitle()==null &&
				pack.getSpecificationVersion()==null &&
				pack.getSpecificationVendor()==null &&
				pack.getImplementationTitle()==null &&
				pack.getImplementationVersion()==null &&
				pack.getImplementationVendor()==null)
				continue;

			final String key =
				pack.getSpecificationTitle() + '|' +
				pack.getSpecificationVersion() + '|' +
				pack.getSpecificationVendor() + '|' +
				pack.getImplementationTitle() + '|' +
				pack.getImplementationVersion() + '|' +
				pack.getImplementationVendor();

			jarMap.computeIfAbsent(key, k -> new TreeSet<>(COMPARATOR)).add(pack);
		}

		Vm_Jspm.writeBody(out, this, new ArrayList<>(jarMap.values()));
	}
}
