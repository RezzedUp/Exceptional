/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import com.rezzedup.util.exceptional.checked.CheckedBiConsumer;
import com.rezzedup.util.exceptional.checked.CheckedBiFunction;
import com.rezzedup.util.exceptional.checked.CheckedConsumer;
import com.rezzedup.util.exceptional.checked.CheckedFunction;
import com.rezzedup.util.exceptional.checked.CheckedRunnable;
import com.rezzedup.util.exceptional.checked.CheckedSupplier;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CheckedInterfacesTests
{
    private static void testImplCatcherSwap(Catcher.Swap<Throwable, ?> origin)
    {
        Catcher<Throwable> catcher = Catcher::ignore;
        Catcher.Swap<Throwable, ?> swapped = origin.catcher(catcher);
        
        // Original catcher should obviously be an entirely different instance.
        assertNotSame(catcher, origin.catcher());
        
        // Catcher should be the same exact instance in the swapped version.
        assertSame(catcher, swapped.catcher());
        
        // Make sure that if the same catcher is received, the 'swappable' simply returns itself.
        assertSame(swapped, swapped.catcher(catcher));
        
        Catcher.Swap<Throwable, ?> again = swapped.catcher(Catcher::print);
        
        // Ensure that a new 'swappable' instance is returned when a different catcher is received, however.
        assertNotSame(swapped, again);
        
        String originToString = "origin=" + origin;
        
        // Both 'swapped' and 'again' should have the same origin
        assertTrue(swapped.toString().contains(originToString));
        assertTrue(again.toString().contains(originToString));
    }
    
    @Test
    public void testCheckedBiConsumer()
    {
        CheckedBiConsumer<String, String, IOException> consumer = (a, b) -> { throw new IOException(); };
        
        assertThrows(IOException.class, () -> consumer.acceptOrThrow("test", "test"));
        assertThrows(Rethrow.class, () -> consumer.accept("test", "test"));
        
        testImplCatcherSwap(consumer);
    }
    
    @Test
    public void testCheckedBiFunction()
    {
        CheckedBiFunction<String, String, String, IOException> function = (a, b) -> { throw new IOException(); };
        
        assertThrows(IOException.class, () -> function.applyOrThrow("test", "test"));
        assertThrows(Rethrow.class, () -> function.apply("test", "test"));
        
        testImplCatcherSwap(function);
    }
    
    @Test
    public void testCheckedConsumer()
    {
        CheckedConsumer<String, IOException> consumer = (a) -> { throw new IOException(); };
        
        assertThrows(IOException.class, () -> consumer.acceptOrThrow("test"));
        assertThrows(Rethrow.class, () -> consumer.accept("test"));
        
        testImplCatcherSwap(consumer);
    }
    
    @Test
    public void testCheckedFunction()
    {
        CheckedFunction<String, String, IOException> function = (a) -> { throw new IOException(); };
        
        assertThrows(IOException.class, () -> function.applyOrThrow("test"));
        assertThrows(Rethrow.class, () -> function.apply("test"));
        
        testImplCatcherSwap(function);
    }
    
    @Test
    public void testCheckedRunnable()
    {
        CheckedRunnable<IOException> runnable = () -> { throw new IOException(); };
        
        assertThrows(IOException.class, runnable::runOrThrow);
        assertThrows(Rethrow.class, runnable::run);
        
        testImplCatcherSwap(runnable);
    }
    
    @Test
    public void testCheckedSupplier()
    {
        CheckedSupplier<String, IOException> supplier = () -> { throw new IOException(); };
        
        assertThrows(IOException.class, supplier::getOrThrow);
        assertThrows(Rethrow.class, supplier::get);
        
        testImplCatcherSwap(supplier);
    }
}
