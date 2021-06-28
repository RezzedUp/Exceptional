/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Rethrow;

import java.util.function.BiConsumer;

/**
 *
 * @param <T>   first argument type
 * @param <U>   second argument type
 * @param <E>   exception type
 *
 * @see BiConsumer
 */
@FunctionalInterface
public interface CheckedBiConsumer<T, U, E extends Throwable>
{
    static <T, U> BiConsumer<T, U> unchecked(CheckedBiConsumer<T, U, ? extends Exception> consumer)
    {
        return (t, u) ->
        {
            try { consumer.accept(t, u); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    void accept(T t, U u) throws E;
}
