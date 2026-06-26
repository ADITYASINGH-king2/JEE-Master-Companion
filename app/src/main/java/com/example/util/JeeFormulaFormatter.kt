package com.example.util

object JeeFormulaFormatter {
    fun formatSymbols(input: String): String {
        return input
            .replace("alpha", "α")
            .replace("beta", "β")
            .replace("theta", "θ")
            .replace("lambda", "λ")
            .replace("pi", "π")
            .replace("omega", "ω")
            .replace("mu", "μ")
            .replace("delta", "Δ")
            .replace("phi", "φ")
            .replace("infinity", "∞")
            .replace("^2", "²")
            .replace("^3", "³")
            .replace("sqrt", "√")
    }
}
