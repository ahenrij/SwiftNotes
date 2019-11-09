package com.odyssey.swiftnotes.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.database.CategorieDAO
import com.odyssey.swiftnotes.helpers.Categorie
import com.odyssey.swiftnotes.helpers.RvCategorieAdapter

/**
 * Created by ZskHenriJ on 30/09/2016.
 */
class CategoryFragment : Fragment() {

    private var adapter_categ: RvCategorieAdapter? = null
    private var recyclerView: RecyclerView? = null

    private var categorieList: MutableList<Categorie>? = null
    private var categorieDAO: CategorieDAO? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater!!.inflate(R.layout.frag_category, container, false)
        recyclerView = v.findViewById<View>(R.id.recycler_view) as RecyclerView
        val llm = LinearLayoutManager(activity)

        categorieDAO = CategorieDAO(activity)
        categorieDAO!!.open()

        //        categorieDAO.deleteAllCategories();

        /*   if(categorieDAO.listeCategories().isEmpty()){
            //Ajouter des catégories a la base donnees
            ajouterCategDefaut();
        }*/

        categorieList = categorieDAO!!.listeCategories()

        //Recycler View
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = llm
        adapter_categ = RvCategorieAdapter(activity, categorieList)
        recyclerView!!.adapter = adapter_categ
        categorieDAO!!.close()
        //Recycler View
        return v
    }

    /* private void ajouterCategDefaut(){
        categorieDAO.addCategorie(new Categorie(0,"Non catégorisées","#4c8bf5"));
        categorieDAO.addCategorie(new Categorie(0,"Notes personelles", "#e91e63"));
        categorieDAO.addCategorie(new Categorie(0,"Shopping", "#ffc107"));
        categorieDAO.addCategorie(new Categorie(0,"Bureau", "#4c8bf5"));
        categorieDAO.addCategorie(new Categorie(0,"Voyage", "#4caf50"));
        categorieDAO.addCategorie(new Categorie(0, "Idées", "#e91e63"));
    }*/

    fun rafraichirListe() {
        adapter_categ!!.rafraichir()
    }

    companion object {
        var ID_CATEGORIE = "categorie"
    }
}
