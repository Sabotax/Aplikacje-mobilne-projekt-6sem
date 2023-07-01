package com.example.beeallrounder.fragments.DBOld

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.example.beeallrounder.R

class DBMainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_d_b_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDBMainToDBList = view.findViewById<Button>(R.id.btnDBMainToDBList)
        btnDBMainToDBList.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_DBMainFragment_to_DBListFragment)
        }

        val btnDBMainToDBWykres = view.findViewById<Button>(R.id.btnDBMainToDBWykres)
        btnDBMainToDBWykres.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_DBMainFragment_to_DBWykres)
        }
    }
}