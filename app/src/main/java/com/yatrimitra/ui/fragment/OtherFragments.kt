package com.yatrimitra.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yatrimitra.YatriMitraApp
import com.yatrimitra.databinding.FragmentHistoryBinding
import com.yatrimitra.databinding.FragmentStatsBinding
import com.yatrimitra.ui.adapter.ArrivalAdapter
import com.yatrimitra.ui.viewmodel.MelaViewModel
import com.yatrimitra.ui.viewmodel.MelaViewModelFactory
import kotlinx.coroutines.launch

// ── HistoryFragment ───────────────────────────────────────────────────────────

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MelaViewModel by viewModels {
        MelaViewModelFactory((requireActivity().application as YatriMitraApp).database)
    }

    private val adapter = ArrivalAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvArrivals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.recentArrivals.collect { arrivals ->
                        adapter.submitList(arrivals)
                    }
                }
                launch {
                    viewModel.totalArrivals.collect { count ->
                        binding.tvTotalArrivals.text = "Total arrivals logged: $count"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ── StatsFragment ─────────────────────────────────────────────────────────────

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MelaViewModel by viewModels {
        MelaViewModelFactory((requireActivity().application as YatriMitraApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stats.collect { stats ->
                        binding.tvRouteCount.text = stats.routeCount.toString()
                        binding.tvVehicleCount.text = stats.vehicleCount.toString()
                        binding.tvArrivalCount.text = stats.arrivalCount.toString()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
