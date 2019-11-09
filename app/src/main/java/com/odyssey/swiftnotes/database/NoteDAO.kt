package com.odyssey.swiftnotes.database


import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import com.odyssey.swiftnotes.activity.NoteActivity
import com.odyssey.swiftnotes.helpers.Note

import java.util.ArrayList

/**
 * Generated by ObscuriaAndroidDAOGenerator
 */
class NoteDAO(context: Context) : DAOBase(context) {
    internal var toutesLesColonnes = arrayOf(MyNotesHandler.ID_NOTE, MyNotesHandler.TITRE_NOTE, MyNotesHandler.DATE_NOTE, MyNotesHandler.DESCRIPTION_NOTE, MyNotesHandler.ALARME_NOTE, MyNotesHandler.IDCATEG_NOTE)

    fun addNote(n: Note): Long {
        val values = ContentValues()
        values.put(MyNotesHandler.TITRE_NOTE, n.titre)
        values.put(MyNotesHandler.DATE_NOTE, n.date)
        values.put(MyNotesHandler.DESCRIPTION_NOTE, n.description)
        values.put(MyNotesHandler.ALARME_NOTE, n.alarmStatus)
        values.put(MyNotesHandler.IDCATEG_NOTE, n.idcateg)
        return db.insert(MyNotesHandler.NOM_TABLE_NOTE, null, values)
    }

    fun delNote(n: Note): Int {
        return db.delete(MyNotesHandler.NOM_TABLE_NOTE, MyNotesHandler.ID_NOTE + " = ?", arrayOf(n.id.toString()))
    }

    fun delNote(id: Long): Int {
        return db.delete(MyNotesHandler.NOM_TABLE_NOTE, MyNotesHandler.ID_NOTE + " = ?", arrayOf(id.toString()))
    }

    fun modifyNote(n: Note): Int {
        val values = ContentValues()
        values.put(MyNotesHandler.TITRE_NOTE, n.titre)
        values.put(MyNotesHandler.DATE_NOTE, n.date)
        values.put(MyNotesHandler.DESCRIPTION_NOTE, n.description)
        values.put(MyNotesHandler.ALARME_NOTE, n.alarmStatus)
        values.put(MyNotesHandler.IDCATEG_NOTE, n.idcateg)

        return db.update(MyNotesHandler.NOM_TABLE_NOTE, values, MyNotesHandler.ID_NOTE + " = ?", arrayOf(n.id.toString()))
    }

    fun selectAllNotes(idCateg: Long): Cursor {
        return db.query(MyNotesHandler.NOM_TABLE_NOTE, toutesLesColonnes, MyNotesHandler.IDCATEG_NOTE + " = ?", arrayOf(idCateg.toString()), null, null, null)
    }

    fun selectAllRappels(): Cursor {
        return db.query(MyNotesHandler.NOM_TABLE_NOTE, toutesLesColonnes, MyNotesHandler.ALARME_NOTE + " = ?", arrayOf(NoteActivity.ALARME_ACTIF), null, null, null)
    }

    fun listeNotes(idCateg: Long): List<Note> {
        val liste = ArrayList<Note>()
        val cursor = selectAllNotes(idCateg)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            liste.add(Note(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getLong(5), cursor.getString(4)))
            cursor.moveToNext()
        }
        cursor.close()
        return liste
    }

    fun selectNote(id_note: Long): Note? {
        var note: Note? = null
        val cursor = db.query(MyNotesHandler.NOM_TABLE_NOTE, toutesLesColonnes, MyNotesHandler.ID_NOTE + " = ?", arrayOf(id_note.toString()), null, null, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            note = Note(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getLong(5), cursor.getString(4))
            cursor.moveToNext()
        }
        cursor.close()
        return note
    }

    fun listeRappels(): List<Note> {
        val liste = ArrayList<Note>()
        val cursor = selectAllRappels()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            liste.add(Note(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getLong(5), cursor.getString(4)))
            cursor.moveToNext()
        }
        cursor.close()
        return liste
    }

    fun deleteAllNotes() {
        db.delete(MyNotesHandler.NOM_TABLE_NOTE, null, null)
    }

}
