package com.odyssey.swiftnotes.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.activity.DetailsNoteActivity

import java.util.ArrayList
import java.util.Locale

import java.security.AccessController.getContext

/**
 * Created by ZskHenriJ on 08/01/2017.
 */
class Speech2TextFragment : Fragment() {

    private var mCallBack: TextListener? = null
    private var btn_talk: ImageView? = null
    private val REQ_CODE_SPEECH_INPUT = 100

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.frag_speech2text, container, false)

        btn_talk = view.findViewById<View>(R.id.img_mic) as ImageView

        btn_talk!!.setOnClickListener { promptSpeechInput() }

        return view
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
                    mCallBack!!.onTextChanged(result[0])
                }
            }
        }
    }

    interface TextListener {
        fun onTextChanged(texte: String)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallBack = activity as TextListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement TextListener")
        }

    }

    override fun onDetach() {
        mCallBack = null
        super.onDetach()
    }
}
