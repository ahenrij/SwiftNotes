package com.odyssey.swiftnotes.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.support.v4.content.WakefulBroadcastReceiver
import android.widget.Toast

import com.odyssey.swiftnotes.activity.NoteActivity
import com.odyssey.swiftnotes.database.MyNotesHandler
import com.odyssey.swiftnotes.database.NoteDAO

import java.util.Calendar

/**
 * Created by ZskHenriJ on 10/10/2016.
 */
class AlarmBroadcastReceiver : WakefulBroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

        var etat: String? = null  //Récupérer l'état de l'alarme encore pour gérer l'annulation

        val noteDAO = NoteDAO(context)
        noteDAO.open()
        val id = intent.getLongExtra(MyNotesHandler.ID_NOTE, -1)
        if (id > 0) {
            etat = noteDAO.selectNote(id)!!.alarmStatus
        }
        noteDAO.close()

        if (etat != null) {
            if (etat == NoteActivity.ALARME_ACTIF) {
                //Faire passer l'id a l'intent du service
                val comp = ComponentName(context.packageName, MyNotesAlarmService::class.java!!.getName())
                WakefulBroadcastReceiver.startWakefulService(context, intent.setComponent(comp))
                //Vibrer le téléphone
                val vibreur = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibreur.vibrate(2000)
            } else {
                Toast.makeText(context, "Alarme déjà annulée", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Id irrécupérable : Broadcast Receiver", Toast.LENGTH_LONG).show()
        }

    }

    fun setAlarm(context: Context, calendar: Calendar, id_note: Long) {
        val alarm_manager: AlarmManager
        val alarm_intent: PendingIntent
        alarm_manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)

        val req_code = id_note.toInt()

        intent.putExtra(MyNotesHandler.ID_NOTE, id_note)
        alarm_intent = PendingIntent.getBroadcast(context, req_code, intent, 0)

        alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarm_intent)
    }

}
