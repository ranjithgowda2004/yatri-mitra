package com.yatrimitra.util

import org.junit.Assert.*
import org.junit.Test

class SimulationEngineTest {

    // ── lerp ──────────────────────────────────────────────────────────────────

    @Test fun `lerp at t=0 returns start`() =
        assertEquals(10f, SimulationEngine.lerp(10f, 20f, 0f), 0.001f)

    @Test fun `lerp at t=1 returns end`() =
        assertEquals(20f, SimulationEngine.lerp(10f, 20f, 1f), 0.001f)

    @Test fun `lerp at t=0_5 returns midpoint`() =
        assertEquals(15f, SimulationEngine.lerp(10f, 20f, 0.5f), 0.001f)

    @Test fun `lerp clamps below 0`() =
        assertEquals(10f, SimulationEngine.lerp(10f, 20f, -1f), 0.001f)

    @Test fun `lerp clamps above 1`() =
        assertEquals(20f, SimulationEngine.lerp(10f, 20f, 2f), 0.001f)

    // ── calculateEtaSeconds ───────────────────────────────────────────────────

    @Test fun `ETA is 0 when vehicle has passed stop`() =
        assertEquals(
            0f,
            SimulationEngine.calculateEtaSeconds(0.3f, 0.5f, 10f, 30f),
            0.001f
        )

    @Test fun `ETA formula half route at 30 kmh on 10 km = 600s`() =
        // remainingKm = (0.5 - 0.0) * 10 = 5 km; eta = 5/30 * 3600 = 600 s
        assertEquals(
            600f,
            SimulationEngine.calculateEtaSeconds(0.5f, 0.0f, 10f, 30f),
            1f
        )

    @Test fun `ETA is 0 when speed is 0`() =
        assertEquals(
            0f,
            SimulationEngine.calculateEtaSeconds(1.0f, 0.0f, 12f, 0f),
            0.001f
        )

    @Test fun `ETA is 0 when vehicle is at stop`() =
        assertEquals(
            0f,
            SimulationEngine.calculateEtaSeconds(0.5f, 0.5f, 12f, 30f),
            0.001f
        )

    // ── formatEta ─────────────────────────────────────────────────────────────

    @Test fun `formatEta returns HERE for 0`() =
        assertEquals("HERE", SimulationEngine.formatEta(0f))

    @Test fun `formatEta returns HERE for negative`() =
        assertEquals("HERE", SimulationEngine.formatEta(-5f))

    @Test fun `formatEta 90s = 1 colon 30`() =
        assertEquals("1:30", SimulationEngine.formatEta(90f))

    @Test fun `formatEta pads single digit seconds`() =
        assertEquals("1:05", SimulationEngine.formatEta(65f))

    @Test fun `formatEta 30s = 0 colon 30`() =
        assertEquals("0:30", SimulationEngine.formatEta(30f))

    // ── distanceKm ────────────────────────────────────────────────────────────

    @Test fun `distanceKm between two positions`() =
        assertEquals(4.0f, SimulationEngine.distanceKm(0.7f, 0.3f, 10f), 0.001f)

    @Test fun `distanceKm is symmetric`() {
        val d1 = SimulationEngine.distanceKm(0.2f, 0.8f, 10f)
        val d2 = SimulationEngine.distanceKm(0.8f, 0.2f, 10f)
        assertEquals(d1, d2, 0.001f)
    }
}
