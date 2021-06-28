/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Rethrow;

import java.util.function.Consumer;

/**
 *
 * @param <T>   argument type
 * @param <E>   exception type
 *
 * @see Consumer
 */
@FunctionalInterface
public interface CheckedConsumer<T, E extends Throwable>
{
    static <T> Consumer<T> unchecked(CheckedConsumer<T, ? extends Exception> consumer)
    {
        return t ->
        {
            try { consumer.accept(t); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    void accept(T t) throws E;
}
