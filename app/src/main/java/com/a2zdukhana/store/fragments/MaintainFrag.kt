package com.a2zdukhana.store.fragments

import com.a2zdukhana.store.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


@Suppress("DEPRECATION")
class MaintainFrag : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.maintainance_frag, null)
        return v
    }
}