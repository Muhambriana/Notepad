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

    private var try1 = 1
    var try2 = 1

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
        var try3 = 0
        fun bind(note: Note){
            with(binding){
                try2++
                println("calling try3 in $try2 times ")
                try3++
                println("calling try3 in $try3 times ")
//                binding.root.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.random_color_1))
                binding.root.setCardBackgroundColor(ContextCompat.getColor(itemView.context, randomColor.random()))
                binding.noteTitle.text = note.title
            }
        }

//        private fun getRandomColor(pos: Int ):Int {
//
//            for (pos in colorPosition) {
//                if (pos == listNote)
//            }
//        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(listNote[adapterPosition])
            }
        }
    }
}