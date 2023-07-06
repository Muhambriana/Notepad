package com.mshell.notepad.ui

import android.content.Intent
import kotlinx.coroutines.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.mshell.notepad.AppController
import com.mshell.notepad.core.adapter.NoteAdapter
import com.mshell.notepad.core.db.Note
import com.mshell.notepad.core.db.NoteDao.Properties
import com.mshell.notepad.databinding.ActivityNoteBinding

class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private val noteAdapter = NoteAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firstInit()
    }

    private fun firstInit() {
        setLauncher()
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, DetailNoteActivity::class.java)
            intent.putExtra(DetailNoteActivity.EXTRA_KEY,"create")
            resultLauncher.launch(intent)
        }
        getAndShowData()
    }

    private fun getAndShowData() = runBlocking {
        val dataNote = async { getNoteData() }
        showRecyclerList(dataNote.await())
    }

    private fun showRecyclerList(dataNote: List<Note>) {
        binding.rvNote.layoutManager = LinearLayoutManager(this)
        binding.rvNote.adapter = noteAdapter
        noteAdapter.setNoteList(dataNote)

        onItemClick()
    }

    private fun onItemClick() {
        noteAdapter.onItemClick = {note ->
            val intent = Intent(this, DetailNoteActivity::class.java)
            intent.putExtra(DetailNoteActivity.EXTRA_KEY,"update")
            intent.putExtra(DetailNoteActivity.EXTRA_DATA,note)
            resultLauncher.launch(intent)
        }
    }

    private fun getNoteData(): List<Note> {
        val daoSession = (application as AppController).getDaoSession()
        val noteDao = daoSession.noteDao
        val queryRes: List<Note> = noteDao.queryBuilder().orderDesc(Properties.Last_updated).list()

        return queryRes
    }

    private fun setLauncher() {
        //For get return data after launch activity
        resultLauncher =  registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            if (result.resultCode == RESULT_OK) {
                //Re-run getBookData and update with the latest
                getAndShowData()
            }
        }
    }

}