package com.mshell.notepad.ui

import android.content.Intent
import kotlinx.coroutines.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.mshell.notepad.AppController
import com.mshell.notepad.R
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
        getAndShowData(null)
    }

    private fun getAndShowData(keyWord: String?) = runBlocking {
        val dataNote = async { getNoteData(keyWord) }

        binding.viewEmpty.root.visibility = View.GONE
        binding.viewNotFound.root.visibility =  View.GONE
        if (keyWord == null && dataNote.await().isEmpty()) {
            binding.viewEmpty.root.visibility = View.VISIBLE
            return@runBlocking
        } else if(keyWord != null && dataNote.await().isEmpty()) {
            binding.viewNotFound.root.visibility =  View.VISIBLE
            return@runBlocking
        }
        showRecyclerList(dataNote.await())
    }

    private fun hideRecycleList() {
        binding.rvNote.layoutManager = null
        binding.rvNote.adapter = null
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

    private fun getNoteData(keyWord: String?): List<Note> {
        val daoSession = (application as AppController).getDaoSession()
        val noteDao = daoSession.noteDao
        return if (keyWord == null) {
            println("bukan search")
            noteDao.queryBuilder().orderDesc(Properties.Last_updated).list()
        } else {
            println("masuk search ${noteDao.queryBuilder()
                .whereOr(Properties.Title.eq(keyWord), Properties.Description.eq(keyWord)).list().size}")
            noteDao.queryBuilder()
                .whereOr(Properties.Title.like("%$keyWord%"), Properties.Description.like("%$keyWord%")).list()
        }
    }

    private fun setLauncher() {
        //For get return data after launch activity
        resultLauncher =  registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            if (result.resultCode == RESULT_OK) {
                //Re-run getBookData and update with the latest
                getAndShowData(null)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_menu -> {
                searchViewListener(item)
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> true
        }
    }

    private fun searchViewListener(item: MenuItem) {
        // getting search view of our item.
        val searchView: SearchView = item.actionView as SearchView

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(title: String?): Boolean {
                hideRecycleList()
                getAndShowData(title)
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                return false
            }
        })

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                hideRecycleList()
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                getAndShowData(null)
                return true
            }
        })
    }

}