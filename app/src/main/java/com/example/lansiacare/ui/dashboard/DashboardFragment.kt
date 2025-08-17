package com.example.lansiacare.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lansiacare.R // ✅ GUNAKAN BARIS INI
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.*
import com.example.lansiacare.data.database.AppDatabase
import com.example.lansiacare.data.entities.Medication
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.utils.ViewModelFactory
import com.example.lansiacare.data.repository.HealthRecordRepository
import com.example.lansiacare.data.repository.MedicationRepository
import com.example.lansiacare.data.repository.MedicalNoteRepository
import com.example.lansiacare.data.repository.UserRepository
import com.example.lansiacare.databinding.FragmentDashboardBinding
import com.example.lansiacare.ui.dashboard.TodayMedicationAdapter
import com.example.lansiacare.utils.Constants

class DashboardFragment : Fragment() {

    internal var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var medicationAdapter: TodayMedicationAdapter
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        updateGreeting()
        loadDummyData() // Untuk demo
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val userPreferences = UserPreferences(requireContext())

        viewModelFactory = ViewModelFactory(
            UserRepository(database.userDao()),
            MedicationRepository(database.medicationDao()),
            HealthRecordRepository(database.healthRecordDao()),
            MedicalNoteRepository(database.medicalNoteDao()),
            userPreferences
        )

        dashboardViewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]
    }

    private fun setupRecyclerView() {
        medicationAdapter = TodayMedicationAdapter { medication ->
            // Handle medication taken action
            // TODO: Update medication status
        }

        binding.rvTodayMedications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = medicationAdapter
        }
    }

    private fun setupObservers() {
        // Observe blood pressure data
        dashboardViewModel.bloodPressureRecords.observe(viewLifecycleOwner) { records ->
            if (records.isNotEmpty()) {
                binding.tvBloodPressureValue.text = records[0].value
            }
        }

        // Observe heart rate data
        dashboardViewModel.heartRateRecords.observe(viewLifecycleOwner) { records ->
            if (records.isNotEmpty()) {
                binding.tvHeartRateValue.text = records[0].value
            }
        }

        // Observe blood sugar data
        dashboardViewModel.bloodSugarRecords.observe(viewLifecycleOwner) { records ->
            if (records.isNotEmpty()) {
                binding.tvBloodSugarValue.text = records[0].value
            }
        }

        // Observe daily steps data
        dashboardViewModel.stepsRecords.observe(viewLifecycleOwner) { records ->
            if (records.isNotEmpty()) {
                binding.tvDailyStepsValue.text = formatSteps(records[0].value.toIntOrNull() ?: 0)
            }
        }

        // Observe today's medications
        dashboardViewModel.activeMedications.observe(viewLifecycleOwner) { medications ->
            val todayMedications = filterTodayMedications(medications)
            medicationAdapter.submitList(todayMedications)

            binding.tvNoMedications.visibility = if (todayMedications.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        // Set user name
        binding.tvUserName.text = dashboardViewModel.getUserName()
    }

    private fun setupClickListeners() {
        // Health data cards click listeners
        binding.cardBloodPressure.setOnClickListener {
            showAddHealthRecordDialog(Constants.RECORD_TYPE_BLOOD_PRESSURE)
        }

        binding.cardHeartRate.setOnClickListener {
            showAddHealthRecordDialog(Constants.RECORD_TYPE_HEART_RATE)
        }

        binding.cardBloodSugar.setOnClickListener {
            showAddHealthRecordDialog(Constants.RECORD_TYPE_BLOOD_SUGAR)
        }

        binding.cardDailySteps.setOnClickListener {
            showAddHealthRecordDialog(Constants.RECORD_TYPE_STEPS)
        }

        // Action buttons
        binding.btnAddHealthRecord.setOnClickListener {
            showHealthRecordSelectionDialog()
        }

        binding.btnViewMedications.setOnClickListener {
            findNavController().navigate(R.id.navigation_medication)
        }

        binding.tvViewAllMedications.setOnClickListener {
            findNavController().navigate(R.id.navigation_medication)
        }

        // Emergency button
        binding.fabEmergency.setOnClickListener {
            findNavController().navigate(R.id.navigation_emergency)
        }
    }

    private fun updateGreeting() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when (hourOfDay) {
            in 5..11 -> getString(R.string.good_morning)
            in 12..17 -> getString(R.string.good_afternoon)
            else -> getString(R.string.good_evening)
        }

        binding.tvGreeting.text = greeting
    }

    private fun filterTodayMedications(medications: List<Medication>): List<Medication> {
        // Filter medications that should be taken today
        // This is a simplified version - in real app, check actual schedule
        return medications.filter { it.isActive }.take(3) // Show max 3 for demo
    }

    private fun formatSteps(steps: Int): String {
        return String.format("%,d", steps)
    }

    private fun showAddHealthRecordDialog(recordType: String) {
        AddHealthRecordDialog.newInstance(recordType) { value, notes ->
            val unit = when (recordType) {
                Constants.RECORD_TYPE_BLOOD_PRESSURE -> "mmHg"
                Constants.RECORD_TYPE_HEART_RATE -> "bpm"
                Constants.RECORD_TYPE_BLOOD_SUGAR -> "mg/dL"
                Constants.RECORD_TYPE_STEPS -> "steps"
                else -> ""
            }
            dashboardViewModel.addHealthRecord(recordType, value, unit, notes)
        }.show(childFragmentManager, "AddHealthRecord")
    }

    private fun showHealthRecordSelectionDialog() {
        HealthRecordSelectionDialog.newInstance { recordType ->
            showAddHealthRecordDialog(recordType)
        }.show(childFragmentManager, "HealthRecordSelection")
    }

    private fun loadDummyData() {
        // Load some dummy data for demonstration
        dashboardViewModel.addHealthRecord(Constants.RECORD_TYPE_BLOOD_PRESSURE, "120/80", "mmHg", "Normal reading")
        dashboardViewModel.addHealthRecord(Constants.RECORD_TYPE_HEART_RATE, "72", "bpm", "Resting heart rate")
        dashboardViewModel.addHealthRecord(Constants.RECORD_TYPE_BLOOD_SUGAR, "95", "mg/dL", "Fasting glucose")
        dashboardViewModel.addHealthRecord(Constants.RECORD_TYPE_STEPS, "5240", "steps", "Morning walk")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}