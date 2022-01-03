/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import java.util.function.Function;

/**
 * {@code Function} that can throw checked exceptions.
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
    /**
     * Applies this function to the given argument.
     *
     * @param t     the function argument
     *
     * @return the function result
     * @throws E a checked exception
     */
    R apply(T t) throws E;
}
