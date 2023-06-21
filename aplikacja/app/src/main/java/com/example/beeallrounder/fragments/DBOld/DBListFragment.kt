package com.example.beeallrounder.fragments.DBOld

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeallrounder.R
import com.example.beeallrounder.data.oldDB.viewmodel.UserViewModelOld
import com.example.beeallrounder.list.ListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DBListFragment : Fragment() {

    private lateinit var mUserViewModelOld : UserViewModelOld


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
        mUserViewModelOld = ViewModelProvider(this).get(UserViewModelOld::class.java)
        mUserViewModelOld.readAllData.observe(viewLifecycleOwner, Observer {
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
            mUserViewModelOld.deleteAllRecords()
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