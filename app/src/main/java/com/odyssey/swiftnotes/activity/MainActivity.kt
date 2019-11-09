package com.odyssey.swiftnotes.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.SearchView

import android.util.TypedValue
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.odyssey.swiftnotes.R
import com.odyssey.swiftnotes.database.CategorieDAO
import com.odyssey.swiftnotes.fragment.AboutFragment
import com.odyssey.swiftnotes.fragment.CategoryFragment
import com.odyssey.swiftnotes.fragment.RappelFragment
import com.odyssey.swiftnotes.helpers.Categorie
import com.odyssey.swiftnotes.helpers.RvCouleurAdapter

import java.util.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val mAboutFragment = AboutFragment()
    private val mRappelFragment = RappelFragment()
    private val mCategoryFragment = CategoryFragment()
    var couleur_position = -1
    private var fab: FloatingActionButton? = null
    private var mFragment: String? = null
    private var KEY_FRAGMENT = "fragment"
    lateinit var couleursList: MutableList<String>
    internal lateinit var adapter: RvCouleurAdapter
    internal lateinit var et_category_name: EditText
    internal lateinit var input_category_lay: TextInputLayout
    internal lateinit var categorieDAO: CategorieDAO
    internal var mSearchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab!!.setOnClickListener(OnAddCategorieListener())

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        showFragment(mCategoryFragment)
    }

    override fun onResume() {
        super.onResume()
        mCategoryFragment.rafraichirListe()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString(KEY_FRAGMENT, mFragment)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        if (savedInstanceState != null) {
            mFragment = savedInstanceState.getString(KEY_FRAGMENT)
        } else {
            mFragment = intent.getStringExtra(KEY_FRAGMENT)
        }

        if (mFragment != null) {


            if (mFragment == (mCategoryFragment::class).simpleName) {
                if (fab!!.visibility == View.GONE) {
                    fab!!.visibility = View.VISIBLE
                }
                showFragment(this.mCategoryFragment)
            } else if (mFragment == (mAboutFragment::class).simpleName) {
                if (fab!!.visibility == View.VISIBLE) {
                    fab!!.visibility = View.GONE
                }
                showFragment(this.mAboutFragment)
            } else if (mFragment == (mRappelFragment::class).simpleName) {
                if (fab!!.visibility == View.VISIBLE) {
                    fab!!.visibility = View.GONE
                }
                showFragment(this.mRappelFragment)
            }
        } else {
            if (fab!!.visibility == View.GONE) {
                fab!!.visibility = View.VISIBLE
            }
            showFragment(this.mCategoryFragment)
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (mFragment == (mCategoryFragment::class).simpleName) {
                super.onBackPressed()
            } else {
                showFragment(mCategoryFragment)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_home) {
            // Handle the camera action
            if (fab!!.visibility == View.GONE) {
                fab!!.visibility = View.VISIBLE
            }
            showFragment(mCategoryFragment)
            title = "SwiftNotes"
        } else if (id == R.id.nav_Rappels) {
            if (fab!!.visibility == View.VISIBLE) {
                fab!!.visibility = View.GONE
            }
            showFragment(mRappelFragment)
            title = "Rappels"
        } else if (id == R.id.nav_passlock) {

            AlertDialog.Builder(this)
                    .setTitle("Vérrouillage")
                    .setMessage("Fonctionnalité bientôt disponible !")
                    .setIcon(R.drawable.lock_red)
                    .show()

        } else if (id == R.id.nav_share) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            val shareBodyText = "Télécharger SwiftNotes pour gérer votre agenda avec facilité sur www.google.android.com/download" + "/swiftnotes.apk"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject/Title")
            intent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
            startActivity(Intent.createChooser(intent, "Partager avec"))

        } else if (id == R.id.nav_about) {

            startActivity(Intent(this@MainActivity, AboutActivity::class.java))
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Show Fragment - Affiche le fragment
     */
    private fun showFragment(fragment: Fragment?) {
        if (fragment == null) {
            return
        }
        KEY_FRAGMENT = fragment::class.simpleName!!
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.fragmentLayout, fragment)
        fragmentTransaction.commit()
        mFragment = fragment::class.simpleName
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
        val r = resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
    }

    /**
     * Listener du click sur le fab pour ajouter une catégorie
     */
    internal inner class OnAddCategorieListener : View.OnClickListener {
        override fun onClick(v: View) {

            val dialog = Dialog(v.context)
            dialog.setContentView(R.layout.dialog_add_categ)
            dialog.setTitle("Nouvelle catégorie")

            couleursList = ArrayList()

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

            adapter = RvCouleurAdapter(dialog.context, couleursList)
            val layoutManager = GridLayoutManager(dialog.context, 5)

            val recyclerView = dialog.findViewById<View>(R.id.recycler_view_couleur) as RecyclerView
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = layoutManager
            recyclerView.addItemDecoration(GridSpacingItemDecoration(5, dpToPx(4), true))
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.adapter = adapter

            et_category_name = dialog.findViewById<View>(R.id.et_category_name) as EditText

            input_category_lay = dialog.findViewById<View>(R.id.layout_category_name) as TextInputLayout
            val btn_add_category = dialog.findViewById<View>(R.id.btn_add_category) as Button

            dialog.setOnDismissListener { mCategoryFragment.rafraichirListe() }

            btn_add_category.setOnClickListener { v ->
                if (validateCategoryName()) {
                    categorieDAO = CategorieDAO(v.context)
                    categorieDAO.open()
                    val titre_categ = et_category_name.text.toString()
                    val couleur_categ = couleursList[couleur_position]

                    categorieDAO.addCategorie(Categorie(0, titre_categ, couleur_categ))
                    categorieDAO.close()
                    dialog.dismiss()
                }
            }
            dialog.show()
        }
    }


    private fun validateCategoryName(): Boolean {

        categorieDAO = CategorieDAO(this)
        categorieDAO.open()

        val existe = categorieDAO.getCategorieByName(et_category_name.text.toString()) != null
        categorieDAO.close()

        if (et_category_name.text.toString() == "") {
            input_category_lay.error = getString(R.string.error_category_name)
            return false
        } else if (existe) {
            input_category_lay.error = getString(R.string.error_category_existing)
            return false
        } else if (couleur_position == -1) {
            input_category_lay.isErrorEnabled = false
            Toast.makeText(this, "Sélectionnez une couleur", Toast.LENGTH_SHORT).show()
            return false
        } else {
            input_category_lay.isErrorEnabled = false
            return true
        }
    }
}
