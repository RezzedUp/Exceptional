/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.unchecked;

/**
 * Represents an unchecked exception.
 *
 * @param <E>   checked exception type
 */
@SuppressWarnings("unused")
public interface Unchecked<E extends Throwable>
{
    /**
     * Gets the non-{@code null} formerly "checked"
     * exception as a raw {@code Throwable}.
     *
     * @return  the raw checked exception
     */
    Throwable getCause();
    
    /**
     * Gets the non-{@code null} formerly "checked"
     * exception as its proper type.
     *
     * @return  the checked exception
     *          cast to its proper type
     */
    @SuppressWarnings("unchecked")
    default E checked()
    {
        return (E) getCause();
    }
}
