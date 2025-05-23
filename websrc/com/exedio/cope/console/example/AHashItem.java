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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.Item;
import com.exedio.cope.misc.DigitPinValidator;
import com.exedio.cope.pattern.Hash;
import com.exedio.cope.pattern.MessageDigestHashAlgorithm;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // OK: for example TYPE
final class AHashItem extends Item
{
	static final Hash md8 = new Hash(MessageDigestHashAlgorithm.create(UTF_8, "SHA-512", 8, new SecureRandom(), 8));
	static final Hash md8x6latin = new Hash(MessageDigestHashAlgorithm.create(StandardCharsets.ISO_8859_1, "SHA-512", 8, new SecureRandom(), 80000));
	static final Hash md5 = new Hash(MessageDigestHashAlgorithm.create(UTF_8, "MD5", 2, new SecureRandom(), 5000));
	static final Hash deterministic = new Hash(MessageDigestHashAlgorithm.create(UTF_8, "MD5", 0, null, 1));
	static final Hash pin = new Hash(MessageDigestHashAlgorithm.create(UTF_8, "SHA-512", 8, new SecureRandom(), 2)).validate(new DigitPinValidator(4));

	/**
	 * Creates a new AHashItem with all the fields initially needed.
	 * @param md8 the initial value for field {@link #md8}.
	 * @param md8x6latin the initial value for field {@link #md8x6latin}.
	 * @param md5 the initial value for field {@link #md5}.
	 * @param deterministic the initial value for field {@link #deterministic}.
	 * @param pin the initial value for field {@link #pin}.
	 * @throws com.exedio.cope.MandatoryViolationException if md8, md8x6latin, md5, deterministic, pin is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantArrayCreation","RedundantSuppression"})
	AHashItem(
				@javax.annotation.Nonnull final java.lang.String md8,
				@javax.annotation.Nonnull final java.lang.String md8x6latin,
				@javax.annotation.Nonnull final java.lang.String md5,
				@javax.annotation.Nonnull final java.lang.String deterministic,
				@javax.annotation.Nonnull final java.lang.String pin)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(AHashItem.md8,md8),
			com.exedio.cope.SetValue.map(AHashItem.md8x6latin,md8x6latin),
			com.exedio.cope.SetValue.map(AHashItem.md5,md5),
			com.exedio.cope.SetValue.map(AHashItem.deterministic,deterministic),
			com.exedio.cope.SetValue.map(AHashItem.pin,pin),
		});
	}

	/**
	 * Creates a new AHashItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AHashItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #md8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean checkMd8(@javax.annotation.Nullable final java.lang.String md8)
	{
		return AHashItem.md8.check(this,md8);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkMd8} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	static final void blindMd8(@javax.annotation.Nullable final java.lang.String md8)
	{
		AHashItem.md8.blind(md8);
	}

	/**
	 * Sets a new value for {@link #md8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setMd8(@javax.annotation.Nonnull final java.lang.String md8)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AHashItem.md8.set(this,md8);
	}

	/**
	 * Returns the encoded hash value for hash {@link #md8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getSHA512s8i8")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getMd8SHA512s8i8()
	{
		return AHashItem.md8.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #md8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setSHA512s8i8")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setMd8SHA512s8i8(@javax.annotation.Nonnull final java.lang.String md8)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AHashItem.md8.setHash(this,md8);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #md8x6latin}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean checkMd8x6latin(@javax.annotation.Nullable final java.lang.String md8x6latin)
	{
		return AHashItem.md8x6latin.check(this,md8x6latin);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkMd8x6latin} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	static final void blindMd8x6latin(@javax.annotation.Nullable final java.lang.String md8x6latin)
	{
		AHashItem.md8x6latin.blind(md8x6latin);
	}

	/**
	 * Sets a new value for {@link #md8x6latin}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setMd8x6latin(@javax.annotation.Nonnull final java.lang.String md8x6latin)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AHashItem.md8x6latin.set(this,md8x6latin);
	}

	/**
	 * Returns the encoded hash value for hash {@link #md8x6latin}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getSHA512s8i80000")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getMd8x6latinSHA512s8i80000()
	{
		return AHashItem.md8x6latin.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #md8x6latin}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setSHA512s8i80000")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setMd8x6latinSHA512s8i80000(@javax.annotation.Nonnull final java.lang.String md8x6latin)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AHashItem.md8x6latin.setHash(this,md8x6latin);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #md5}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean checkMd5(@javax.annotation.Nullable final java.lang.String md5)
	{
		return AHashItem.md5.check(this,md5);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkMd5} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	static final void blindMd5(@javax.annotation.Nullable final java.lang.String md5)
	{
		AHashItem.md5.blind(md5);
	}

	/**
	 * Sets a new value for {@link #md5}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setMd5(@javax.annotation.Nonnull final java.lang.String md5)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AHashItem.md5.set(this,md5);
	}

	/**
	 * Returns the encoded hash value for hash {@link #md5}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMD5s2i5000")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getMd5MD5s2i5000()
	{
		return AHashItem.md5.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #md5}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMD5s2i5000")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setMd5MD5s2i5000(@javax.annotation.Nonnull final java.lang.String md5)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AHashItem.md5.setHash(this,md5);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #deterministic}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean checkDeterministic(@javax.annotation.Nullable final java.lang.String deterministic)
	{
		return AHashItem.deterministic.check(this,deterministic);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkDeterministic} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	static final void blindDeterministic(@javax.annotation.Nullable final java.lang.String deterministic)
	{
		AHashItem.deterministic.blind(deterministic);
	}

	/**
	 * Sets a new value for {@link #deterministic}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setDeterministic(@javax.annotation.Nonnull final java.lang.String deterministic)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AHashItem.deterministic.set(this,deterministic);
	}

	/**
	 * Returns the encoded hash value for hash {@link #deterministic}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMD5")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getDeterministicMD5()
	{
		return AHashItem.deterministic.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #deterministic}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMD5")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setDeterministicMD5(@javax.annotation.Nonnull final java.lang.String deterministic)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AHashItem.deterministic.setHash(this,deterministic);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #pin}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final boolean checkPin(@javax.annotation.Nullable final java.lang.String pin)
	{
		return AHashItem.pin.check(this,pin);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPin} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	static final void blindPin(@javax.annotation.Nullable final java.lang.String pin)
	{
		AHashItem.pin.blind(pin);
	}

	/**
	 * Sets a new value for {@link #pin}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setPin(@javax.annotation.Nonnull final java.lang.String pin)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AHashItem.pin.set(this,pin);
	}

	/**
	 * Returns the encoded hash value for hash {@link #pin}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getSHA512s8i2")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getPinSHA512s8i2()
	{
		return AHashItem.pin.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #pin}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setSHA512s8i2")
	@java.lang.SuppressWarnings({"FinalMethodInFinalClass","RedundantSuppression","UnnecessarilyQualifiedStaticUsage"})
	final void setPinSHA512s8i2(@javax.annotation.Nonnull final java.lang.String pin)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AHashItem.pin.setHash(this,pin);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for aHashItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AHashItem> TYPE = com.exedio.cope.TypesBound.newType(AHashItem.class,AHashItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AHashItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
