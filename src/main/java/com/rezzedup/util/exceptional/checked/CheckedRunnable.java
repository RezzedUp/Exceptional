/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

/**
 * {@code Runnable} that can throw checked exceptions.
 *
 * @param <E>   exception type
 *
 * @see Runnable
 */
@FunctionalInterface
public interface CheckedRunnable<E extends Throwable>
{
    /**
     * Runs.
     *
     * @throws E a checked exception
     */
    void run() throws E;
}
