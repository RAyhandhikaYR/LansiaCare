package com.example.lansiacare.ui.medication

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lansiacare.data.entities.Medication
import com.example.lansiacare.utils.Constants
import com.example.lansiacare.databinding.ItemMedicationBinding
import com.example.lansiacare.R



class MedicationAdapter(
    private val onEditClick: (Medication) -> Unit,
    private val onDeleteClick: (Medication) -> Unit,
    private val onToggleActive: (Medication) -> Unit
) : ListAdapter<Medication, MedicationAdapter.MedicationViewHolder>(MedicationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val binding = ItemMedicationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MedicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MedicationViewHolder(
        private val binding: ItemMedicationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(medication: Medication) {
            binding.apply {
                tvMedicationName.text = medication.name
                tvMedicationDosage.text = medication.dosage
                tvMedicationFrequency.text = getFrequencyText(medication.frequency)
                tvMedicationSchedule.text = medication.timeSchedule.joinToString(", ")
                tvMedicationNotes.text = medication.notes.ifEmpty { "Tidak ada catatan" }

                // Set active/inactive state
                switchActive.isChecked = medication.isActive

                // Change appearance based on active state
                val alpha = if (medication.isActive) 1.0f else 0.6f
                cardMedication.alpha = alpha

                val textColor = if (medication.isActive) {
                    ContextCompat.getColor(itemView.context, R.color.on_surface)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.on_surface_variant)
                }

                tvMedicationName.setTextColor(textColor)
                tvMedicationDosage.setTextColor(textColor)

                // Set click listeners
                btnEdit.setOnClickListener {
                    onEditClick(medication)
                }

                btnDelete.setOnClickListener {
                    onDeleteClick(medication)
                }

                switchActive.setOnCheckedChangeListener { _, _ ->
                    onToggleActive(medication)
                }

                // Show/hide notes
                if (medication.notes.isEmpty()) {
                    tvMedicationNotes.visibility = View.GONE
                    tvNotesLabel.visibility = View.GONE
                } else {
                    tvMedicationNotes.visibility = View.VISIBLE
                    tvNotesLabel.visibility = View.VISIBLE
                }
            }
        }

        private fun getFrequencyText(frequency: String): String {
            return when (frequency) {
                Constants.FREQUENCY_DAILY -> "Sekali sehari"
                Constants.FREQUENCY_TWICE_DAILY -> "Dua kali sehari"
                Constants.FREQUENCY_THREE_TIMES -> "Tiga kali sehari"
                Constants.FREQUENCY_WEEKLY -> "Sekali seminggu"
                else -> frequency
            }
        }
    }

    private class MedicationDiffCallback : DiffUtil.ItemCallback<Medication>() {
        override fun areItemsTheSame(oldItem: Medication, newItem: Medication): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Medication, newItem: Medication): Boolean {
            return oldItem == newItem
        }
    }
}