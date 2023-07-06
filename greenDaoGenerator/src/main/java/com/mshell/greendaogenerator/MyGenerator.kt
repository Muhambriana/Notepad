package com.mshell.greendaogenerator

import org.greenrobot.greendao.generator.DaoGenerator
import org.greenrobot.greendao.generator.Entity
import org.greenrobot.greendao.generator.Schema

object MyGenerator {

    private lateinit var schema: Schema

    @JvmStatic
    fun main(args: Array<String>) {
        schema = Schema(1, "com.mshell.greendaogenerator.db")
        schema.enableKeepSectionsByDefault()

        addTables()

        DaoGenerator().generateAll(schema, "./app/src/main/java")
    }

    private fun addTables() {
        addNoteEntities()
//        addUserEntities()
    }

    private fun addNoteEntities(): Entity {
        val note: Entity = schema.addEntity("Note")
        note.addIdProperty().primaryKey().autoincrement()
        note.addIntProperty("note_id").notNull()
        note.addStringProperty("title")
        note.addStringProperty("description")
        note.addDateProperty("date_created")
        note.addDateProperty("last_updated")
        return note
    }

//    private fun addUserEntities(): Entity {
//        val user: Entity = schema.addEntity("User")
//        user.addIdProperty().primaryKey().autoincrement()
//        user.addIntProperty("user_id").notNull()
//        user.addStringProperty("first_name")
//        user.addStringProperty("last_name")
//        user.addStringProperty("email")
//        return user
//    }
}