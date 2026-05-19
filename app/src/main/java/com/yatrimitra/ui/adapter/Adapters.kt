package com.yatrimitra.ui.adapter

import android.graphics.Color
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yatrimitra.data.entity.ArrivalEntity
import com.yatrimitra.data.entity.RouteEntity
import com.yatrimitra.data.entity.StopEntity
import com.yatrimitra.databinding.ItemArrivalBinding
import com.yatrimitra.databinding.ItemRouteBinding
import com.yatrimitra.databinding.ItemStopBinding
import java.text.SimpleDateFormat
import java.util.*

// ── RouteAdapter ──────────────────────────────────────────────────────────────

class RouteAdapter(
    private val onRouteClick: (RouteEntity) -> Unit
) : ListAdapter<RouteEntity, RouteAdapter.VH>(RouteDiff) {

    private var selectedId: String? = null

    inner class VH(val b: ItemRouteBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val route = getItem(position)
        with(holder.b) {
            tvRouteName.text = route.name
            tvRouteDescription.text = route.description
            tvRouteMeta.text = "${route.lengthKm} km"
            try {
                routeStripe.setBackgroundColor(Color.parseColor(route.colorHex))
            } catch (_: Exception) {
                routeStripe.setBackgroundColor(Color.parseColor("#FF6B00"))
            }
            root.alpha = if (route.routeId == selectedId) 1f else 0.7f
            root.setOnClickListener {
                selectedId = route.routeId
                notifyDataSetChanged()
                onRouteClick(route)
            }
        }
    }

    private object RouteDiff : DiffUtil.ItemCallback<RouteEntity>() {
        override fun areItemsTheSame(a: RouteEntity, b: RouteEntity) = a.routeId == b.routeId
        override fun areContentsTheSame(a: RouteEntity, b: RouteEntity) = a == b
    }
}

// ── StopAdapter ───────────────────────────────────────────────────────────────

class StopAdapter(
    private val onStopClick: (StopEntity) -> Unit
) : ListAdapter<StopEntity, StopAdapter.VH>(StopDiff) {

    private var selectedId: String? = null

    inner class VH(val b: ItemStopBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemStopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val stop = getItem(position)
        with(holder.b) {
            tvStopName.text = stop.name
            tvStopDist.text = "${"%.0f".format(stop.position * 100)}% along route"

            // Highlight selected stop dot
            val isSelected = stop.stopId == selectedId
            stopDot.setBackgroundResource(
                if (isSelected) com.yatrimitra.R.drawable.bg_stop_dot_selected
                else com.yatrimitra.R.drawable.bg_stop_dot
            )
            tvStopName.alpha = if (isSelected) 1f else 0.75f

            root.setOnClickListener {
                selectedId = stop.stopId
                notifyDataSetChanged()
                onStopClick(stop)
            }
        }
    }

    private object StopDiff : DiffUtil.ItemCallback<StopEntity>() {
        override fun areItemsTheSame(a: StopEntity, b: StopEntity) = a.stopId == b.stopId
        override fun areContentsTheSame(a: StopEntity, b: StopEntity) = a == b
    }
}

// ── ArrivalAdapter ────────────────────────────────────────────────────────────

class ArrivalAdapter : ListAdapter<ArrivalEntity, ArrivalAdapter.VH>(ArrivalDiff) {

    private val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    inner class VH(val b: ItemArrivalBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemArrivalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val a = getItem(position)
        with(holder.b) {
            tvVehicleId.text = a.vehicleId
            tvStopName.text = a.stopName
            tvTime.text = sdf.format(Date(a.arrivedAt))
        }
    }

    private object ArrivalDiff : DiffUtil.ItemCallback<ArrivalEntity>() {
        override fun areItemsTheSame(a: ArrivalEntity, b: ArrivalEntity) = a.id == b.id
        override fun areContentsTheSame(a: ArrivalEntity, b: ArrivalEntity) = a == b
    }
}
