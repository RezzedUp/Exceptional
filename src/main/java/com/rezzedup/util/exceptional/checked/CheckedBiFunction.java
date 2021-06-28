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
import java.util.function.BiFunction;

/**
 *
 * @param <T>   first argument type
 * @param <U>   second argument type
 * @param <R>   return type
 * @param <E>   exception type
 *
 * @see BiFunction
 */
@FunctionalInterface
public interface CheckedBiFunction<T, U, R, E extends Throwable>
{
    static <T, U, R> BiFunction<T, U, R> unchecked(CheckedBiFunction<T, U, R, ? extends Exception> function)
    {
        Objects.requireNonNull(function, "function");
        
        return (t, u) ->
        {
            try { return function.accept(t, u); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    R accept(T t, U u) throws E;
}
