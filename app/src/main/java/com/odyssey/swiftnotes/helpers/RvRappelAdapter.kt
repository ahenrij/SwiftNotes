package com.odyssey.swiftnotes.helpers

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.activity.DetailsNoteActivity
import com.odyssey.swiftnotes.activity.NoteActivity
import com.odyssey.swiftnotes.database.CategorieDAO
import com.odyssey.swiftnotes.database.MyNotesHandler
import com.odyssey.swiftnotes.database.NoteDAO

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by ZskHenriJ on 03/10/2016.
 */
class RvRappelAdapter(notes: MutableList<Note>, context: Context) : RecyclerView.Adapter<RvRappelAdapter.RappelViewHolder>() {
    internal var noteDAO: NoteDAO
    internal var categorieDAO: CategorieDAO
    internal var dateFormat: SimpleDateFormat? = null
    internal var dateFormatShort: SimpleDateFormat? = null
    internal var date: Date

    init {
        this.notes = notes
        this.mContext = context
        noteDAO = NoteDAO(mContext)
        categorieDAO = CategorieDAO(mContext)
    }

    class RappelViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cardView: CardView
        internal var icone_note: ImageView
        internal var titre_note: TextView
        internal var date_note: TextView
        internal var desciption_note: TextView
        internal var alarme: ImageView
        internal var overflow: ImageView

        init {
            cardView = itemView.findViewById<View>(R.id.cv_note) as CardView
            icone_note = itemView.findViewById<View>(R.id.icone_note) as ImageView
            titre_note = itemView.findViewById<View>(R.id.titre_note) as TextView
            date_note = itemView.findViewById<View>(R.id.date_note) as TextView
            desciption_note = itemView.findViewById<View>(R.id.description_note) as TextView
            alarme = itemView.findViewById<View>(R.id.icone_alarme) as ImageView
            overflow = itemView.findViewById<View>(R.id.overflow_notes) as ImageView

            itemView.setOnClickListener {
                val position = adapterPosition
                //                    Toast.makeText(v.getContext(), "Click detected on item " + position, Toast.LENGTH_SHORT).show();

                val n = notes[position]
                val i = Intent(mContext, DetailsNoteActivity::class.java)

                i.putExtra(MyNotesHandler.ID_NOTE, n.id)
                i.putExtra(MyNotesHandler.IDCATEG_NOTE, n.idcateg)

                mContext.startActivity(i)
                //                    Note note = RvNoteAdapter.notes.get(position);
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RappelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_card_view, parent, false)
        return RappelViewHolder(view)
    }

    override fun onBindViewHolder(holder: RappelViewHolder, position: Int) {


        categorieDAO.open()
        val categorie = categorieDAO.selectCategorie(notes[position].idcateg)
        categorieDAO.close()
        val titre = notes[position].titre + " " + notes[position].alarmStatus

        holder.icone_note.setBackgroundColor(Color.parseColor(categorie.couleur))
        holder.titre_note.text = titre
        //        Date date = Date.valueOf(notes.get(position).getDate());
        //        holder.date_note.setText(String.valueOf(date.getDay()).concat("/").concat(String.valueOf(date.getMonth())));
        try {
            date = dateFormat!!.parse(notes[position].date)
            holder.date_note.text = dateFormatShort!!.format(date)
        } catch (e: ParseException) {
            Log.d("Parsing exception", "Date parsing failed (RvNoteAdapter : " + e.toString() + " - Message : " + e.message)
        }

        holder.desciption_note.text = notes[position].description //.substring(0,150)
        holder.alarme.visibility = View.VISIBLE


        if (notes[position].alarmStatus == NoteActivity.ALARME_INACTIF) {
            holder.alarme.visibility = View.GONE
        }
        holder.overflow.setOnClickListener { showPopupMenu(holder.overflow, position) }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(mContext, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_note, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(MyMenuItemClickListener(position))
        popupMenu.show()
    }

    fun rafraichirListe() {
        notes.clear()
        noteDAO.open()
        notes = noteDAO.listeRappels()
        notifyDataSetChanged()
        noteDAO.close()
    }

    internal inner class MyMenuItemClickListener(var position: Int) : PopupMenu.OnMenuItemClickListener {

        override fun onMenuItemClick(menuItem: MenuItem): Boolean {


            when (menuItem.itemId) {

                R.id.action_supprimer_note -> {
                    noteDAO.open()
                    val i = noteDAO.delNote(notes[position].id)
                    if (i != 1) {
                        Toast.makeText(mContext, "Erreur survenue ", Toast.LENGTH_LONG).show()
                        noteDAO.close()
                        return false
                    }
                    Toast.makeText(mContext, "Note " + notes[position].titre + " supprimée", Toast.LENGTH_LONG).show()
                    rafraichirListe()
                    return true
                }
            }
            return false
        }
    }

    companion object {

        var notes: MutableList<Note>
        var mContext: Context
    }
}
