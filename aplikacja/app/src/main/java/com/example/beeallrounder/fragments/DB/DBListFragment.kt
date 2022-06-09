package com.example.beeallrounder.fragments.DB

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeallrounder.R
import com.example.beeallrounder.data.viewmodel.UserViewModel
import com.example.beeallrounder.list.ListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DBListFragment : Fragment() {

    private lateinit var mUserViewModel : UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_d_b_list, container, false)

        //recycler view
        val adapter = ListAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // user view model
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mUserViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            user -> adapter.setData(user)
       })

        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDBListToDBAdd = view.findViewById<FloatingActionButton>(R.id.btnDBListToDBAdd)
        btnDBListToDBAdd.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_DBListFragment_to_DBAddFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_delete) {
            deleteAllRecords()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllRecords() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(R.string.Yes) { _, _  ->
            mUserViewModel.deleteAllRecords()
            Toast.makeText(requireContext(),R.string.SuccessfulyRemovedAll,
                Toast.LENGTH_LONG).show()

        }
        builder.setNegativeButton(R.string.No) { _, _  ->
            //nothing
        }
        builder.setTitle(R.string.DeleteAllRecords)
        builder.setMessage(R.string.AreYouSureAll)
        builder.create().show()
    }
}