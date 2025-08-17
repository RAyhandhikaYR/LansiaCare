package com.example.lansiacare.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.ViewModelProvider
import com.example.lansiacare.data.database.AppDatabase
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.utils.ViewModelFactory
import com.example.lansiacare.data.repository.HealthRecordRepository
import com.example.lansiacare.data.repository.MedicationRepository
import com.example.lansiacare.data.repository.MedicalNoteRepository
import com.example.lansiacare.data.repository.UserRepository
import com.example.lansiacare.MainActivity
import com.example.lansiacare.R
import com.example.lansiacare.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
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
        // Observer untuk hasil login
        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { user ->
                    Toast.makeText(requireContext(), "Selamat datang, ${user.name}!", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                }
            )
        }

        // Observer untuk loading state
        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.btnLogin.text = ""
                binding.progressBar.visibility = View.VISIBLE
                binding.btnLogin.isEnabled = false
            } else {
                binding.btnLogin.text = getString(R.string.login)
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun setupClickListeners() {
        // Login button click
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                authViewModel.login(email, password)
            }
        }

        // Register link click
        binding.tvRegisterLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email tidak boleh kosong"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Format email tidak valid"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password tidak boleh kosong"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password minimal 6 karakter"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    private fun navigateToDashboard() {
        findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)

        // Show bottom navigation
        (activity as? MainActivity)?.binding?.navView?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}