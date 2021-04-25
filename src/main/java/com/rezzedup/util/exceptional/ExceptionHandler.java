/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

@FunctionalInterface
public interface ExceptionHandler<E extends Throwable>
{
    void handle(E exception);
    
    static <E extends Throwable> void print(E exception)
    {
        exception.printStackTrace();
    }
    
    static <E extends Throwable> void rethrow(E exception)
    {
        throw new Rethrow(exception);
    }
}
