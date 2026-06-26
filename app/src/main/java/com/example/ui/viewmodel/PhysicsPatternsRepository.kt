package com.example.ui.viewmodel

data class PhysicsPattern(
    val id: String,
    val title: String,
    val topic: String,
    val standardQuestion: String,
    val keyShortcut: String,
    val diagramUrl: String, // Decorative details
    val analysisSteps: List<String>
)

object PhysicsPatternsRepository {
    fun getPatterns(): List<PhysicsPattern> {
        return listOf(
            PhysicsPattern(
                id = "p1",
                title = "Rolling Without Slipping on Inclined Plane",
                topic = "Rotational Dynamics",
                standardQuestion = "A cylinder or sphere rolls down an incline of angle θ. Find its acceleration down the incline.",
                keyShortcut = "a = g sin(θ) / (1 + I / (m R²))",
                diagramUrl = "https://example.com/diag1.png",
                analysisSteps = listOf(
                    "Write force balance along incline: m g sin(θ) - f = m a",
                    "Write torque balance about center: f R = I α",
                    "Apply constraint for rolling without slipping: a = R α",
                    "Solve for a: a = g sin(θ) / (1 + β), where β = I / (m R²)"
                )
            ),
            PhysicsPattern(
                id = "p2",
                title = "Charged Particle entering Perpendicular Magnetic Field B",
                topic = "Electromagnetism",
                standardQuestion = "A particle with charge q, mass m enters perpendicular magnetic field B. Find radius and time period of helical/circular path.",
                keyShortcut = "r = m v / (q B), T = 2 π m / (q B)",
                diagramUrl = "https://example.com/diag2.png",
                analysisSteps = listOf(
                    "Magnetic force F = q (v × B) acts perpendicular to velocity vector.",
                    "This force provides the necessary centripetal force: q v B = m v² / r",
                    "Solve for radius: r = m v / (q B)",
                    "Find angular speed ω = v / r = q B / m",
                    "Calculate time period T = 2 π / ω = 2 π m / (q B)"
                )
            ),
            PhysicsPattern(
                id = "p3",
                title = "Block moving on a Movable Wedge",
                topic = "Newton's Laws & Momentum",
                standardQuestion = "A block of mass m releases from rest on smooth wedge of mass M with angle θ. Find acceleration of wedge.",
                keyShortcut = "a_wedge = (m g sinθ cosθ) / (M + m sin²θ)",
                diagramUrl = "https://example.com/diag3.png",
                analysisSteps = listOf(
                    "Define horizontal momentum conservation: P_x = constant (since smooth plane)",
                    "Write relative motion equations: block relative to wedge",
                    "Set up constraint equations along vertical and horizontal planes",
                    "Apply standard JEE shortcut formula to find wedge acceleration: a_wedge = m g sinθ cosθ / (M + m sin²θ)"
                )
            )
        )
    }
}
