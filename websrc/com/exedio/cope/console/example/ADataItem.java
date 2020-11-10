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

import com.exedio.cope.DataField;
import com.exedio.cope.Item;
import com.exedio.cope.Vault;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // OK: for example TYPE
final class ADataItem extends Item
{
	static final DataField blob1 = new DataField().optional();

	static final DataField blob2 = new DataField().optional();

	@Vault
	static final DataField vault1 = new DataField().optional();

	@Vault
	static final DataField vault2 = new DataField().optional();

	@Vault("other")
	static final DataField vaultOther = new DataField().optional();


	/**
	 * Creates a new ADataItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression","ZeroLengthArrayAllocation"})
	ADataItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new ADataItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private ADataItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns, whether there is no data for field {@link #blob1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isBlob1Null()
	{
		return ADataItem.blob1.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #blob1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getBlob1Length()
	{
		return ADataItem.blob1.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #blob1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final byte[] getBlob1Array()
	{
		return ADataItem.blob1.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getBlob1(final java.io.OutputStream blob1)
			throws
				java.io.IOException
	{
		ADataItem.blob1.get(this,blob1);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getBlob1(final java.nio.file.Path blob1)
			throws
				java.io.IOException
	{
		ADataItem.blob1.get(this,blob1);
	}

	/**
	 * Sets a new value for the persistent field {@link #blob1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setBlob1(final com.exedio.cope.DataField.Value blob1)
	{
		ADataItem.blob1.set(this,blob1);
	}

	/**
	 * Sets a new value for the persistent field {@link #blob1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setBlob1(final byte[] blob1)
	{
		ADataItem.blob1.set(this,blob1);
	}

	/**
	 * Sets a new value for the persistent field {@link #blob1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setBlob1(final java.io.InputStream blob1)
			throws
				java.io.IOException
	{
		ADataItem.blob1.set(this,blob1);
	}

	/**
	 * Sets a new value for the persistent field {@link #blob1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setBlob1(final java.nio.file.Path blob1)
			throws
				java.io.IOException
	{
		ADataItem.blob1.set(this,blob1);
	}

	/**
	 * Returns, whether there is no data for field {@link #blob2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isBlob2Null()
	{
		return ADataItem.blob2.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #blob2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getBlob2Length()
	{
		return ADataItem.blob2.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #blob2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final byte[] getBlob2Array()
	{
		return ADataItem.blob2.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getBlob2(final java.io.OutputStream blob2)
			throws
				java.io.IOException
	{
		ADataItem.blob2.get(this,blob2);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getBlob2(final java.nio.file.Path blob2)
			throws
				java.io.IOException
	{
		ADataItem.blob2.get(this,blob2);
	}

	/**
	 * Sets a new value for the persistent field {@link #blob2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setBlob2(final com.exedio.cope.DataField.Value blob2)
	{
		ADataItem.blob2.set(this,blob2);
	}

	/**
	 * Sets a new value for the persistent field {@link #blob2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setBlob2(final byte[] blob2)
	{
		ADataItem.blob2.set(this,blob2);
	}

	/**
	 * Sets a new value for the persistent field {@link #blob2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setBlob2(final java.io.InputStream blob2)
			throws
				java.io.IOException
	{
		ADataItem.blob2.set(this,blob2);
	}

	/**
	 * Sets a new value for the persistent field {@link #blob2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setBlob2(final java.nio.file.Path blob2)
			throws
				java.io.IOException
	{
		ADataItem.blob2.set(this,blob2);
	}

	/**
	 * Returns, whether there is no data for field {@link #vault1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isVault1Null()
	{
		return ADataItem.vault1.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #vault1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getVault1Length()
	{
		return ADataItem.vault1.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #vault1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final byte[] getVault1Array()
	{
		return ADataItem.vault1.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getVault1(final java.io.OutputStream vault1)
			throws
				java.io.IOException
	{
		ADataItem.vault1.get(this,vault1);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getVault1(final java.nio.file.Path vault1)
			throws
				java.io.IOException
	{
		ADataItem.vault1.get(this,vault1);
	}

	/**
	 * Sets a new value for the persistent field {@link #vault1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault1(final com.exedio.cope.DataField.Value vault1)
	{
		ADataItem.vault1.set(this,vault1);
	}

	/**
	 * Sets a new value for the persistent field {@link #vault1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault1(final byte[] vault1)
	{
		ADataItem.vault1.set(this,vault1);
	}

	/**
	 * Sets a new value for the persistent field {@link #vault1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault1(final java.io.InputStream vault1)
			throws
				java.io.IOException
	{
		ADataItem.vault1.set(this,vault1);
	}

	/**
	 * Sets a new value for the persistent field {@link #vault1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault1(final java.nio.file.Path vault1)
			throws
				java.io.IOException
	{
		ADataItem.vault1.set(this,vault1);
	}

	/**
	 * Returns, whether there is no data for field {@link #vault2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isVault2Null()
	{
		return ADataItem.vault2.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #vault2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getVault2Length()
	{
		return ADataItem.vault2.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #vault2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final byte[] getVault2Array()
	{
		return ADataItem.vault2.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getVault2(final java.io.OutputStream vault2)
			throws
				java.io.IOException
	{
		ADataItem.vault2.get(this,vault2);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getVault2(final java.nio.file.Path vault2)
			throws
				java.io.IOException
	{
		ADataItem.vault2.get(this,vault2);
	}

	/**
	 * Sets a new value for the persistent field {@link #vault2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault2(final com.exedio.cope.DataField.Value vault2)
	{
		ADataItem.vault2.set(this,vault2);
	}

	/**
	 * Sets a new value for the persistent field {@link #vault2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault2(final byte[] vault2)
	{
		ADataItem.vault2.set(this,vault2);
	}

	/**
	 * Sets a new value for the persistent field {@link #vault2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault2(final java.io.InputStream vault2)
			throws
				java.io.IOException
	{
		ADataItem.vault2.set(this,vault2);
	}

	/**
	 * Sets a new value for the persistent field {@link #vault2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVault2(final java.nio.file.Path vault2)
			throws
				java.io.IOException
	{
		ADataItem.vault2.set(this,vault2);
	}

	/**
	 * Returns, whether there is no data for field {@link #vaultOther}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean isVaultOtherNull()
	{
		return ADataItem.vaultOther.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #vaultOther}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final long getVaultOtherLength()
	{
		return ADataItem.vaultOther.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #vaultOther}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final byte[] getVaultOtherArray()
	{
		return ADataItem.vaultOther.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getVaultOther(final java.io.OutputStream vaultOther)
			throws
				java.io.IOException
	{
		ADataItem.vaultOther.get(this,vaultOther);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void getVaultOther(final java.nio.file.Path vaultOther)
			throws
				java.io.IOException
	{
		ADataItem.vaultOther.get(this,vaultOther);
	}

	/**
	 * Sets a new value for the persistent field {@link #vaultOther}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVaultOther(final com.exedio.cope.DataField.Value vaultOther)
	{
		ADataItem.vaultOther.set(this,vaultOther);
	}

	/**
	 * Sets a new value for the persistent field {@link #vaultOther}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVaultOther(final byte[] vaultOther)
	{
		ADataItem.vaultOther.set(this,vaultOther);
	}

	/**
	 * Sets a new value for the persistent field {@link #vaultOther}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVaultOther(final java.io.InputStream vaultOther)
			throws
				java.io.IOException
	{
		ADataItem.vaultOther.set(this,vaultOther);
	}

	/**
	 * Sets a new value for the persistent field {@link #vaultOther}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setVaultOther(final java.nio.file.Path vaultOther)
			throws
				java.io.IOException
	{
		ADataItem.vaultOther.set(this,vaultOther);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for aDataItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ADataItem> TYPE = com.exedio.cope.TypesBound.newType(ADataItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private ADataItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
