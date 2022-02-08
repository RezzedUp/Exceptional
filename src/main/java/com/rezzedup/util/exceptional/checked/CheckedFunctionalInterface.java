/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Catcher;
import com.rezzedup.util.exceptional.CatcherSource;

public interface CheckedFunctionalInterface<T extends CheckedFunctionalInterface<T, E>, E extends Throwable>
    extends CatcherSource<Throwable>
{
    T catcher(Catcher<Throwable> catcher);
}
