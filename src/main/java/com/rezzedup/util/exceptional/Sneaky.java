/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

public class Sneaky
{
	private Sneaky() { throw new UnsupportedOperationException(); }
	
	@SuppressWarnings("unchecked")
	private static <E extends Throwable> void sneak(Throwable exception) throws E { throw (E) exception; }
	
	public static RuntimeException smuggle(Throwable throwable)
	{
		sneak(throwable);
		throw new AssertionError();
	}
	
	public static <E extends Throwable> void rethrow(E exception) { throw smuggle(exception); }
}
