package com.odyssey.swiftnotes.helpers

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.activity.MainActivity
import com.odyssey.swiftnotes.activity.NoteActivity
import com.odyssey.swiftnotes.database.CategorieDAO
import com.odyssey.swiftnotes.database.MyNotesHandler
import com.odyssey.swiftnotes.database.NoteDAO
import com.odyssey.swiftnotes.fragment.CategoryFragment

import java.util.ArrayList

/**
 * Created by ZskHenriJ on 30/09/2016.
 */
class RvCategorieAdapter(mContext: Context, categories: MutableList<Categorie>) : RecyclerView.Adapter<RvCategorieAdapter.CategorieViewHolder>() {
    internal var categorieDAO: CategorieDAO
    internal var noteDAO: NoteDAO
    internal var et_category_name: EditText
    internal var input_category_lay: TextInputLayout


    init {
        RvCategorieAdapter.mContext = mContext
        RvCategorieAdapter.categories = categories
        categorieDAO = CategorieDAO(mContext)
        noteDAO = NoteDAO(mContext)
    }

    class CategorieViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cardView: CardView
        internal var couleurCateg: TextView
        internal var titreCateg: TextView
        internal var nombrElement: TextView
        internal var overflow: ImageView

        init {
            cardView = itemView.findViewById<View>(R.id.cv) as CardView
            couleurCateg = itemView.findViewById<View>(R.id.couleur_categorie) as TextView
            titreCateg = itemView.findViewById<View>(R.id.titre_category) as TextView
            nombrElement = itemView.findViewById<View>(R.id.nombre_elmt) as TextView
            overflow = itemView.findViewById<View>(R.id.overflow) as ImageView

            itemView.setOnClickListener { v ->
                val position = adapterPosition
                val idCategorie = RvCategorieAdapter.categories[position].id

                val intent = Intent(v.context, NoteActivity::class.java)
                intent.putExtra(CategoryFragment.ID_CATEGORIE, idCategorie)
                intent.putExtra(MyNotesHandler.COULEUR_CATEGORIE, categories[position].couleur)

                RvCategorieAdapter.mContext.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_card_view, parent, false)
        return CategorieViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategorieViewHolder, position: Int) {

        holder.couleurCateg.setBackgroundColor(Color.parseColor(categories[position].couleur))
        holder.couleurCateg.setText(categories[position].titre!!.substring(0, 1).toUpperCase())
        holder.titreCateg.text = categories[position].titre

        //        holder.nombrElement.setText(String.valueOf(categories.get(position).getId()));
        /*        if(Build.VERSION.SDK_INT >= 21){
              holder.nombrElement.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(categories.get(position).getCouleur())));
          }else{
              holder.nombrElement.setBackgroundColor(Color.parseColor(categories.get(position).getCouleur()));
          }
 */
        noteDAO.open()
        holder.nombrElement.text = noteDAO.listeNotes(categories[position].id).size.toString()
        if (Build.VERSION.SDK_INT > 21) {
            //            holder.nombrElement.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(categories.get(position).getCouleur())));
        }
        noteDAO.close()

        holder.overflow.setOnClickListener { showPopupMenu(holder.overflow, position) }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(mContext, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_categorie, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(MyMenuItemClickListener(position))
        popupMenu.show()
    }

    fun rafraichir() {
        categories.clear()
        categorieDAO.open()
        categories = categorieDAO.listeCategories()
        categorieDAO.close()
        notifyDataSetChanged()
    }

    internal inner class MyMenuItemClickListener(var position: Int) : PopupMenu.OnMenuItemClickListener {

        override fun onMenuItemClick(menuItem: MenuItem): Boolean {


            when (menuItem.itemId) {
                R.id.action_modifier_categ -> {
                    //                    Toast.makeText(mContext, "Modifier la catégorie "+categories.get(position).getTitre(), Toast.LENGTH_SHORT).show();


                    val dialog = Dialog(mContext)
                    dialog.setContentView(R.layout.dialog_add_categ)
                    dialog.setTitle("Modifier catégorie")

                    val couleursList = ArrayList<String>()

                    couleursList.add("#4c8bf5")
                    couleursList.add("#e91e63")
                    couleursList.add("#C2185B")
                    couleursList.add("#ffc107")
                    couleursList.add("#673ab7")
                    couleursList.add("#ff9800")
                    couleursList.add("#f44336")
                    couleursList.add("#795548")
                    couleursList.add("#8bc34a")
                    couleursList.add("#9e9e9e")
                    couleursList.add("#607d8b")
                    couleursList.add("#9c27b0")
                    couleursList.add("#4caf50")

                    MainActivity.couleur_position = couleursList.indexOf(categories[position].couleur)

                    val adapter = RvCouleurAdapter(dialog.context, couleursList)
                    val layoutManager = GridLayoutManager(dialog.context, 5)

                    val recyclerView = dialog.findViewById<View>(R.id.recycler_view_couleur) as RecyclerView
                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager = layoutManager
                    recyclerView.addItemDecoration(GridSpacingItemDecoration(5, dpToPx(4), true))
                    recyclerView.itemAnimator = DefaultItemAnimator()
                    recyclerView.adapter = adapter

                    et_category_name = dialog.findViewById<View>(R.id.et_category_name) as EditText
                    et_category_name.setText(categories[position].titre)
                    et_category_name.setSelection(categories[position].titre!!.length)

                    input_category_lay = dialog.findViewById<View>(R.id.layout_category_name) as TextInputLayout
                    val btn_add_category = dialog.findViewById<View>(R.id.btn_add_category) as Button

                    dialog.setOnDismissListener { MainActivity.mCategoryFragment.rafraichirListe() }

                    btn_add_category.setOnClickListener { view ->
                        if (validateCategoryName()) {
                            categorieDAO = CategorieDAO(view.context)
                            categorieDAO.open()

                            val c = categories[position]
                            c.titre = et_category_name.text.toString()
                            c.couleur = couleursList[MainActivity.couleur_position]

                            categorieDAO.modifyCategorie(c)
                            categorieDAO.close()
                            dialog.dismiss()
                        }
                    }
                    dialog.show()


                    return true
                }
                R.id.action_supprimer_categ -> {

                    AlertDialog.Builder(mContext)
                            .setTitle("Confirmation")
                            .setMessage("Supprimer la catégorie " + categories[position].titre + " ?")
                            .setPositiveButton(android.R.string.ok) { dialogInterface, z ->
                                categorieDAO.open()
                                /**
                                 * Supprime toutes les notes de ladite catégorie avant de la supprimer
                                 */
                                /**
                                 * Supprime toutes les notes de ladite catégorie avant de la supprimer
                                 */
                                noteDAO.open()
                                val id_categ = categories[position].id
                                val noteList = noteDAO.listeNotes(id_categ)
                                for (i in noteList.indices) {
                                    noteDAO.delNote(noteList[i])
                                }
                                noteDAO.close()

                                val i = categorieDAO.delCategorie(id_categ)
                                if (i != 1) {
                                    Toast.makeText(mContext, "Erreur survenue ", Toast.LENGTH_LONG).show()
                                    categorieDAO.close()
                                }
                                Toast.makeText(mContext, "Catégorie " + categories[position].titre + " supprimée", Toast.LENGTH_SHORT).show()
                                rafraichir()
                            }
                            .setNegativeButton(android.R.string.cancel) { dialogInterface, i -> dialogInterface.dismiss() }
                            .show()
                    return true
                }
            }
            return false
        }
    }

    /**
     * RecyclerView item decoration - Donne des margins égaux autour des items de la Grid
     */
    inner class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }

    /**
     * Convertit dp en pixel
     */
    private fun dpToPx(dp: Int): Int {
        val r = mContext.resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
    }

    private fun validateCategoryName(): Boolean {

        categorieDAO = CategorieDAO(mContext)
        categorieDAO.open()

        categorieDAO.close()

        if (et_category_name.text.toString().trim { it <= ' ' }.isEmpty()) {
            input_category_lay.error = mContext.getString(R.string.error_category_name)
            return false
        } else if (MainActivity.couleur_position == -1) {
            input_category_lay.isErrorEnabled = false
            Toast.makeText(mContext, "Sélectionnez une couleur", Toast.LENGTH_SHORT).show()
            return false
        } else {
            input_category_lay.isErrorEnabled = false
            return true
        }
    }

    companion object {

        var categories: MutableList<Categorie>
        var mContext: Context
    }
}
