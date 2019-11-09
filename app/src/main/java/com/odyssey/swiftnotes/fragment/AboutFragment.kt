package com.odyssey.swiftnotes.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.odyssey.swiftnotes.R

/**
 * Created by ZskHenriJ on 30/09/2016.
 */
class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstance: Bundle?): View? {
        return inflater!!.inflate(R.layout.frag_about, container, false)
    }
}
