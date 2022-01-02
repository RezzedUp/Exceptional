/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import com.rezzedup.util.exceptional.checked.CheckedRunnable;
import com.rezzedup.util.exceptional.checked.CheckedSupplier;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for handling exceptions.
 */
@FunctionalInterface
public interface Attempt
{
	static Attempt with(Catcher<? super Exception> catcher)
	{
		Objects.requireNonNull(catcher, "catcher");
		return () -> catcher;
	}
	
	static Attempt ignoring() { return () -> Catcher::ignore; }
	
	static Attempt printing() { return () -> Catcher::print; }
	
	static Attempt rethrowing() { return () -> Catcher::rethrow; }
	
	Catcher<? super Exception> catcher();
	
	default void run(CheckedRunnable<? extends Exception> runnable)
	{
		try { runnable.run(); }
		catch (Exception e) { catcher().accept(e); }
	}
	
	@SuppressWarnings("ConstantConditions")
	default <T> Optional<T> get(CheckedSupplier<@NullOr T, ? extends Exception> supplier)
	{
		try { return Optional.ofNullable(supplier.get()); }
		catch (Exception e) { catcher().accept(e); }
		return Optional.empty();
	}
}
