package com.odyssey.swiftnotes.helpers

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.activity.DetailsNoteActivity
import com.odyssey.swiftnotes.activity.NoteActivity
import com.odyssey.swiftnotes.database.MyNotesHandler
import com.odyssey.swiftnotes.database.NoteDAO

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by ZskHenriJ on 01/10/2016.
 */
class RvNoteAdapter(notes: MutableList<Note>, context: Context) : RecyclerView.Adapter<RvNoteAdapter.NoteViewHolder>() {
    internal var noteDAO: NoteDAO
    internal var idCategorie: Long = 0
    internal var dateFormat: SimpleDateFormat
    internal var dateFormatShort: SimpleDateFormat
    internal var date: Date

    init {
        RvNoteAdapter.notes = notes
        mContext = context
        idCategorie = NoteActivity.idCategorie
        noteDAO = NoteDAO(mContext)
        dateFormat = SimpleDateFormat(
                DetailsNoteActivity.FORMAT_DATE, Locale.getDefault())
        dateFormatShort = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    }

    class NoteViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cardView: CardView
        internal var icone_note: ImageView
        internal var titre_note: TextView
        internal var date_note: TextView
        internal var desciption_note: TextView
        internal var alarme: ImageView
        internal var overflow: ImageView
        internal var lay_card_note: LinearLayout

        init {
            cardView = itemView.findViewById<View>(R.id.cv_note) as CardView
            icone_note = itemView.findViewById<View>(R.id.icone_note) as ImageView
            titre_note = itemView.findViewById<View>(R.id.titre_note) as TextView
            date_note = itemView.findViewById<View>(R.id.date_note) as TextView
            desciption_note = itemView.findViewById<View>(R.id.description_note) as TextView
            alarme = itemView.findViewById<View>(R.id.icone_alarme) as ImageView
            overflow = itemView.findViewById<View>(R.id.overflow_notes) as ImageView
            lay_card_note = itemView.findViewById<View>(R.id.lay_card_note) as LinearLayout

            itemView.setOnClickListener {
                val position = adapterPosition

                val n = notes[position]
                val i = Intent(mContext, DetailsNoteActivity::class.java)
                i.putExtra(MyNotesHandler.ID_NOTE, n.id)
                i.putExtra(MyNotesHandler.IDCATEG_NOTE, n.idcateg)
                //                    i.putExtra(MyNotesHandler.COULEUR_CATEGORIE,NoteActivity.couleurCategorie);
                //                    i.putExtra(MyNotesHandler.TITRE_NOTE,n.getTitre());
                //                    i.putExtra(MyNotesHandler.DATE_NOTE,n.getDate());
                //                    i.putExtra(MyNotesHandler.DESCRIPTION_NOTE,n.getDescription());
                //                    i.putExtra(MyNotesHandler.ALARME_NOTE,n.getAlarmStatus());
                mContext.startActivity(i)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_card_view, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        //        holder.icone_note.setBackgroundColor(Color.parseColor(NoteActivity.couleurCategorie));

        holder.titre_note.text = notes[position].titre
        holder.date_note.text = notes[position].date

        try {
            date = dateFormat.parse(notes[position].date)
            holder.date_note.text = dateFormatShort.format(date)
        } catch (e: ParseException) {
            Log.d("Parsing exception", "Date parsing failed (RvNoteAdapter : " + e.toString() + " - Message : " + e.message)
        }


        if (notes[position].description!!.isEmpty()) {
            holder.desciption_note.text = mContext.resources.getString(R.string.descrip_undefined)
        } else {
            holder.desciption_note.text = notes[position].description //.substring(0,150)
        }
        val alarmStatus = notes[position].alarmStatus

        if (alarmStatus == NoteActivity.ALARME_ACTIF) {
            holder.alarme.visibility = View.VISIBLE
        } else if (alarmStatus == NoteActivity.ALARME_INACTIF) {
            holder.alarme.visibility = View.GONE
        }

        holder.overflow.setOnClickListener { showPopupMenu(holder.overflow, position) }
    }

    fun rafraichir() {
        noteDAO.open()
        notes.clear()
        notes = noteDAO.listeNotes(idCategorie)
        notifyDataSetChanged()
        noteDAO.close()
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

    internal inner class MyMenuItemClickListener(var position: Int) : PopupMenu.OnMenuItemClickListener {

        override fun onMenuItemClick(menuItem: MenuItem): Boolean {


            when (menuItem.itemId) {

                R.id.action_supprimer_note -> {
                    AlertDialog.Builder(mContext)
                            .setTitle("Confirmation")
                            .setMessage("Supprimer la note " + notes[position].titre + " ?")
                            .setPositiveButton(android.R.string.ok) { dialogInterface, z ->
                                noteDAO.open()
                                val i = noteDAO.delNote(notes[position].id)
                                noteDAO.close()
                                if (i != 1) {
                                    Toast.makeText(mContext, "Erreur survenue ", Toast.LENGTH_LONG).show()
                                }
                                Toast.makeText(mContext, "Note " + notes[position].titre + " supprimée", Toast.LENGTH_SHORT).show()
                                rafraichir()
                            }
                            .setPositiveButton(android.R.string.cancel) { dialogInterface, i -> dialogInterface.dismiss() }
                            .show()
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
