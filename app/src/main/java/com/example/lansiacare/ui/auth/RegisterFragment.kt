package com.example.lansiacare.ui.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import java.util.*
import com.example.lansiacare.data.database.AppDatabase
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.utils.ViewModelFactory
import com.example.lansiacare.utils.DateUtils
import com.example.lansiacare.data.repository.HealthRecordRepository
import com.example.lansiacare.data.repository.MedicationRepository
import com.example.lansiacare.data.repository.MedicalNoteRepository
import com.example.lansiacare.data.repository.UserRepository
import com.example.lansiacare.MainActivity
import com.example.lansiacare.R
import com.example.lansiacare.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    private var selectedBirthDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
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

        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
    }

    private fun setupObservers() {
        // Observer untuk hasil registrasi
        authViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                        navigateToDashboard()
                    }
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }
            )
        }

        // Observer untuk loading state
        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.btnRegister.text = ""
                binding.progressBar.visibility = View.VISIBLE
                binding.btnRegister.isEnabled = false
            } else {
                binding.btnRegister.text = getString(R.string.register)
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
            }
        }
    }

    private fun setupClickListeners() {
        // Register button click
        binding.btnRegister.setOnClickListener {
            if (validateAndRegister()) {
                val name = binding.etName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val phone = binding.etPhone.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                val emergencyContact = binding.etEmergencyContact.text.toString().trim()
                val emergencyPhone = binding.etEmergencyPhone.text.toString().trim()

                authViewModel.register(
                    name = name,
                    email = email,
                    password = password,
                    phone = phone,
                    emergencyContact = emergencyContact,
                    emergencyPhone = emergencyPhone,
                    dateOfBirth = selectedBirthDate!!
                )
            }
        }

        // Login link click
        binding.tvLoginLink.setOnClickListener {
            findNavController().navigateUp()
        }

        // Birth date picker
        binding.etBirthDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Set default to 70 years ago (typical elderly age)
        calendar.set(currentYear - 70, currentMonth, currentDay)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedBirthDate = calendar.time
                binding.etBirthDate.setText(DateUtils.formatDate(selectedBirthDate!!))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set max date to today (can't be born in the future)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun validateAndRegister(): Boolean {
        var isValid = true

        // Validate name
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tilName.error = "Nama tidak boleh kosong"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        // Validate email
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email tidak boleh kosong"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Format email tidak valid"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        // Validate phone
        val phone = binding.etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.tilPhone.error = "Nomor telepon tidak boleh kosong"
            isValid = false
        } else if (phone.length < 10) {
            binding.tilPhone.error = "Nomor telepon minimal 10 digit"
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        // Validate password
        val password = binding.etPassword.text.toString().trim()
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password tidak boleh kosong"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password minimal 6 karakter"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        // Validate emergency contact
        val emergencyContact = binding.etEmergencyContact.text.toString().trim()
        if (emergencyContact.isEmpty()) {
            binding.tilEmergencyContact.error = "Nama kontak darurat tidak boleh kosong"
            isValid = false
        } else {
            binding.tilEmergencyContact.error = null
        }

        // Validate emergency phone
        val emergencyPhone = binding.etEmergencyPhone.text.toString().trim()
        if (emergencyPhone.isEmpty()) {
            binding.tilEmergencyPhone.error = "Nomor kontak darurat tidak boleh kosong"
            isValid = false
        } else if (emergencyPhone.length < 10) {
            binding.tilEmergencyPhone.error = "Nomor telepon minimal 10 digit"
            isValid = false
        } else {
            binding.tilEmergencyPhone.error = null
        }

        // Validate birth date
        if (selectedBirthDate == null) {
            binding.tilBirthDate.error = "Tanggal lahir harus dipilih"
            isValid = false
        } else {
            binding.tilBirthDate.error = null
        }

        return isValid
    }

    private fun navigateToDashboard() {
        findNavController().navigate(R.id.action_registerFragment_to_dashboardFragment)

        // Show bottom navigation
        (activity as? MainActivity)?.binding?.navView?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}