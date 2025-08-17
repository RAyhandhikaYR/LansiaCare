package com.example.lansiacare.ui.emergency

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.lansiacare.utils.Constants
import com.example.lansiacare.utils.ViewModelFactory
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.data.database.AppDatabase
import com.example.lansiacare.data.repository.MedicalNoteRepository
import com.example.lansiacare.data.repository.UserRepository
import com.example.lansiacare.data.repository.MedicationRepository
import com.example.lansiacare.data.repository.HealthRecordRepository
import com.example.lansiacare.R
import com.example.lansiacare.databinding.FragmentEmergencyBinding

class EmergencyFragment : Fragment() {

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!

    private lateinit var emergencyViewModel: EmergencyViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    // Permission launcher for phone calls
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        setupClickListeners()
        loadUserData()
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

        emergencyViewModel = ViewModelProvider(this, viewModelFactory)[EmergencyViewModel::class.java]
    }

    private fun setupObservers() {
        emergencyViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvEmergencyContactName.text = it.emergencyContact
                binding.tvEmergencyContactPhone.text = it.emergencyPhone
                binding.tvUserName.text = it.name
                binding.tvUserPhone.text = it.phone
            }
        }
    }

    private fun setupClickListeners() {
        // Emergency Call Buttons
        binding.btnCallAmbulance.setOnClickListener {
            showConfirmationDialog(
                title = "Panggil Ambulans",
                message = "Apakah Anda yakin ingin memanggil ambulans (119)?",
                phoneNumber = Constants.EMERGENCY_AMBULANCE
            )
        }

        binding.btnCallPolice.setOnClickListener {
            showConfirmationDialog(
                title = "Panggil Polisi",
                message = "Apakah Anda yakin ingin memanggil polisi (110)?",
                phoneNumber = Constants.EMERGENCY_POLICE
            )
        }

        binding.btnCallEmergencyContact.setOnClickListener {
            emergencyViewModel.currentUser.value?.let { user ->
                showConfirmationDialog(
                    title = "Hubungi Kontak Darurat",
                    message = "Apakah Anda yakin ingin menghubungi ${user.emergencyContact}?",
                    phoneNumber = user.emergencyPhone
                )
            }
        }

        // Quick Actions
        binding.btnSendLocationSms.setOnClickListener {
            sendLocationSMS()
        }

        binding.btnMedicalInfo.setOnClickListener {
            showMedicalInfoDialog()
        }

        // Edit Emergency Contact
        binding.btnEditEmergencyContact.setOnClickListener {
            showEditEmergencyContactDialog()
        }
    }

    private fun loadUserData() {
        emergencyViewModel.loadCurrentUser()
    }

    private fun showConfirmationDialog(title: String, message: String, phoneNumber: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("Ya, Panggil") { _, _ ->
                makePhoneCall(phoneNumber)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (checkCallPermission()) {
            try {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$phoneNumber")
                startActivity(callIntent)

                // Log emergency call (for tracking purposes)
                emergencyViewModel.logEmergencyCall(phoneNumber)

            } catch (e: Exception) {
                showErrorMessage("Gagal melakukan panggilan: ${e.message}")
            }
        } else {
            requestCallPermission()
        }
    }

    private fun checkCallPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCallPermission() {
        when {
            shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                showPermissionRationaleDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Izin Panggilan Diperlukan")
            .setMessage("Aplikasi memerlukan izin untuk melakukan panggilan darurat. Mohon berikan izin untuk fitur ini.")
            .setPositiveButton("Berikan Izin") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Izin Ditolak")
            .setMessage("Tanpa izin panggilan, fitur darurat tidak dapat berfungsi optimal. Anda dapat memberikan izin melalui Pengaturan > Aplikasi > Lansia Care > Izin.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun sendLocationSMS() {
        emergencyViewModel.currentUser.value?.let { user ->
            try {
                // In real app, you would get actual GPS coordinates
                val locationMessage = "DARURAT: Saya ${user.name} membutuhkan bantuan. Lokasi saya: [Koordinat GPS akan disisipkan di sini]. Mohon segera hubungi saya di ${user.phone}."

                val smsIntent = Intent(Intent.ACTION_SENDTO)
                smsIntent.data = Uri.parse("smsto:${user.emergencyPhone}")
                smsIntent.putExtra("sms_body", locationMessage)
                startActivity(smsIntent)

                Toast.makeText(requireContext(), "SMS darurat disiapkan", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                showErrorMessage("Gagal mengirim SMS: ${e.message}")
            }
        }
    }

    private fun showMedicalInfoDialog() {
        MedicalInfoDialog.newInstance().show(childFragmentManager, "MedicalInfo")
    }

    private fun showEditEmergencyContactDialog() {
        emergencyViewModel.currentUser.value?.let { user ->
            EditEmergencyContactDialog.newInstance(user) { updatedUser ->
                emergencyViewModel.updateUser(updatedUser)
                showSuccessMessage("Kontak darurat berhasil diperbarui")
            }.show(childFragmentManager, "EditEmergencyContact")
        }
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