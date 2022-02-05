/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.tests;

import org.opentest4j.AssertionFailedError;
import pl.tlinkowski.annotation.basic.NullOr;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

public class Testing
{
    private Testing() {}
    
    public static void assertPreventsInstantiation(Class<?> clazz)
    {
        try
        {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            
            try
            {
                constructor.newInstance();
            }
            catch (InvocationTargetException e)
            {
                @NullOr Throwable cause = e.getCause();
                assertNotNull(cause);
                assertSame(UnsupportedOperationException.class, cause.getClass());
            }
            catch (ReflectiveOperationException e)
            {
                throw new AssertionFailedError("Could not call no-arg constructor for class: " + clazz, e);
            }
        }
        catch (NoSuchMethodException e)
        {
            throw new AssertionFailedError("Class " + clazz + " does not have a no-arg constructor.");
        }
    }
}
