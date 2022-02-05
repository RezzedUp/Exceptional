/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RethrowTests
{
    @Test
    public void alwaysThrowsRethrowInstance()
    {
        Rethrow runtime = assertThrows(Rethrow.class, () -> { throw Rethrow.caught(new RuntimeException()); });
        Rethrow exception = assertThrows(Rethrow.class, () -> { throw Rethrow.caught(new Exception()); });
        Rethrow error = assertThrows(Rethrow.class, () -> { throw Rethrow.caught(new Error()); });
        
        // Rethrowing should never re-wrap an existing Rethrow instance; simply rethrow it as-is.
        assertSame(runtime, assertThrows(Rethrow.class, () -> { throw Rethrow.caught(runtime); }));
        assertSame(exception, assertThrows(Rethrow.class, () -> { throw Rethrow.caught(exception); }));
        assertSame(error, assertThrows(Rethrow.class, () -> { throw Rethrow.caught(error); }));
        
    }
    
    @SuppressWarnings({"ConstantConditions", "ThrowableNotThrown"})
    @Test
    public void neverContainsNullException()
    {
        assertThrows(NullPointerException.class, () -> new Rethrow(null));
        assertNotNull(new Rethrow(new Exception()).getCause());
    }
}
