package com.example.lansiacare.ui.medicalnotes

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.*
import com.example.lansiacare.utils.DateUtils
import com.example.lansiacare.data.entities.MedicalNote
import com.example.lansiacare.databinding.DialogAddEditMedicalNoteBinding

class AddEditMedicalNoteDialog : DialogFragment() {

    private var _binding: DialogAddEditMedicalNoteBinding? = null
    private val binding get() = _binding!!

    private var medicalNote: MedicalNote? = null
    private lateinit var onNoteSaved: (MedicalNote) -> Unit
    private var visitDate: Date? = null

    companion object {
        private const val ARG_MEDICAL_NOTE = "medical_note"

        fun newInstance(
            medicalNote: MedicalNote? = null,
            onNoteSaved: (MedicalNote) -> Unit
        ): AddEditMedicalNoteDialog {
            val dialog = AddEditMedicalNoteDialog()
            dialog.onNoteSaved = onNoteSaved
            if (medicalNote != null) {
                val args = Bundle()
                // In real app, you would serialize the medical note object
                dialog.arguments = args
                dialog.medicalNote = medicalNote
            }
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEditMedicalNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupClickListeners()

        // If editing, populate fields
        medicalNote?.let { populateFields(it) }
    }

    private fun setupDialog() {
        val title = if (medicalNote != null) "Edit Catatan Medis" else "Tambah Catatan Medis"
        binding.tvTitle.text = title

        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            if (validateAndSave()) {
                dismiss()
            }
        }

        binding.etVisitDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun populateFields(note: MedicalNote) {
        binding.apply {
            etNoteTitle.setText(note.title)
            etDescription.setText(note.description)
            etDoctorName.setText(note.doctorName)
            etHospital.setText(note.hospital)
            etVisitDate.setText(DateUtils.formatDate(note.visitDate))
            visitDate = note.visitDate
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        if (visitDate != null) {
            calendar.time = visitDate!!
        }

        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            visitDate = calendar.time
            binding.etVisitDate.setText(DateUtils.formatDate(visitDate!!))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun validateAndSave(): Boolean {
        val title = binding.etNoteTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val doctorName = binding.etDoctorName.text.toString().trim()
        val hospital = binding.etHospital.text.toString().trim()

        // Validation
        if (title.isEmpty()) {
            binding.tilNoteTitle.error = "Judul catatan tidak boleh kosong"
            return false
        } else {
            binding.tilNoteTitle.error = null
        }

        if (description.isEmpty()) {
            binding.tilDescription.error = "Deskripsi tidak boleh kosong"
            return false
        } else {
            binding.tilDescription.error = null
        }

        if (visitDate == null) {
            Toast.makeText(requireContext(), "Pilih tanggal kunjungan", Toast.LENGTH_SHORT).show()
            return false
        }

        // Create or update medical note
        val savedNote = if (medicalNote != null) {
            medicalNote!!.copy(
                title = title,
                description = description,
                doctorName = doctorName,
                hospital = hospital,
                visitDate = visitDate!!
            )
        } else {
            MedicalNote(
                id = DateUtils.generateId(),
                userEmail = "", // Will be set by ViewModel
                title = title,
                description = description,
                doctorName = doctorName,
                hospital = hospital,
                visitDate = visitDate!!
            )
        }

        onNoteSaved(savedNote)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
