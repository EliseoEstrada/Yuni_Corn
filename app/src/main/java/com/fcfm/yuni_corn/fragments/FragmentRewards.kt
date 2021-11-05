package com.fcfm.yuni_corn.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.adapters.GroupsAdapter
import com.fcfm.yuni_corn.adapters.HomeworksAdapter
import com.fcfm.yuni_corn.adapters.RewardsAdapter
import com.fcfm.yuni_corn.utils.Globals
import kotlinx.android.synthetic.main.fragment_homeworks.*
import kotlinx.android.synthetic.main.fragment_rewards.*

class FragmentRewards: Fragment(R.layout.fragment_rewards) {

    lateinit var  adapter: RewardsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RewardsAdapter(Globals.listRewardsUser)
        rv_rewardsContainer_fr.adapter = adapter
    }
}