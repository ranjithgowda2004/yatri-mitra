package com.yatrimitra.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.yatrimitra.data.entity.StopEntity
import com.yatrimitra.util.SimulationEngine
import com.yatrimitra.util.VehicleState

/**
 * Custom Canvas View — draws the route line, stops, and moving auto-rickshaws.
 * Call [updateVehicles] and [updateStops] from the Fragment after StateFlow emits.
 */
class RouteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var routeColor: Int = Color.parseColor("#FF6B00")
        set(value) { field = value; invalidate() }

    var selectedStopId: String? = null
        set(value) { field = value; invalidate() }

    private var stops: List<StopEntity> = emptyList()
    private var vehicles: List<VehicleState> = emptyList()
    private val renderPositions = mutableMapOf<String, Float>()

    // ── Paints ────────────────────────────────────────────────────────────────

    private val routePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 8f
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 18f
    }

    private val stopFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#21262D")
    }

    private val stopBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.parseColor("#30363D")
    }

    private val selStopPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val glowRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8B949E")
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    private val selLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 26f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(100, 0, 0, 0)
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(25, 48, 54, 61)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private val speedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 22f
        typeface = Typeface.MONOSPACE
        textAlign = Paint.Align.CENTER
    }

    private val etaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 18f
        textAlign = Paint.Align.CENTER
        color = Color.argb(200, 255, 255, 255)
    }

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val wheelBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val PAD = 80f
    private val STOP_R = 12f
    private val SEL_R = 18f
    private val POLE_H = 60f
    private val LERP_T = 0.12f

    // ── Public API ────────────────────────────────────────────────────────────

    fun updateStops(newStops: List<StopEntity>) {
        stops = newStops
        invalidate()
    }

    fun updateVehicles(newStates: List<VehicleState>) {
        vehicles = newStates
        newStates.forEach { s ->
            val prev = renderPositions[s.vehicleId] ?: s.position
            renderPositions[s.vehicleId] = SimulationEngine.lerp(prev, s.position, LERP_T)
        }
        invalidate()
    }

    // ── Draw ──────────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val routeY = h / 2f

        drawGrid(canvas, w, h)
        drawRoute(canvas, w, routeY)
        drawStops(canvas, w, routeY)
        drawVehicles(canvas, w, routeY)
    }

    private fun drawGrid(canvas: Canvas, w: Float, h: Float) {
        var x = 0f; while (x <= w) { canvas.drawLine(x, 0f, x, h, gridPaint); x += 60f }
        var y = 0f; while (y <= h) { canvas.drawLine(0f, y, w, y, gridPaint); y += 60f }
    }

    private fun drawRoute(canvas: Canvas, w: Float, routeY: Float) {
        val r = Color.red(routeColor)
        val g = Color.green(routeColor)
        val b = Color.blue(routeColor)

        glowPaint.color = Color.argb(35, r, g, b)
        canvas.drawLine(PAD, routeY, w - PAD, routeY, glowPaint)

        val grad = LinearGradient(
            PAD, routeY, w - PAD, routeY,
            intArrayOf(Color.argb(60, r, g, b), routeColor, Color.argb(60, r, g, b)),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        routePaint.shader = grad
        canvas.drawLine(PAD, routeY, w - PAD, routeY, routePaint)
        routePaint.shader = null
    }

    private fun drawStops(canvas: Canvas, w: Float, routeY: Float) {
        val polePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        stops.forEach { stop ->
            val x = posToX(stop.position, w)
            val isSel = stop.stopId == selectedStopId

            polePaint.color = if (isSel) routeColor else Color.argb(80, 72, 79, 88)
            canvas.drawLine(x, routeY - POLE_H, x, routeY, polePaint)

            if (isSel) {
                glowRingPaint.color = Color.argb(80, Color.red(routeColor), Color.green(routeColor), Color.blue(routeColor))
                canvas.drawCircle(x, routeY, SEL_R + 10f, glowRingPaint)
                selStopPaint.color = routeColor
                canvas.drawCircle(x, routeY, SEL_R, selStopPaint)
                selLabelPaint.color = routeColor
                canvas.save()
                canvas.translate(x, routeY - POLE_H - 12f)
                canvas.rotate(-25f)
                canvas.drawText(stop.name, 0f, 0f, selLabelPaint)
                canvas.restore()
            } else {
                canvas.drawCircle(x, routeY, STOP_R, stopFillPaint)
                canvas.drawCircle(x, routeY, STOP_R, stopBorderPaint)
                canvas.save()
                canvas.translate(x, routeY - POLE_H - 12f)
                canvas.rotate(-25f)
                canvas.drawText(stop.name, 0f, 0f, labelPaint)
                canvas.restore()
            }
        }
    }

    private fun drawVehicles(canvas: Canvas, w: Float, routeY: Float) {
        vehicles.forEach { state ->
            val pos = renderPositions[state.vehicleId] ?: state.position
            val x = posToX(pos, w)
            val color = colorForVehicle(state.vehicleId)

            // Shadow
            canvas.drawOval(RectF(x - 18f, routeY + 10f, x + 18f, routeY + 20f), shadowPaint)

            // Rickshaw body
            bodyPaint.color = color
            canvas.drawRoundRect(RectF(x - 18f, routeY - 16f, x + 18f, routeY + 4f), 5f, 5f, bodyPaint)

            // Roof
            bodyPaint.color = darken(color, 0.75f)
            canvas.drawRoundRect(RectF(x - 13f, routeY - 26f, x + 13f, routeY - 14f), 4f, 4f, bodyPaint)

            // Windshield
            bodyPaint.color = Color.argb(120, 150, 220, 255)
            canvas.drawRect(x + 7f, routeY - 25f, x + 13f, routeY - 15f, bodyPaint)

            // Wheels
            bodyPaint.color = Color.parseColor("#161B22")
            canvas.drawCircle(x - 11f, routeY + 6f, 6f, bodyPaint)
            canvas.drawCircle(x + 11f, routeY + 6f, 6f, bodyPaint)
            wheelBorderPaint.color = color
            canvas.drawCircle(x - 11f, routeY + 6f, 6f, wheelBorderPaint)
            canvas.drawCircle(x + 11f, routeY + 6f, 6f, wheelBorderPaint)

            // Headlight
            bodyPaint.color = Color.parseColor("#FFD700")
            canvas.drawCircle(x + 18f, routeY - 6f, 3f, bodyPaint)

            // Speed label
            speedPaint.color = color
            canvas.drawText("${state.speedKmh.toInt()} km/h", x, routeY - 36f, speedPaint)

            // ETA label
            state.etaSeconds?.let { eta ->
                canvas.drawText("ETA ${SimulationEngine.formatEta(eta)}", x, routeY - 54f, etaPaint)
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun posToX(position: Float, viewWidth: Float): Float =
        PAD + position * (viewWidth - PAD * 2f)

    private fun colorForVehicle(vehicleId: String): Int {
        val palette = listOf("#FF6B00", "#00897B", "#7C4DFF", "#FFB300", "#F85149", "#58A6FF")
        val idx = ((vehicleId.hashCode() % palette.size) + palette.size) % palette.size
        return Color.parseColor(palette[idx])
    }

    private fun darken(color: Int, factor: Float): Int = Color.argb(
        Color.alpha(color),
        (Color.red(color) * factor).toInt().coerceIn(0, 255),
        (Color.green(color) * factor).toInt().coerceIn(0, 255),
        (Color.blue(color) * factor).toInt().coerceIn(0, 255)
    )
}
