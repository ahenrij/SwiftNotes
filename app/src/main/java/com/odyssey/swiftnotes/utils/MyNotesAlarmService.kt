package com.odyssey.swiftnotes.utils

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.activity.DetailsNoteActivity
import com.odyssey.swiftnotes.activity.NoteActivity
import com.odyssey.swiftnotes.database.MyNotesHandler
import com.odyssey.swiftnotes.database.NoteDAO
import com.odyssey.swiftnotes.helpers.Note

/**
 * Created by ZskHenriJ on 13/10/2016.
 */
class MyNotesAlarmService : IntentService("MyNotesAlarmService") {

    private var mNotificationManager: NotificationManager? = null
    private val noteDAO = NoteDAO(this)
    private var note: Note? = null

    override fun onHandleIntent(intent: Intent?) {
        val id_note = intent!!.getLongExtra(MyNotesHandler.ID_NOTE, -1)
        showNotification(id_note)
        AlarmBroadcastReceiver.completeWakefulIntent(intent)
    }

    fun showNotification(id_note: Long) {

        noteDAO.open()
        note = noteDAO.selectNote(id_note)
        if (note == null) {
            noteDAO.close()
            return
        }
        note!!.alarmStatus = NoteActivity.ALARME_INACTIF
        noteDAO.modifyNote(note)
        noteDAO.close()


        NOTIFICATION_ID = note!!.id.toInt()
        val msg = note!!.description

        mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Intent appelé au clic sur la notification.
        val intent_note = Intent(this, DetailsNoteActivity::class.java)
        intent_note.putExtra(MyNotesHandler.ID_NOTE, note!!.id)
        intent_note.putExtra(MyNotesHandler.IDCATEG_NOTE, note!!.idcateg)
        //        intent_note.putExtra(MyNotesHandler.TITRE_NOTE,note.getTitre());
        //        intent_note.putExtra(MyNotesHandler.DESCRIPTION_NOTE,note.getDescription());
        //        intent_note.putExtra(MyNotesHandler.DATE_NOTE,note.getDate());

        val comp = ComponentName(this.packageName, DetailsNoteActivity::class.java!!.getName())
        intent_note.component = comp

        val contentIntent = PendingIntent.getActivity(this, id_note.toInt(),
                intent_note, 0)


        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.note)
                .setContentTitle(note!!.titre)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setTicker("Ma notification")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(msg)

        mBuilder.setContentIntent(contentIntent)
        mNotificationManager!!.notify(NOTIFICATION_ID, mBuilder.build())
    }

    companion object {
        var NOTIFICATION_ID = 1
    }
}
