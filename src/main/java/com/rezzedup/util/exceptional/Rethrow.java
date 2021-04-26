/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import java.util.Objects;

/**
 * Represents an existing exception thrown again.
 * It also guarantees that causal exceptions
 * cannot be {@code null}.
 */
public class Rethrow extends RuntimeException
{
    /**
     * Constructs.
     *
     * @param cause     an existing exception
     *
     * @throws NullPointerException if cause is {@code null}
     */
    public Rethrow(Throwable cause)
    {
        super(Objects.requireNonNull(cause, "cause"));
    }
    
    /**
     * Gets the non-{@code null} exception represented
     * by this rethrow.
     *
     * @return  the existing exception that
     *          this rethrow originates from
     */
    @Override // overridden to assert non-null return value
    public synchronized Throwable getCause()
    {
        return super.getCause();
    }
}
