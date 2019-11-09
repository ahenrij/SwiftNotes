package com.odyssey.swiftnotes.activity

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.database.CategorieDAO
import com.odyssey.swiftnotes.database.MyNotesHandler
import com.odyssey.swiftnotes.database.NoteDAO
import com.odyssey.swiftnotes.fragment.AlarmFragment
import com.odyssey.swiftnotes.fragment.DetailsNoteActivityFragment
import com.odyssey.swiftnotes.fragment.Speech2TextFragment
import com.odyssey.swiftnotes.helpers.Categorie
import com.odyssey.swiftnotes.helpers.Note
import com.roughike.bottombar.BottomBar
import com.roughike.bottombar.OnMenuTabSelectedListener

class DetailsNoteActivity : AppCompatActivity(), Speech2TextFragment.TextListener {

    var editNoteFragment: DetailsNoteActivityFragment
    val mAlarmFragment = AlarmFragment()
    val mSpeech2TextFragment = Speech2TextFragment()
    private var coordinatorLayout: CoordinatorLayout? = null
    internal var mFragment: String
    var textSpeech: String
        internal set
    internal var noteDAO: NoteDAO
    internal var categorieDAO: CategorieDAO
    internal var mContext: Context
    internal var note: Note? = Note()
    internal var categorie: Categorie
    internal var bottomBar: BottomBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_note)

        noteDAO = NoteDAO(this)
        categorieDAO = CategorieDAO(this)
        categorieDAO.open()
        mContext = this
        textSpeech = ""

        id_note = intent.getLongExtra(MyNotesHandler.ID_NOTE, -1)
        id_categ = intent.getLongExtra(MyNotesHandler.IDCATEG_NOTE, -1)
        categorie = categorieDAO.selectCategorie(id_categ)
        categorieDAO.close()
        couleur = categorie.couleur

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.arrow_left)
        toolbar.title = "Note"
        toolbar.setNavigationOnClickListener { enregisterNote() }
        toolbar.setBackgroundColor(Color.parseColor(couleur))

        setStatusBarColor(returnDarkerColor(Color.parseColor(couleur)))

        coordinatorLayout = findViewById<View>(R.id.detail_note_activity) as CoordinatorLayout
        bottomBar = BottomBar.attach(this, savedInstanceState)

        editNoteFragment = DetailsNoteActivityFragment()

        bottomBar.setItemsFromMenu(R.menu.menu_detail_note) { itemId ->
            when (itemId) {
                R.id.menu_edit_note -> if (mFragment != editNoteFragment.javaClass.getSimpleName()) {
                    showFragment(editNoteFragment)
                }
            /*case R.id.menu_speech2text:
                        if (!mFragment.equals(mSpeech2TextFragment.getClass().getSimpleName())) {
                            showFragment(mSpeech2TextFragment);
                        }
                        break;*/
                R.id.menu_alarm_note -> if (mFragment != mAlarmFragment.javaClass.getSimpleName()) {
                    showFragment(mAlarmFragment)
                }
            }
        }

        //        bottomBar.findViewById(R.id.menu_speech2text).setOn

        bottomBar.setActiveTabColor(couleur)
        showFragment(editNoteFragment)
    }

    fun showFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.fragmentEditNote, fragment)
        mFragment = fragment.javaClass.getSimpleName()
        /* if (!mFragment.equals(mEditNoteFragment.getClass().getSimpleName())) {
            mEditNoteFragment = (DetailsNoteActivityFragment) fragment;
        }*/
        fragmentTransaction.commit()
    }

    fun enregisterNote() {

        if (!editNoteFragment.titre.isEmpty()) {

            //Il faut revenir au fragment d'edition avant de quitter
            if (mFragment != editNoteFragment.javaClass.getSimpleName()) {
                bottomBar.selectTabAtPosition(0, true)
                //                showFragment(mEditNoteFragment);
                return
            }

            if (id_note <= 0) {
                /**
                 * Ajout de note
                 */
                noteDAO.open()
                note!!.titre = editNoteFragment.titre
                note!!.description = editNoteFragment.description
                note!!.date = editNoteFragment.todayToString()
                note!!.idcateg = id_categ
                note!!.alarmStatus = alarm_status
                id_note = noteDAO.addNote(note)

                noteDAO.close()
                if (id_note > 0) {
                    Toast.makeText(mContext, "Note ajoutée !", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "Erreur durant l'ajout", Toast.LENGTH_LONG).show()
                }
            } else {
                /**
                 * Modification de note
                 */
                noteDAO.open()
                note = noteDAO.selectNote(id_note)
                note!!.titre = editNoteFragment.titre
                note!!.description = editNoteFragment.description
                note!!.date = editNoteFragment.todayToString()
                val i = noteDAO.modifyNote(note)
                noteDAO.close()
                if (i == 1) {
                    Toast.makeText(mContext, "Note mise à jour", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "Erreur durant la modification", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(mContext, "Aucune mise à jour", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    fun setStatusBarColor(color: Int) {
        val window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }

    override fun onTextChanged(texte: String) {
        bottomBar.selectTabAtPosition(0, true)
        textSpeech = texte
        if (mFragment != editNoteFragment.javaClass.getSimpleName()) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            fragmentTransaction.replace(R.id.fragmentEditNote, editNoteFragment)
            mFragment = editNoteFragment.javaClass.getSimpleName()
            /* if (!mFragment.equals(mEditNoteFragment.getClass().getSimpleName())) {
            mEditNoteFragment = (DetailsNoteActivityFragment) fragment;
        }*/
            fragmentTransaction.commit()
        }
    }

    fun getCouleur(): String? {
        return couleur
    }

    companion object {

        val FORMAT_DATE = "EEE dd MMMM yyyy  HH:mm:ss"
        var couleur: String? = null
        var alarm_status = NoteActivity.ALARME_INACTIF
        var id_note: Long = 0
        var id_categ: Long = 0

        fun returnDarkerColor(color: Int): Int {
            val ratio = 1.0f - 0.2f
            val a = color shr 24 and 0xFF
            val r = ((color shr 16 and 0xFF) * ratio).toInt()
            val g = ((color shr 8 and 0xFF) * ratio).toInt()
            val b = ((color and 0xFF) * ratio).toInt()

            return a shl 24 or (r shl 16) or (g shl 8) or b
        }
    }
}
