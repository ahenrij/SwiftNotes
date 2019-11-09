package com.odyssey.swiftnotes.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.activity.NoteActivity
import com.odyssey.swiftnotes.database.NoteDAO
import com.odyssey.swiftnotes.helpers.Note
import com.odyssey.swiftnotes.helpers.RvNoteAdapter

/**
 * Created by ZskHenriJ on 30/09/2016.
 */
class NoteFragment : Fragment() {

    private var noteAdapter: RvNoteAdapter? = null
    private var recyclerView: RecyclerView? = null
    internal var notes: MutableList<Note>
    private var noteDAO: NoteDAO? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater!!.inflate(R.layout.frag_notes, container, false)
        recyclerView = v.findViewById<View>(R.id.recycler_view_notes) as RecyclerView
        val llm = LinearLayoutManager(activity)

        noteDAO = NoteDAO(activity)
        noteDAO!!.open()
        notes = noteDAO!!.listeNotes(NoteActivity.idCategorie)
        noteDAO!!.close()

        noteAdapter = RvNoteAdapter(notes, activity)

        if (recyclerView == null) {
            Toast.makeText(activity, "Erreur survenue", Toast.LENGTH_LONG).show()
        } else {
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.layoutManager = llm
            recyclerView!!.adapter = noteAdapter
        }
        return v
    }

    fun rafraichirListe() {
        noteAdapter!!.rafraichir()
    }

}
