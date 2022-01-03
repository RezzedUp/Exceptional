/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import java.util.function.BiConsumer;

/**
 * {@code BiConsumer} that can throw checked exceptions.
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
    /**
     * Performs this operation on the given arguments.
     *
     * @param t     the first input argument
     * @param u     the second input argument
     *
     * @throws E a checked exception
     */
    void accept(T t, U u) throws E;
}
