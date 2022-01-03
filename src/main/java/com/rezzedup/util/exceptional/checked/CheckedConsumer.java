/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import java.util.function.Consumer;

/**
 * {@code Consumer} that can throw checked exceptions.
 *
 * @param <T>   argument type
 * @param <E>   exception type
 *
 * @see Consumer
 */
@FunctionalInterface
public interface CheckedConsumer<T, E extends Throwable>
{
    /**
     * Performs this operation on the given argument.
     *
     * @param t     the argument
     *
     * @throws E a checked exception
     */
    void accept(T t) throws E;
}
