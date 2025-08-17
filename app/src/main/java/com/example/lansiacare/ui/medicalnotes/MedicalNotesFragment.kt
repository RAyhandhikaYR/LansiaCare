package com.lansiacare.ui.medicalnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.lansiacare.LansiaCareApplication
import com.example.lansiacare.data.entities.MedicalNote
import com.example.lansiacare.databinding.FragmentMedicalNotesBinding
import com.example.lansiacare.utils.ViewModelFactory
import com.example.lansiacare.ui.medicalnotes.MedicalNotesViewModel
import com.example.lansiacare.ui.medicalnotes.MedicalNotesAdapter
import com.example.lansiacare.ui.medicalnotes.AddEditMedicalNoteDialog
import com.example.lansiacare.ui.medicalnotes.MedicalNoteDetailDialog

class MedicalNotesFragment : Fragment() {

    private var _binding: FragmentMedicalNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var medicalNotesViewModel: MedicalNotesViewModel
    private lateinit var medicalNotesAdapter: MedicalNotesAdapter
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicalNotesBinding.inflate(inflater, container, false)
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
        val application = requireActivity().application as LansiaCareApplication

        viewModelFactory = ViewModelFactory(
            application.userRepository,
            application.medicationRepository,
            application.healthRecordRepository,
            application.medicalNoteRepository,
            application.userPreferences
        )

        medicalNotesViewModel = ViewModelProvider(this, viewModelFactory)[MedicalNotesViewModel::class.java]
    }

    private fun setupRecyclerView() {
        medicalNotesAdapter = MedicalNotesAdapter(
            onEditClick = { note ->
                showAddEditNoteDialog(note)
            },
            onDeleteClick = { note ->
                showDeleteConfirmationDialog(note)
            },
            onViewClick = { note ->
                showNoteDetailDialog(note)
            }
        )

        binding.rvMedicalNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = medicalNotesAdapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun setupObservers() {
        medicalNotesViewModel.medicalNotes.observe(viewLifecycleOwner) { notes ->
            medicalNotesAdapter.submitList(notes)

            // Show/hide empty state
            if (notes.isEmpty()) {
                binding.layoutEmptyState.visibility = View.VISIBLE
                binding.rvMedicalNotes.visibility = View.GONE
            } else {
                binding.layoutEmptyState.visibility = View.GONE
                binding.rvMedicalNotes.visibility = View.VISIBLE
            }
        }

        medicalNotesViewModel.addResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    showSuccessMessage("Catatan medis berhasil disimpan")
                },
                onFailure = { exception ->
                    showErrorMessage(exception.message ?: "Gagal menyimpan catatan")
                }
            )
        }

        medicalNotesViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.fabAddNote.setOnClickListener {
            showAddEditNoteDialog(null)
        }

        binding.btnAddFirstNote.setOnClickListener {
            showAddEditNoteDialog(null)
        }
    }

    private fun showAddEditNoteDialog(note: MedicalNote?) {
        val isEdit = note != null
        AddEditMedicalNoteDialog.newInstance(note) { savedNote ->
            if (isEdit) {
                medicalNotesViewModel.updateMedicalNote(savedNote)
            } else {
                medicalNotesViewModel.addMedicalNote(
                    title = savedNote.title,
                    description = savedNote.description,
                    doctorName = savedNote.doctorName,
                    hospital = savedNote.hospital,
                    visitDate = savedNote.visitDate
                )
            }
        }.show(childFragmentManager, "AddEditMedicalNote")
    }

    private fun showDeleteConfirmationDialog(note: MedicalNote) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Catatan")
            .setMessage("Apakah Anda yakin ingin menghapus catatan \"${note.title}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                medicalNotesViewModel.deleteMedicalNote(note)
                showSuccessMessage("Catatan berhasil dihapus")
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showNoteDetailDialog(note: MedicalNote) {
        MedicalNoteDetailDialog.newInstance(note).show(childFragmentManager, "MedicalNoteDetail")
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
