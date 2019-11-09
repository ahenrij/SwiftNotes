package com.odyssey.swiftnotes.fragment

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment
import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.activity.DetailsNoteActivity
import com.odyssey.swiftnotes.activity.NoteActivity
import com.odyssey.swiftnotes.database.NoteDAO
import com.odyssey.swiftnotes.helpers.Note
import com.odyssey.swiftnotes.utils.AlarmBroadcastReceiver

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/**
 * Created by ZskHenriJ on 10/10/2016.
 */
class AlarmFragment : Fragment() {

    private val format_date = "EEE dd MMMM yyyy"
    private val format_time = "HH:mm"
    internal var tv_date: TextView
    internal var tv_time: TextView
    internal var btn_set_date: ImageButton
    internal var btn_set_time: ImageButton
    internal var btn_define_alarme: Button
    internal var alarm_receiver = AlarmBroadcastReceiver()
    private var noteDAO: NoteDAO? = null
    private var dateFormat: SimpleDateFormat? = null
    private var timeFormat: SimpleDateFormat? = null
    private var calendar: Calendar? = null
    private var note: Note? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.frag_alarme, container, false)


        calendar = Calendar.getInstance()
        dateFormat = SimpleDateFormat(format_date, Locale.getDefault())
        timeFormat = SimpleDateFormat(format_time, Locale.getDefault())
        tv_date = view.findViewById<View>(R.id.tv_date_alarme) as TextView
        tv_time = view.findViewById<View>(R.id.tv_heure_alarme) as TextView
        btn_set_date = view.findViewById<View>(R.id.btn_change_date) as ImageButton
        btn_set_time = view.findViewById<View>(R.id.btn_change_time) as ImageButton
        btn_define_alarme = view.findViewById<View>(R.id.btn_define_alarme) as Button
        //        btn_define_alarme.setBackgroundColor(Color.parseColor(DetailsNoteActivity.couleur));
        update()
        noteDAO = NoteDAO(activity)

        //Afficher Annuler l'alarme si une alarme est déja definie pour cette note
        if (DetailsNoteActivity.id_note > 0) {
            noteDAO!!.open()
            note = noteDAO!!.selectNote(DetailsNoteActivity.id_note)
            if (note == null) {
                Toast.makeText(activity, "Erreur code 111. Impossible de définir l'alarme", Toast.LENGTH_SHORT).show()
            } else {
                if (note!!.alarmStatus == NoteActivity.ALARME_ACTIF) {
                    btn_define_alarme.text = resources.getString(R.string.annuler_alarme)
                }
            }
            noteDAO!!.close()
        }

        btn_set_date.setOnClickListener { showDatePicker() }

        btn_set_time.setOnClickListener { showTimePicker() }

        btn_define_alarme.setOnClickListener {
            if (DetailsNoteActivity.id_note <= 0) { //Il s'agit d'un ajout de note

                if (!(activity as DetailsNoteActivity).editNoteFragment.getTitre().isEmpty()) {
                    /**
                     * Ajouter d'abord la note avant de définir l'alarme
                     */
                    /**
                     * Ajouter d'abord la note avant de définir l'alarme
                     */
                    noteDAO!!.open()
                    note!!.titre = (activity as DetailsNoteActivity).editNoteFragment.getTitre()
                    note!!.description = (activity as DetailsNoteActivity).editNoteFragment.getDescription()
                    note!!.date = (activity as DetailsNoteActivity).editNoteFragment.todayToString()
                    note!!.idcateg = DetailsNoteActivity.id_categ
                    note!!.alarmStatus = NoteActivity.ALARME_INACTIF
                    DetailsNoteActivity.id_note = noteDAO!!.addNote(note)
                    if (DetailsNoteActivity.id_note > 0) {

                        note!!.id = DetailsNoteActivity.id_note
                        noteDAO!!.close()

                        //Définir l'alarme
                        definir_alarme()
                    } else {
                        Toast.makeText(activity, "Erreur survenue lors de l'enregistrement de la note.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(activity, "Veuillez ajouter un titre à votre note", Toast.LENGTH_SHORT).show()
                }

                /**
                 * TODO Enregistrer la date_time de l'alarme  de sorte à initialiser le fragment avec
                 */

                /**
                 * TODO Enregistrer la date_time de l'alarme  de sorte à initialiser le fragment avec
                 */

            } else { // La note existe déjà

                /**
                 * Annuler l'alarme
                 */

                /**
                 * Annuler l'alarme
                 */
                if (btn_define_alarme.text.toString() == resources.getString(R.string.annuler_alarme)) {
                    if (note != null) {
                        noteDAO!!.open()
                        note!!.alarmStatus = NoteActivity.ALARME_INACTIF
                        noteDAO!!.modifyNote(note)
                        noteDAO!!.close()
                        btn_define_alarme.text = resources.getString(R.string.definir_alarme)
                        Toast.makeText(activity, "Alarme annulée !", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    /**
                     * Definir une alarme
                     */
                    /**
                     * Definir une alarme
                     */
                    definir_alarme()
                }
            }
        }
        return view
    }

    fun update() {
        tv_date.text = dateFormat!!.format(calendar!!.time)
        tv_time.text = timeFormat!!.format(calendar!!.time)
    }

    fun showDatePicker() {
        val cdp = CalendarDatePickerDialogFragment()
                .setOnDateSetListener { dialog, year, monthOfYear, dayOfMonth ->
                    calendar!!.set(year, monthOfYear, dayOfMonth)
                    update()
                }
                .setFirstDayOfWeek(Calendar.MONDAY)
        cdp.show(fragmentManager, "DatePicker")
    }

    fun showTimePicker() {

        val rtpd = RadialTimePickerDialogFragment()
                .setOnTimeSetListener { dialog, hourOfDay, minute ->
                    calendar!!.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar!!.set(Calendar.MINUTE, minute)
                    update()
                }
        rtpd.show(fragmentManager, "TimePicker")
    }

    fun definir_alarme() {
        //Si le moment défini est inférieure au ooment de l'activation de l'alarme
        val actuellement = Calendar.getInstance()
        if (actuellement.timeInMillis > calendar!!.timeInMillis) {
            val mBuilder = AlertDialog.Builder(activity)
            mBuilder.setMessage("Définissez une date future")
                    .setTitle("Alarme")
                    .setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
            val dialog = mBuilder.create()
            dialog.show()

        } else { //Le moment de déclenchement de l'alarme est futur

            /**
             * Ici tout est ok pour définir l'alarme
             */
            noteDAO!!.open()
            note!!.alarmStatus = NoteActivity.ALARME_ACTIF
            noteDAO!!.modifyNote(note)
            noteDAO!!.close()

            alarm_receiver.setAlarm(activity, calendar, DetailsNoteActivity.id_note)
            btn_define_alarme.text = resources.getString(R.string.annuler_alarme)
            Toast.makeText(activity, "Alarme définie", Toast.LENGTH_SHORT).show()

        }
    }
}
