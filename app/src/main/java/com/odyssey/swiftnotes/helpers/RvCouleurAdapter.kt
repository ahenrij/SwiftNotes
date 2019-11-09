package com.odyssey.swiftnotes.helpers

import android.app.ListActivity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.activity.MainActivity

/**
 * Created by ZskHenriJ on 04/10/2016.
 */
class RvCouleurAdapter(private val mContext: Context, couleurs: List<String>) : RecyclerView.Adapter<RvCouleurAdapter.CouleurViewHolder>() {

    init {
        RvCouleurAdapter.couleurs = couleurs
    }


    class CouleurViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var tv_couleur: TextView
        internal var img_check: ImageView
        internal var lay_couleurs: RelativeLayout

        init {
            tv_couleur = view.findViewById<View>(R.id.carre_couleur) as TextView
            img_check = view.findViewById<View>(R.id.img_check) as ImageView
            lay_couleurs = view.findViewById<View>(R.id.layout_couleurs) as RelativeLayout
        }


    }

    internal inner class ItemClickListener(var position: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            MainActivity.couleur_position = position
            notifyDataSetChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouleurViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.carre_couleur, parent, false)
        return CouleurViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CouleurViewHolder, position: Int) {
        holder.tv_couleur.setBackgroundColor(Color.parseColor(couleurs!![position]))

        if (position == MainActivity.couleur_position) {
            holder.img_check.visibility = View.VISIBLE
        } else {
            holder.img_check.visibility = View.GONE
        }

        holder.lay_couleurs.setOnClickListener(ItemClickListener(position))
    }

    override fun getItemCount(): Int {
        return couleurs!!.size
    }

    companion object {
        private var couleurs: List<String>? = null
    }
}
