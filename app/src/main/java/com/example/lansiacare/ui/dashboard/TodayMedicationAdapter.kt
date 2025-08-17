package com.example.lansiacare.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lansiacare.data.entities.Medication
import com.example.lansiacare.databinding.ItemMedicationTodayBinding

class TodayMedicationAdapter(
    private val onMedicationTaken: (Medication) -> Unit
) : ListAdapter<Medication, TodayMedicationAdapter.MedicationViewHolder>(MedicationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val binding = ItemMedicationTodayBinding.inflate(
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
        private val binding: ItemMedicationTodayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(medication: Medication) {
            binding.tvMedicationName.text = "${medication.name} ${medication.dosage}"

            // Show first time schedule for today (simplified)
            if (medication.timeSchedule.isNotEmpty()) {
                binding.tvMedicationTime.text = medication.timeSchedule[0]
            }

            binding.btnTakeMedication.setOnClickListener {
                onMedicationTaken(medication)
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