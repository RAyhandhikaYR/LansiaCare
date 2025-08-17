package com.example.lansiacare.ui.emergency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.lansiacare.databinding.DialogEditEmergencyContactBinding
import com.example.lansiacare.data.entities.User

class EditEmergencyContactDialog : DialogFragment() {

    private var _binding: DialogEditEmergencyContactBinding? = null
    private val binding get() = _binding!!

    private lateinit var user: User
    private lateinit var onUserUpdated: (User) -> Unit

    companion object {
        private const val ARG_USER = "user"

        fun newInstance(user: User, onUserUpdated: (User) -> Unit): EditEmergencyContactDialog {
            val dialog = EditEmergencyContactDialog()
            dialog.user = user
            dialog.onUserUpdated = onUserUpdated
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditEmergencyContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateFields()
        setupClickListeners()
    }

    private fun populateFields() {
        binding.apply {
            etEmergencyContactName.setText(user.emergencyContact)
            etEmergencyContactPhone.setText(user.emergencyPhone)
        }
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
    }

    private fun validateAndSave(): Boolean {
        val name = binding.etEmergencyContactName.text.toString().trim()
        val phone = binding.etEmergencyContactPhone.text.toString().trim()

        if (name.isEmpty()) {
            binding.tilEmergencyContactName.error = "Nama kontak darurat tidak boleh kosong"
            return false
        } else {
            binding.tilEmergencyContactName.error = null
        }

        if (phone.isEmpty()) {
            binding.tilEmergencyContactPhone.error = "Nomor telepon tidak boleh kosong"
            return false
        } else if (phone.length < 10) {
            binding.tilEmergencyContactPhone.error = "Nomor telepon minimal 10 digit"
            return false
        } else {
            binding.tilEmergencyContactPhone.error = null
        }

        val updatedUser = user.copy(
            emergencyContact = name,
            emergencyPhone = phone
        )

        onUserUpdated(updatedUser)
        return true
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}