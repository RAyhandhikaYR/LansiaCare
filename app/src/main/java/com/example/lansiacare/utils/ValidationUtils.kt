package com.lansiacare.utils

import android.util.Patterns

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        val cleanPhone = phone.replace(Regex("[\\s\\-()]"), "")
        return cleanPhone.length >= 10 && cleanPhone.all { it.isDigit() || it == '+' }
    }

    fun isValidPassword(password: String): ValidationResult {
        return when {
            password.length < 6 -> ValidationResult.Error("Password minimal 6 karakter")
            password.isBlank() -> ValidationResult.Error("Password tidak boleh kosong")
            else -> ValidationResult.Success
        }
    }

    fun isValidName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("Nama tidak boleh kosong")
            name.length < 2 -> ValidationResult.Error("Nama minimal 2 karakter")
            name.length > 50 -> ValidationResult.Error("Nama maksimal 50 karakter")
            else -> ValidationResult.Success
        }
    }

    fun isValidBloodPressure(value: String): ValidationResult {
        val regex = Regex("""^\d{2,3}/\d{2,3}$""")
        return when {
            value.isBlank() -> ValidationResult.Error("Tekanan darah tidak boleh kosong")
            !regex.matches(value) -> ValidationResult.Error("Format harus seperti 120/80")
            else -> {
                val parts = value.split("/")
                val systolic = parts[0].toIntOrNull()
                val diastolic = parts[1].toIntOrNull()
                when {
                    systolic == null || diastolic == null -> ValidationResult.Error("Nilai harus berupa angka")
                    systolic < 70 || systolic > 300 -> ValidationResult.Error("Tekanan sistolik tidak valid (70-300)")
                    diastolic < 40 || diastolic > 200 -> ValidationResult.Error("Tekanan diastolik tidak valid (40-200)")
                    systolic <= diastolic -> ValidationResult.Error("Tekanan sistolik harus lebih tinggi dari diastolik")
                    else -> ValidationResult.Success
                }
            }
        }
    }

    fun isValidHeartRate(value: String): ValidationResult {
        return when {
            value.isBlank() -> ValidationResult.Error("Detak jantung tidak boleh kosong")
            else -> {
                val heartRate = value.toIntOrNull()
                when {
                    heartRate == null -> ValidationResult.Error("Detak jantung harus berupa angka")
                    heartRate < 30 || heartRate > 220 -> ValidationResult.Error("Detak jantung tidak valid (30-220 bpm)")
                    else -> ValidationResult.Success
                }
            }
        }
    }

    fun isValidBloodSugar(value: String): ValidationResult {
        return when {
            value.isBlank() -> ValidationResult.Error("Gula darah tidak boleh kosong")
            else -> {
                val bloodSugar = value.toIntOrNull()
                when {
                    bloodSugar == null -> ValidationResult.Error("Gula darah harus berupa angka")
                    bloodSugar < 50 || bloodSugar > 600 -> ValidationResult.Error("Gula darah tidak valid (50-600 mg/dL)")
                    else -> ValidationResult.Success
                }
            }
        }
    }

    fun isValidSteps(value: String): ValidationResult {
        return when {
            value.isBlank() -> ValidationResult.Error("Jumlah langkah tidak boleh kosong")
            else -> {
                val steps = value.toIntOrNull()
                when {
                    steps == null -> ValidationResult.Error("Jumlah langkah harus berupa angka")
                    steps < 0 -> ValidationResult.Error("Jumlah langkah tidak boleh negatif")
                    steps > 50000 -> ValidationResult.Error("Jumlah langkah terlalu tinggi (maksimal 50,000)")
                    else -> ValidationResult.Success
                }
            }
        }
    }

    fun isValidWeight(value: String): ValidationResult {
        return when {
            value.isBlank() -> ValidationResult.Error("Berat badan tidak boleh kosong")
            else -> {
                val weight = value.toFloatOrNull()
                when {
                    weight == null -> ValidationResult.Error("Berat badan harus berupa angka")
                    weight < 20 || weight > 300 -> ValidationResult.Error("Berat badan tidak valid (20-300 kg)")
                    else -> ValidationResult.Success
                }
            }
        }
    }

    fun isValidMedicationName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("Nama obat tidak boleh kosong")
            name.length < 2 -> ValidationResult.Error("Nama obat minimal 2 karakter")
            name.length > 100 -> ValidationResult.Error("Nama obat terlalu panjang")
            else -> ValidationResult.Success
        }
    }

    fun isValidDosage(dosage: String): ValidationResult {
        return when {
            dosage.isBlank() -> ValidationResult.Error("Dosis obat tidak boleh kosong")
            dosage.length > 50 -> ValidationResult.Error("Dosis terlalu panjang")
            else -> ValidationResult.Success
        }
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()

        val isSuccess: Boolean
            get() = this is Success

        val errorMessage: String?
            get() = if (this is Error) message else null
    }
}