package com.example.beeallrounder.fragments.Comm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.example.beeallrounder.R

class CommMainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comm_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCommMainToLocalDownload = view.findViewById<Button>(R.id.btnCommMainToCommLocalDownload)
        btnCommMainToLocalDownload.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_commMainFragment_to_commLocalDownloadFragment)
        }

        val btnCommMainToLocalDownloadBle = view.findViewById<Button>(R.id.btnCommMainToCommLocalDownloadBle)
        btnCommMainToLocalDownloadBle.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_commMainFragment_to_commLocalDownloadBle)
        }
    }

}