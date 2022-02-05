/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import com.rezzedup.util.exceptional.tests.Testing;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

public class SneakyTests
{
    @Test
    public void cannotInstantiate()
    {
        Testing.assertPreventsInstantiation(Sneaky.class);
    }
    
    @Test
    public void alwaysThrowsExactException()
    {
        assertThrowsExactly(IOException.class, () -> { throw Sneaky.rethrow(new IOException()); });
        assertThrowsExactly(ParseException.class, () -> { throw Sneaky.rethrow(new ParseException("", 0)); });
        assertThrowsExactly(NoSuchMethodException.class, () -> { throw Sneaky.rethrow(new NoSuchMethodException()); });
    }
    
    @Test
    public void hasValidCatcher()
    {
        assertInstanceOf(Catcher.class, (Catcher<?>) Sneaky::catcher);
        assertInstanceOf(Catcher.class, Catcher.of(Sneaky::catcher));
    }
}
