package com.mshell.notepad.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mshell.notepad.AppController
import com.mshell.notepad.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val daoSession = (application as AppController).getDaoSession()
        val noteDao = daoSession.noteDao

//        val newNote = Note()
//        newNote.title = "Catatan 1"
//        newNote.description = "Ini isi catatan"
//        noteDao.insert(newNote)

        val allUser = noteDao.loadAll()
        Log.i("Kocak", allUser[0].title)
    }
}