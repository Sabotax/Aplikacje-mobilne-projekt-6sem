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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DBListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DBListFragment : Fragment() {

    private lateinit var mUserViewModel : UserViewModel
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DBListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DBListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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
        builder.setPositiveButton("Yes") { _, _  ->
            mUserViewModel.deleteAllRecords()
            Toast.makeText(requireContext(),"Successfully removed all records",
                Toast.LENGTH_LONG).show()

        }
        builder.setNegativeButton("No") { _, _  ->
            //nothing
        }
        builder.setTitle("Delete all records?")
        builder.setMessage("Are you sure you want to delete all records?")
        builder.create().show()
    }
}