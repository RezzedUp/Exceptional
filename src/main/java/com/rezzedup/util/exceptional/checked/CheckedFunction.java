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
import java.util.function.Function;

/**
 *
 * @param <T>   argument type
 * @param <R>   return type
 * @param <E>   exception type
 *
 * @see Function
 */
@FunctionalInterface
public interface CheckedFunction<T, R, E extends Throwable>
{
    static <T, R> Function<T, R> unchecked(CheckedFunction<T, R, ? extends Exception> function)
    {
        Objects.requireNonNull(function, "function");
        
        return t ->
        {
            try { return function.apply(t); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    R apply(T t) throws E;
}
