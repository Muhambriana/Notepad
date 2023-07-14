package com.mshell.notepad.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mshell.notepad.AppController
import com.mshell.notepad.R
import com.mshell.notepad.core.db.DaoSession
import com.mshell.notepad.core.db.Note
import com.mshell.notepad.core.db.NoteDao.Properties
import com.mshell.notepad.databinding.ActivityDetailNoteBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date

class DetailNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailNoteBinding
    private lateinit var daoSession: DaoSession

    private var note: Note? = Note()
    private var isNotSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firstInit()
    }

    private fun firstInit() {
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        daoSession = (application as AppController).getDaoSession()

        val dataNote: Note? = intent.getParcelableExtra(EXTRA_DATA)
        if (dataNote != null) {
            note = dataNote
        }
        if (intent.getStringExtra(EXTRA_KEY) == "update") {
            fillEditText()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun fillEditText() {

        binding.edNoteTitle.setText(note?.title)
        binding.edNoteDesc.setText(note?.description)
        binding.tvLatestUpdate.text = note?.last_updated?.let { dateFormat(it) }
        binding.tvLatestUpdate.visibility = View.VISIBLE
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateFormat(date: Date):String {
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return formatter.format(date)
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

        showToast()
        setResult(RESULT_OK)
    }

    private fun showToast() {
        Toast.makeText(this, "Catatan Berhasil Disimpan", Toast.LENGTH_LONG).show()
    }

    private fun updateToDB() = runBlocking {
        val data = async { saveNote() }
        data.await()?.last_updated = Date()
        daoSession.noteDao.update(note)

        showToast()
        setResult(RESULT_OK)
    }

    private fun showConfirmDialog() {
        if (note?.title != binding.edNoteTitle.text.toString() || note?.description != binding.edNoteDesc.text.toString()) {
            println("masuk kondisi ${note?.title}  || ${binding.edNoteTitle.text.toString()}")
            MaterialAlertDialogBuilder(this)
                .setMessage("Are you sure want to quit without save")
                .setPositiveButton("STAY") {_, _->

                }
                .setNegativeButton("QUIT") {_, _ ->
                    finish()
                }
                .setCancelable(false)
                .show()
        } else {
            super.onBackPressed()
        }
    }

    private fun onTextChangeListener(menuItem: MenuItem) {
        binding.edNoteTitle.addTextChangedListener {
            //hide and show save button
            isNotSaved = true
            menuItem.isVisible = isNotSaved
        }

        binding.edNoteDesc.addTextChangedListener {
            isNotSaved = true
            menuItem.isVisible = isNotSaved
        }
    }

    private fun getLatestSavedDate(): String {
        val item = daoSession.noteDao.queryBuilder().where(Properties.Id.eq(note?.id)).unique().last_updated
        return dateFormat(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_note_menu, menu)
        menu?.findItem(R.id.save_menu)?.let { onTextChangeListener(it) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_menu -> {
                if(intent.getStringExtra(EXTRA_KEY) == "update") {
                    updateToDB()
                    isNotSaved = false
                    item.isVisible = isNotSaved
                    binding.tvLatestUpdate.text = getLatestSavedDate()
                } else {
                    saveToDB()
                    isNotSaved = false
                    item.isVisible = isNotSaved
                    binding.tvLatestUpdate.text = getLatestSavedDate()
                }

                true
            }
            android.R.id.home -> {
                showConfirmDialog()
                onBackPressed()
                true
            }
            else -> true
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showConfirmDialog()
    }

    companion object{
        const val EXTRA_KEY = "extra_key"
        const val EXTRA_DATA = "extra_data"
    }
}