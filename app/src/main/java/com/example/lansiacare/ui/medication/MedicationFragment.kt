package com.example.lansiacare.ui.medication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.lansiacare.utils.ViewModelFactory
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.data.database.AppDatabase
import com.example.lansiacare.data.entities.Medication
import com.example.lansiacare.data.repository.MedicalNoteRepository
import com.example.lansiacare.data.repository.UserRepository
import com.example.lansiacare.data.repository.MedicationRepository
import com.example.lansiacare.data.repository.HealthRecordRepository
import com.example.lansiacare.databinding.FragmentMedicationBinding




class MedicationFragment : Fragment() {

    private var _binding: FragmentMedicationBinding? = null
    private val binding get() = _binding!!

    private lateinit var medicationViewModel: MedicationViewModel
    private lateinit var medicationAdapter: MedicationAdapter
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
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

        medicationViewModel = ViewModelProvider(this, viewModelFactory)[MedicationViewModel::class.java]
    }

    private fun setupRecyclerView() {
        medicationAdapter = MedicationAdapter(
            onEditClick = { medication ->
                showAddEditMedicationDialog(medication)
            },
            onDeleteClick = { medication ->
                showDeleteConfirmationDialog(medication)
            },
            onToggleActive = { medication ->
                medicationViewModel.updateMedication(
                    medication.copy(isActive = !medication.isActive)
                )
            }
        )

        binding.rvMedications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = medicationAdapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun setupObservers() {
        // Observe all medications
        medicationViewModel.allMedications.observe(viewLifecycleOwner) { medications ->
            medicationAdapter.submitList(medications)

            // Show/hide empty state
            if (medications.isEmpty()) {
                binding.layoutEmptyState.visibility = View.VISIBLE
                binding.rvMedications.visibility = View.GONE
            } else {
                binding.layoutEmptyState.visibility = View.GONE
                binding.rvMedications.visibility = View.VISIBLE
            }
        }

        // Observe add medication result
        medicationViewModel.addResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    showSuccessMessage("Obat berhasil ditambahkan")
                },
                onFailure = { exception ->
                    showErrorMessage(exception.message ?: "Gagal menambahkan obat")
                }
            )
        }

        // Observe loading state
        medicationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.fabAddMedication.setOnClickListener {
            showAddEditMedicationDialog(null)
        }

        binding.btnAddFirstMedication.setOnClickListener {
            showAddEditMedicationDialog(null)
        }
    }

    private fun showAddEditMedicationDialog(medication: Medication?) {
        val isEdit = medication != null
        AddEditMedicationDialog.newInstance(medication) { updatedMedication ->
            if (isEdit) {
                medicationViewModel.updateMedication(updatedMedication)
            } else {
                medicationViewModel.addMedication(
                    name = updatedMedication.name,
                    dosage = updatedMedication.dosage,
                    frequency = updatedMedication.frequency,
                    timeSchedule = updatedMedication.timeSchedule,
                    startDate = updatedMedication.startDate,
                    endDate = updatedMedication.endDate,
                    notes = updatedMedication.notes
                )
            }
        }.show(childFragmentManager, "AddEditMedication")
    }

    private fun showDeleteConfirmationDialog(medication: Medication) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Obat")
            .setMessage("Apakah Anda yakin ingin menghapus obat ${medication.name}?")
            .setPositiveButton("Hapus") { _, _ ->
                medicationViewModel.deleteMedication(medication)
                showSuccessMessage("Obat berhasil dihapus")
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showSuccessMessage(message: String) {
        com.google.android.material.snackbar.Snackbar.make(
            binding.root, message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showErrorMessage(message: String) {
        com.google.android.material.snackbar.Snackbar.make(
            binding.root, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}