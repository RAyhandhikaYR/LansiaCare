package com.example.lansiacare.ui.medication

import android.app.TimePickerDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.example.lansiacare.data.entities.Medication
import com.example.lansiacare.utils.Constants
import com.example.lansiacare.utils.DateUtils
import com.example.lansiacare.databinding.DialogAddEditMedicationBinding
import java.util.*

class AddEditMedicationDialog : DialogFragment() {

    private var _binding: DialogAddEditMedicationBinding? = null
    private val binding get() = _binding!!

    private var medication: Medication? = null
    private lateinit var onMedicationSaved: (Medication) -> Unit
    private val timeSchedule = mutableListOf<String>()
    private var startDate: Date? = null
    private var endDate: Date? = null

    companion object {
        private const val ARG_MEDICATION = "medication"

        fun newInstance(
            medication: Medication? = null,
            onMedicationSaved: (Medication) -> Unit
        ): AddEditMedicationDialog {
            val dialog = AddEditMedicationDialog()
            dialog.onMedicationSaved = onMedicationSaved
            if (medication != null) {
                val args = Bundle()
                // In real app, you would serialize the medication object
                dialog.arguments = args
                dialog.medication = medication
            }
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEditMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupSpinners()
        setupClickListeners()

        // If editing, populate fields
        medication?.let { populateFields(it) }
    }

    private fun setupDialog() {
        val title = if (medication != null) "Edit Obat" else "Tambah Obat"
        binding.tvTitle.text = title

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupSpinners() {
        // Frequency spinner
        val frequencies = arrayOf(
            "Sekali sehari",
            "Dua kali sehari",
            "Tiga kali sehari",
            "Sekali seminggu"
        )

        val frequencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            frequencies
        )
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFrequency.adapter = frequencyAdapter
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

        binding.btnAddTime.setOnClickListener {
            showTimePickerDialog()
        }

        binding.etStartDate.setOnClickListener {
            showDatePickerDialog(true)
        }

        binding.etEndDate.setOnClickListener {
            showDatePickerDialog(false)
        }
    }

    private fun populateFields(medication: Medication) {
        binding.apply {
            etMedicationName.setText(medication.name)
            etDosage.setText(medication.dosage)
            etNotes.setText(medication.notes)
            etStartDate.setText(DateUtils.formatDate(medication.startDate))

            medication.endDate?.let {
                etEndDate.setText(DateUtils.formatDate(it))
                endDate = it
            }

            startDate = medication.startDate
            timeSchedule.addAll(medication.timeSchedule)
            updateTimeScheduleChips()

            // Set frequency spinner
            val frequencyIndex = when (medication.frequency) {
                Constants.FREQUENCY_DAILY -> 0
                Constants.FREQUENCY_TWICE_DAILY -> 1
                Constants.FREQUENCY_THREE_TIMES -> 2
                Constants.FREQUENCY_WEEKLY -> 3
                else -> 0
            }
            spinnerFrequency.setSelection(frequencyIndex)
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
            if (!timeSchedule.contains(timeString)) {
                timeSchedule.add(timeString)
                timeSchedule.sort()
                updateTimeScheduleChips()
            } else {
                Toast.makeText(requireContext(), "Waktu sudah ditambahkan", Toast.LENGTH_SHORT).show()
            }
        }, hour, minute, true).show()
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val selectedDate = calendar.time
            val dateString = DateUtils.formatDate(selectedDate)

            if (isStartDate) {
                startDate = selectedDate
                binding.etStartDate.setText(dateString)
            } else {
                endDate = selectedDate
                binding.etEndDate.setText(dateString)
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateTimeScheduleChips() {
        binding.chipGroupTimeSchedule.removeAllViews()

        timeSchedule.forEach { time ->
            val chip = Chip(requireContext())
            chip.text = time
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                timeSchedule.remove(time)
                updateTimeScheduleChips()
            }
            binding.chipGroupTimeSchedule.addView(chip)
        }
    }

    private fun validateAndSave(): Boolean {
        val name = binding.etMedicationName.text.toString().trim()
        val dosage = binding.etDosage.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            binding.tilMedicationName.error = "Nama obat tidak boleh kosong"
            return false
        } else {
            binding.tilMedicationName.error = null
        }

        if (dosage.isEmpty()) {
            binding.tilDosage.error = "Dosis tidak boleh kosong"
            return false
        } else {
            binding.tilDosage.error = null
        }

        if (timeSchedule.isEmpty()) {
            Toast.makeText(requireContext(), "Tambahkan minimal satu jadwal waktu", Toast.LENGTH_LONG).show()
            return false
        }

        if (startDate == null) {
            Toast.makeText(requireContext(), "Pilih tanggal mulai", Toast.LENGTH_SHORT).show()
            return false
        }

        // Get frequency
        val frequencyConstants = arrayOf(
            Constants.FREQUENCY_DAILY,
            Constants.FREQUENCY_TWICE_DAILY,
            Constants.FREQUENCY_THREE_TIMES,
            Constants.FREQUENCY_WEEKLY
        )
        val selectedFrequency = frequencyConstants[binding.spinnerFrequency.selectedItemPosition]

        // Create or update medication
        val savedMedication = if (medication != null) {
            medication!!.copy(
                name = name,
                dosage = dosage,
                frequency = selectedFrequency,
                timeSchedule = timeSchedule.toList(),
                startDate = startDate!!,
                endDate = endDate,
                notes = notes
            )
        } else {
            Medication(
                id = DateUtils.generateId(),
                userEmail = "", // Will be set by ViewModel
                name = name,
                dosage = dosage,
                frequency = selectedFrequency,
                timeSchedule = timeSchedule.toList(),
                startDate = startDate!!,
                endDate = endDate,
                notes = notes
            )
        }

        onMedicationSaved(savedMedication)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}