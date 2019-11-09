package com.odyssey.swiftnotes.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Window
import android.view.WindowManager

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.database.CategorieDAO
import com.odyssey.swiftnotes.database.MyNotesHandler
import com.odyssey.swiftnotes.database.NoteDAO
import com.odyssey.swiftnotes.fragment.CategoryFragment
import com.odyssey.swiftnotes.fragment.NoteFragment

import com.odyssey.swiftnotes.activity.DetailsNoteActivity.returnDarkerColor


class NoteActivity : AppCompatActivity() {

    internal var categorieDAO: CategorieDAO
    internal var noteDAO: NoteDAO
    internal var noteFragment = NoteFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        categorieDAO = CategorieDAO(this)
        categorieDAO.open()
        idCategorie = intent.getLongExtra(CategoryFragment.ID_CATEGORIE, -1)
        couleurCategorie = categorieDAO.selectCategorie(idCategorie).couleur

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.arrow_left)
        toolbar.setBackgroundColor(Color.parseColor(couleurCategorie))
        toolbar.title = "Notes - " + categorieDAO.selectCategorie(idCategorie).titre!!

        toolbar.setNavigationOnClickListener { finish() }

        setStatusBarColor(returnDarkerColor(Color.parseColor(couleurCategorie)))

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.backgroundTintList = ColorStateList.valueOf(Color.parseColor(couleurCategorie))
        fab.setOnClickListener {
            val i = Intent(this@NoteActivity, DetailsNoteActivity::class.java)
            i.putExtra(MyNotesHandler.IDCATEG_NOTE, idCategorie)
            startActivity(i)
        }

        noteDAO = NoteDAO(this)
        /*noteDAO.open();

        if(noteDAO.listeNotes(idCategorie).isEmpty()){
            ajouterNotesDeCateg(idCategorie);
        }

        noteDAO.close();*/
        categorieDAO.close()
        showFragment(noteFragment)
    }


    override fun onResume() {
        super.onResume()
        noteFragment.rafraichirListe()
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun showFragment(fragment: Fragment?) {
        if (fragment == null) {
            return
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.fragmentNoteLayout, fragment)
        fragmentTransaction.commit()
    }

    fun setStatusBarColor(color: Int) {
        val window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }

    companion object {

        var idCategorie: Long = 0
        var couleurCategorie: String? = null
        val ALARME_ACTIF = "actif"
        val ALARME_INACTIF = "inactif"
    }

    /*private void ajouterNotesDeCateg(long idCategorie){
        noteDAO.addNote(new Note(0,"Ma note numéro 1","01/10","Cette est la premiere note pour la catégorie "+String.valueOf(idCategorie),idCategorie,ALARME_ACTIF));
        noteDAO.addNote(new Note(0,"Ma note numéro 2","01/10","Cette est la deuxieme note pour la catégorie "+String.valueOf(idCategorie),idCategorie,ALARME_INACTIF));
        noteDAO.addNote(new Note(0, "Ma note numéro 3", "01/10", "Cette est la troisieme note  la catégorie "+String.valueOf(idCategorie), idCategorie, ALARME_INACTIF));
        noteDAO.addNote(new Note(0,"Ma note numéro 4","01/10","Cette est la quatrieme note pour la catégorie "+String.valueOf(idCategorie),idCategorie,ALARME_INACTIF));
        noteDAO.addNote(new Note(0, "Ma note numéro 5", "01/10", "Cette est la cinquieme note pour la  catégorie " + String.valueOf(idCategorie), idCategorie, ALARME_INACTIF));
        noteDAO.addNote(new Note(0, "Ma note numéro 6", "01/10", "Cette est la sixieme note pour la catégorie " + String.valueOf(idCategorie), idCategorie, ALARME_ACTIF));
        noteDAO.addNote(new Note(0, "Ma note numéro 7", "01/10", "Cette est la septieme note pour la catégorie " + String.valueOf(idCategorie), idCategorie, ALARME_INACTIF));
    }*/

}
