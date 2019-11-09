package com.odyssey.swiftnotes.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.database.NoteDAO
import com.odyssey.swiftnotes.helpers.Note
import com.odyssey.swiftnotes.helpers.RvRappelAdapter

/**
 * Created by ZskHenriJ on 30/09/2016.
 */
class RappelFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var noteDAO: NoteDAO? = null
    private var noteList: MutableList<Note>? = null
    private var rappelAdapter: RvRappelAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater!!.inflate(R.layout.frag_rappel, container, false)
        recyclerView = v.findViewById<View>(R.id.recycler_view_rappels) as RecyclerView
        val llm = LinearLayoutManager(activity)

        noteDAO = NoteDAO(activity)
        noteDAO!!.open()
        noteList = noteDAO!!.listeRappels()
        noteDAO!!.close()

        rappelAdapter = RvRappelAdapter(noteList, activity)


        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = rappelAdapter

        return v
    }

}
