package com.srfmolina.krocy.ui.presentation.common.model

enum class DisplaySizeUi {
    S, M, L;

    /**
     * Whether the UI should use its compact (single-column, space-constrained) layout.
     * Single source of truth for the responsive breakpoint — change this when the breakpoint
     * model changes (e.g. M should behave like S) so every call site stays consistent.
     */
    val isCompact: Boolean
        get() = this == S
}