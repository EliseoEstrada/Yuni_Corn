package com.fcfm.yuni_corn.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.R
import kotlinx.android.synthetic.main.fragment_homeworks.*

class FragmentHomeworks: Fragment(R.layout.fragment_homeworks)  {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView18.text = getArguments()?.getString("open_from") ?: ""
    }
}