package com.example.lansiacare.ui.dashboard

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.lansiacare.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class HealthRecordSelectionDialog : DialogFragment() {

    private lateinit var onRecordTypeSelected: (String) -> Unit

    companion object {
        fun newInstance(onRecordTypeSelected: (String) -> Unit): HealthRecordSelectionDialog {
            val dialog = HealthRecordSelectionDialog()
            dialog.onRecordTypeSelected = onRecordTypeSelected
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val options = arrayOf(
            "Tekanan Darah",
            "Detak Jantung",
            "Gula Darah",
            "Langkah Harian"
        )

        val recordTypes = arrayOf(
            Constants.RECORD_TYPE_BLOOD_PRESSURE,
            Constants.RECORD_TYPE_HEART_RATE,
            Constants.RECORD_TYPE_BLOOD_SUGAR,
            Constants.RECORD_TYPE_STEPS
        )

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Jenis Data Kesehatan")
            .setItems(options) { _, which ->
                onRecordTypeSelected(recordTypes[which])
            }
            .setNegativeButton("Batal", null)
            .create()
    }
}