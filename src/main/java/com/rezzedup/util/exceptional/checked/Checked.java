/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Catcher;

final class Checked
{
    private Checked() { throw new UnsupportedOperationException(); }
    
    static <O, I extends O> String implToString(Class<I> impl, O origin, Catcher<?> catcher)
    {
        return impl.getName() + "{origin=" + origin + ", catcher=" + catcher + "}";
    }
}
