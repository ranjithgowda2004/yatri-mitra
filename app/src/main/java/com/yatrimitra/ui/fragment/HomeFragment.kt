package com.yatrimitra.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yatrimitra.R
import com.yatrimitra.YatriMitraApp
import com.yatrimitra.databinding.FragmentHomeBinding
import com.yatrimitra.ui.adapter.RouteAdapter
import com.yatrimitra.ui.adapter.StopAdapter
import com.yatrimitra.ui.viewmodel.MelaViewModel
import com.yatrimitra.ui.viewmodel.MelaViewModelFactory
import com.yatrimitra.util.SimulationEngine
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MelaViewModel by viewModels {
        MelaViewModelFactory((requireActivity().application as YatriMitraApp).database)
    }

    private lateinit var routeAdapter: RouteAdapter
    private lateinit var stopAdapter: StopAdapter
    private var isRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupControls()
        collectFlows()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapters() {
        routeAdapter = RouteAdapter { route ->
            viewModel.selectRoute(route.routeId)
            try {
                binding.routeView.routeColor =
                    android.graphics.Color.parseColor(route.colorHex)
            } catch (_: Exception) { }
        }
        binding.rvRoutes.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = routeAdapter
        }

        stopAdapter = StopAdapter { stop ->
            viewModel.selectStop(stop)
            binding.routeView.selectedStopId = stop.stopId
        }
        binding.rvStops.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stopAdapter
        }
    }

    private fun setupControls() {
        binding.btnStart.setOnClickListener {
            if (!isRunning) {
                viewModel.startSimulation()
                isRunning = true
                binding.btnStart.isEnabled = false
                binding.btnPause.isEnabled = true
            }
        }

        binding.btnPause.setOnClickListener {
            viewModel.pauseSimulation()
            isRunning = false
            binding.btnStart.isEnabled = true
            binding.btnPause.isEnabled = false
        }

        binding.btnAddVehicle.setOnClickListener {
            viewModel.addVehicle()
            Toast.makeText(requireContext(), "Vehicle added!", Toast.LENGTH_SHORT).show()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etRiderName.text?.toString() ?: ""
            viewModel.registerRider(name)
            binding.etRiderName.text?.clear()
            Toast.makeText(requireContext(), "Registered at stop!", Toast.LENGTH_SHORT).show()
        }

        binding.sliderSpeed.addOnChangeListener { _, value, _ ->
            viewModel.setSpeed(value)
            binding.tvSpeedVal.text = "${value.toInt()} km/h"
        }
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.routes.collect { routes ->
                        routeAdapter.submitList(routes)
                        binding.tvNoRoutes.isVisible = routes.isEmpty()
                        if (routes.isNotEmpty() && viewModel.selectedRouteId.value == null) {
                            viewModel.selectRoute(routes.first().routeId)
                        }
                    }
                }

                launch {
                    viewModel.stops.collect { stops ->
                        stopAdapter.submitList(stops)
                        binding.routeView.updateStops(stops)
                    }
                }

                launch {
                    viewModel.vehicleStates.collect { states ->
                        binding.routeView.updateVehicles(states)
                        binding.tvVehicleCount.text = "${states.size} vehicles"
                    }
                }

                launch {
                    viewModel.nearestEtaSeconds.collect { eta ->
                        if (eta != null) {
                            binding.tvEta.text = SimulationEngine.formatEta(eta)
                            binding.tvEtaLabel.text = "Next Auto ETA"
                            binding.tvEta.setTextColor(
                                when {
                                    eta <= 0f -> requireContext().getColor(R.color.green)
                                    eta < 60f -> requireContext().getColor(R.color.amber)
                                    else -> requireContext().getColor(R.color.saffron)
                                }
                            )
                        } else {
                            binding.tvEta.text = "--:--"
                            binding.tvEtaLabel.text = "Select a stop"
                            binding.tvEta.setTextColor(requireContext().getColor(R.color.text_dim))
                        }
                    }
                }

                launch {
                    viewModel.selectedStop.collect { stop ->
                        binding.tvSelectedStop.text = stop?.name ?: "None selected"
                        binding.routeView.selectedStopId = stop?.stopId
                    }
                }

                launch {
                    viewModel.stats.collect { stats ->
                        binding.tvStats.text =
                            "Routes: ${stats.routeCount}  ·  Vehicles: ${stats.vehicleCount}  ·  Arrivals: ${stats.arrivalCount}"
                    }
                }

                launch {
                    viewModel.arrivalEvents.collect { (vehicleId, stop) ->
                        binding.arrivalBanner.apply {
                            text = "🛺  $vehicleId arriving at ${stop.name}!"
                            isVisible = true
                            postDelayed({ isVisible = false }, 3500)
                        }
                    }
                }
            }
        }
    }
}
