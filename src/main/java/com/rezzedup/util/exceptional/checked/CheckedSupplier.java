/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import java.util.function.Supplier;

/**
 * {@code Supplier} that can throw checked exceptions.
 *
 * @param <T>   return type
 * @param <E>   exception type
 *
 * @see Supplier
 */
@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable>
{
    /**
     * Gets a result.
     *
     * @return a result
     * @throws E a checked exception
     */
    T get() throws E;
}
