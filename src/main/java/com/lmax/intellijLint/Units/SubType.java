/*
 *    Copyright 2017 LMAX Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lmax.intellijLint.Units;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.lmax.intellijLint.Units.ResolutionFailureReason.NONE;

public class SubType {
    private final @NotNull PsiElement element;
    private final @Nullable String subtypeFQN;
    private final boolean resolved;
    private final ResolutionFailureReason resolutionFailureReason;

    SubType(@NotNull PsiElement element, ResolutionFailureReason resolutionFailureReason)
    {
        this.element = element;
        subtypeFQN = null;
        resolved = false;
        this.resolutionFailureReason = resolutionFailureReason;
    }

    SubType(@NotNull PsiElement element)
    {
        this.element = element;
        subtypeFQN = null;
        resolved = true;
        resolutionFailureReason = NONE;
    }

    SubType(@NotNull PsiElement element, @Nullable String subtypeFQN, boolean resolved)
    {
        this.element = element;
        this.subtypeFQN = subtypeFQN;
        this.resolved = resolved;
        resolutionFailureReason = NONE;
    }

    public @Nullable String getSubtypeFQN()
    {
        return subtypeFQN;
    }
    public @NotNull PsiElement getPsiElement() {
        return element;
    }
    public boolean hasSubtype() { return resolved && subtypeFQN != null; }
    public ResolutionFailureReason getFailureReason() {
        return resolutionFailureReason;
    }
    public boolean isResolved() {
        return resolved;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubType))
        {
            return false;
        }

        final SubType other = (SubType) obj;

        if (!this.resolved || !other.resolved)
        {
            return false;
        }

        //noinspection SimplifiableIfStatement easier to read, imo.
        if (this.subtypeFQN == null && other.subtypeFQN == null)
        {
            return true;
        }

        return other.subtypeFQN != null && other.subtypeFQN.equals(this.subtypeFQN);
    }
}
