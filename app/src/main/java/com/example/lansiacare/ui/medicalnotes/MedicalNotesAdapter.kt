package com.example.lansiacare.ui.medicalnotes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lansiacare.data.entities.MedicalNote
import com.example.lansiacare.databinding.ItemMedicalNoteBinding
import com.example.lansiacare.utils.DateUtils


class MedicalNotesAdapter(
    private val onEditClick: (MedicalNote) -> Unit,
    private val onDeleteClick: (MedicalNote) -> Unit,
    private val onViewClick: (MedicalNote) -> Unit
) : ListAdapter<MedicalNote, MedicalNotesAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemMedicalNoteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(
        private val binding: ItemMedicalNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: MedicalNote) {
            binding.apply {
                tvNoteTitle.text = note.title
                tvNoteDate.text = DateUtils.formatDate(note.visitDate)
                tvNoteDoctor.text = if (note.doctorName.isNotEmpty()) {
                    "Dr. ${note.doctorName}"
                } else {
                    "Dokter tidak disebutkan"
                }
                tvNoteHospital.text = note.hospital.ifEmpty { "Rumah Sakit tidak disebutkan" }
                tvNotePreview.text = if (note.description.length > 100) {
                    "${note.description.take(100)}..."
                } else {
                    note.description
                }

                // Click listeners
                cardNote.setOnClickListener { onViewClick(note) }
                btnEdit.setOnClickListener { onEditClick(note) }
                btnDelete.setOnClickListener { onDeleteClick(note) }
            }
        }
    }

    private class NoteDiffCallback : DiffUtil.ItemCallback<MedicalNote>() {
        override fun areItemsTheSame(oldItem: MedicalNote, newItem: MedicalNote): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MedicalNote, newItem: MedicalNote): Boolean {
            return oldItem == newItem
        }
    }
}
