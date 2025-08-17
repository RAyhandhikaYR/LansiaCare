package com.example.lansiacare.ui.dashboard

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.lansiacare.databinding.DialogAddHealthRecordBinding
import com.example.lansiacare.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddHealthRecordDialog : DialogFragment() {

    private var _binding: DialogAddHealthRecordBinding? = null
    private val binding get() = _binding!!

    private lateinit var recordType: String
    private lateinit var onRecordAdded: (String, String) -> Unit

    companion object {
        private const val ARG_RECORD_TYPE = "record_type"

        fun newInstance(
            recordType: String,
            onRecordAdded: (String, String) -> Unit
        ): AddHealthRecordDialog {
            val dialog = AddHealthRecordDialog()
            dialog.onRecordAdded = onRecordAdded
            val args = Bundle()
            args.putString(ARG_RECORD_TYPE, recordType)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordType = arguments?.getString(ARG_RECORD_TYPE) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddHealthRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupClickListeners()
    }

    private fun setupDialog() {
        val title = when (recordType) {
            Constants.RECORD_TYPE_BLOOD_PRESSURE -> "Tambah Tekanan Darah"
            Constants.RECORD_TYPE_HEART_RATE -> "Tambah Detak Jantung"
            Constants.RECORD_TYPE_BLOOD_SUGAR -> "Tambah Gula Darah"
            Constants.RECORD_TYPE_STEPS -> "Tambah Langkah Harian"
            else -> "Tambah Data Kesehatan"
        }

        binding.tvTitle.text = title

        val hint = when (recordType) {
            Constants.RECORD_TYPE_BLOOD_PRESSURE -> "Contoh: 120/80"
            Constants.RECORD_TYPE_HEART_RATE -> "Contoh: 72"
            Constants.RECORD_TYPE_BLOOD_SUGAR -> "Contoh: 95"
            Constants.RECORD_TYPE_STEPS -> "Contoh: 5000"
            else -> "Masukkan nilai"
        }

        binding.tilValue.hint = hint
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val value = binding.etValue.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()

            if (validateInput(value)) {
                onRecordAdded(value, notes)
                Toast.makeText(requireContext(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun validateInput(value: String): Boolean {
        if (value.isEmpty()) {
            binding.tilValue.error = "Nilai tidak boleh kosong"
            return false
        }

        when (recordType) {
            Constants.RECORD_TYPE_BLOOD_PRESSURE -> {
                if (!value.matches(Regex("\\d+/\\d+"))) {
                    binding.tilValue.error = "Format harus seperti 120/80"
                    return false
                }
            }
            Constants.RECORD_TYPE_HEART_RATE,
            Constants.RECORD_TYPE_BLOOD_SUGAR,
            Constants.RECORD_TYPE_STEPS -> {
                if (value.toIntOrNull() == null) {
                    binding.tilValue.error = "Harus berupa angka"
                    return false
                }
            }
        }

        binding.tilValue.error = null
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
