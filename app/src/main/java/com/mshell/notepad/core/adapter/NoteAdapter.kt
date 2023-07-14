package com.mshell.notepad.core.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mshell.notepad.R
import com.mshell.notepad.core.db.Note
import com.mshell.notepad.databinding.ItemListNoteBinding

class NoteAdapter: RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var listNote = mutableListOf<Note>()
    private val randomColor = listOf(R.color.random_color_1, R.color.random_color_2, R.color.random_color_3, R.color.random_color_4, R.color.random_color_5, R.color.random_color_6)
    var onItemClick: ((Note) -> Unit)? = null

    fun setNoteList(notes: List<Note>?) {
        if (notes == null) return
        listNote.clear()
        listNote.addAll(notes)
    }

    // For create every item in recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_note, parent, false))


    // For set data to view
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = listNote[position]
        holder.bind(note)
    }

    // To get size of book inside adapter
    override fun getItemCount(): Int {
        return listNote.size
    }


    inner class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListNoteBinding.bind(itemView)
        fun bind(note: Note){
            with(binding){
//                binding.root.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.random_color_1))
                binding.root.setCardBackgroundColor(ContextCompat.getColor(itemView.context, randomColor.random()))
                binding.noteTitle.text = note.title
                binding.noteDesc.text = note.description
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(listNote[adapterPosition])
            }
        }
    }
}


