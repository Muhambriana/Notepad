package com.mshell.notepad.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mshell.notepad.AppController
import com.mshell.notepad.R
import com.mshell.notepad.core.db.DaoSession
import com.mshell.notepad.core.db.Note
import com.mshell.notepad.databinding.ActivityCreateNoteBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date

class CreateNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNoteBinding
    private lateinit var daoSession: DaoSession

    private var note: Note? = Note()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firstInit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_menu -> {
                if(intent.getStringExtra(EXTRA_KEY) == "update") {
                    note = intent.getParcelableExtra(EXTRA_DATA)
                    updateToDB()
                } else {
                    saveToDB()
                }

                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> true
        }
    }

    private fun firstInit() {
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        daoSession = (application as AppController).getDaoSession()

        if (intent.getStringExtra(EXTRA_KEY) == "update") {
            updateNote(intent.getParcelableExtra<Note>(EXTRA_DATA))
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateNote(note: Note?) {

        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        println("before: ${note?.date_created}")
        println("after: ${note?.last_updated?.let { formatter.format(it) }}")
        binding.edNoteTitle.setText(note?.title)
        binding.edNoteDesc.setText(note?.description)
        binding.tvLatestUpdate.text = note?.last_updated?.let { formatter.format(it) }
        binding.tvLatestUpdate.visibility = View.VISIBLE
    }

    private fun saveNote():Note? {
        note?.title = binding.edNoteTitle.text.toString()
        note?.description = binding.edNoteDesc.text.toString()
        note?.last_updated = Date()
        return note
    }

    private fun saveToDB() = runBlocking {
        val data = async { saveNote() }
        data.await()?.date_created = Date()
        data.await()?.last_updated = Date()
        daoSession.noteDao.insert(data.await())
    }

    private fun updateToDB() = runBlocking {
        val data = async { saveNote() }
        data.await()?.last_updated = Date()
        daoSession.noteDao.update(note)
        println("kocak ${data.await()?.last_updated}")
        setResult(RESULT_OK)
    }

    companion object{
        const val EXTRA_KEY = "extra_key"
        const val EXTRA_DATA = "extra_data"
    }
}