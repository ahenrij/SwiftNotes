//Specify your package
package com.odyssey.swiftnotes.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase

/**
 * Generated by ObscuriaAndroidDAOGenerator
 */
abstract class DAOBase internal constructor(context: Context) {
    internal var mHandler: MyNotesHandler
    var db: SQLiteDatabase
        internal set

    init {
        mHandler = MyNotesHandler(context)
    }

    fun open() {
        db = mHandler.writableDatabase
    }

    fun close() {
        db.close()
    }
}
