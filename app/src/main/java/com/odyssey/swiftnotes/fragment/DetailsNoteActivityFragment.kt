package com.odyssey.swiftnotes.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.speech.RecognizerIntent
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.activity.DetailsNoteActivity
import com.odyssey.swiftnotes.database.NoteDAO
import com.odyssey.swiftnotes.helpers.Note

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.RandomAccess


/**
 * A placeholder fragment containing a simple view.
 */
class DetailsNoteActivityFragment : Fragment() {

    internal var titre: String? = null
    internal var date: String? = null
    internal var description: String? = null

    var et_titre_note: EditText
    var et_description_note: EditText
    private var fabTxt2Speech: FloatingActionButton? = null
    private var tv_date_note: TextView? = null
    internal var activity: DetailsNoteActivity
    private val REQ_CODE_SPEECH_INPUT = 100

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (getActivity() is DetailsNoteActivity) {
            activity = getActivity() as DetailsNoteActivity
        }

        val view = inflater!!.inflate(R.layout.fragment_details_note, container, false)
        et_titre_note = view.findViewById<View>(R.id.et_titre_note) as EditText
        et_description_note = view.findViewById<View>(R.id.et_description_note) as EditText
        tv_date_note = view.findViewById<View>(R.id.tv_date_note) as TextView
        fabTxt2Speech = view.findViewById<View>(R.id.fabtxt2speech) as FloatingActionButton


        if (DetailsNoteActivity.id_note > 0) {

            val noteDAO = NoteDAO(getActivity())
            noteDAO.open()
            val note = noteDAO.selectNote(DetailsNoteActivity.id_note)
            noteDAO.close()

            //            titre = getActivity().getIntent().getStringExtra(MyNotesHandler.TITRE_NOTE);
            titre = note!!.titre
            //            date = getActivity().getIntent().getStringExtra(MyNotesHandler.DATE_NOTE);
            date = note.date
            //            description = getActivity().getIntent().getStringExtra(MyNotesHandler.DESCRIPTION_NOTE);

            description = note.description

            et_description_note.setText(description)

            if (!activity.textSpeech.isEmpty()) {

                et_description_note.setText("OK")
            }
            /*Bundle bundle = getArguments();
            if (bundle != null) {
                description = bundle.getString("desc", "");
                Toast.makeText(getContext(), description, Toast.LENGTH_LONG).show();
            }*/

            et_titre_note.setText(titre)
            et_titre_note.setSelection(titre!!.length)
            tv_date_note!!.text = date
        } else {

            if (!activity.textSpeech.isEmpty()) {
                et_description_note.setText("OK")
            }
            tv_date_note!!.text = todayToString()
        }

        fabTxt2Speech!!.setBackgroundColor(Color.parseColor(DetailsNoteActivity.couleur))

        fabTxt2Speech!!.setOnClickListener { promptSpeechInput() }

        return view
    }

    fun getTitre(): String {
        return et_titre_note.text.toString()
    }

    fun getDescription(): String {
        return et_description_note.text.toString()
    }

    fun todayToString(): String {
        val dateFormat = SimpleDateFormat(
                DetailsNoteActivity.FORMAT_DATE, Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun promptSpeechInput() {
        //Showing google speech input
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt))

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    et_description_note.setText(result[0])
                }
            }
        }
    }

}
