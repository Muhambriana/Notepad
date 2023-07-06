package com.mshell.notepad

import android.app.Application
import com.mshell.notepad.core.db.DaoMaster
import com.mshell.notepad.core.db.DaoMaster.DevOpenHelper
import com.mshell.notepad.core.db.DaoSession

class AppController: Application(){

    private lateinit var daoSession: DaoSession
    private lateinit var helper: DevOpenHelper

    override fun onCreate() {
        super.onCreate()

        helper = DaoMaster.DevOpenHelper(this, "notes-db")
        val db= helper.writableDb
        daoSession = DaoMaster(db).newSession()
    }

    fun getDaoSession(): DaoSession {
        return daoSession
    }

    companion object{
        const val ENCRYPTED = true
    }
}