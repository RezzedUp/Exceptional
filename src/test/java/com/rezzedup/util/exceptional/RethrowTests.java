/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class RethrowTests
{
	@Test
	public void testRethrow()
	{
		assertThrows(Rethrow.class, () -> {
			throw Rethrow.caught(new IOException());
		});
	}
	
	@Test
	public void testSneaky()
	{
		assertThrows(IOException.class, () -> {
			throw Sneaky.smuggle(new IOException());
		});
	}
}
