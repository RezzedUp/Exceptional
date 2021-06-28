/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Rethrow;

import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * @param <T>   return type
 * @param <E>   exception type
 *
 * @see Supplier
 */
@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable>
{
    static <T> Supplier<T> unchecked(CheckedSupplier<T, ? extends Exception> supplier)
    {
        Objects.requireNonNull(supplier, "supplier");
        
        return () ->
        {
            try { return supplier.get(); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    T get() throws E;
}
