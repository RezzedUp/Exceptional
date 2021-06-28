/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Rethrow;

/**
 * @param <E>   exception type
 *
 * @see Runnable
 */
@FunctionalInterface
public interface CheckedRunnable<E extends Throwable>
{
    static Runnable unchecked(CheckedRunnable<? extends Exception> runnable)
    {
        return () ->
        {
            try { runnable.run(); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    void run() throws E;
}
