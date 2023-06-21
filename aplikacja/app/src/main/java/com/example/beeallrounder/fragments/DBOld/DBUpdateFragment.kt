package com.example.beeallrounder.fragments.DBOld

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.beeallrounder.R
import com.example.beeallrounder.databases.oldDB.model.Beehive_snapshot
import com.example.beeallrounder.databases.oldDB.viewmodel.UserViewModelOld

class DBUpdateFragment : Fragment() {

    private val args by navArgs<DBUpdateFragmentArgs>()
    private lateinit var mUserViewModel: UserViewModelOld

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_d_b_update, container, false)

        val DBUpdateDateEdit = view.findViewById<EditText>(R.id.DBUpdateDateEdit)
        val DBUpdateMainNumberEdit = view.findViewById<EditText>(R.id.DBUpdateMainNumberEdit)
        val DBUpdateNotatkiEdit = view.findViewById<EditText>(R.id.DBUpdateNotatkiEdit)

        DBUpdateDateEdit.setText(args.currentSnapshot.date)
        DBUpdateMainNumberEdit.setText(args.currentSnapshot.hiveNumber.toString())
        DBUpdateNotatkiEdit.setText(args.currentSnapshot.notes)

        view.findViewById<Button>(R.id.btnDBUpdateRecordSend).setOnClickListener {
            updateItem()
        }
        mUserViewModel = ViewModelProvider(this).get(UserViewModelOld::class.java)

        // add menu
        setHasOptionsMenu(true)

        return view
    }

    private fun updateItem() {
        val date = requireView().findViewById<EditText>(R.id.DBUpdateDateEdit).text.toString()
        val hiveNumber_string = requireView().findViewById<EditText>(R.id.DBUpdateMainNumberEdit).text.toString()
        val notes = requireView().findViewById<EditText>(R.id.DBUpdateNotatkiEdit).text.toString()

        if (inputCheck(date,hiveNumber_string,notes) ) {
            val hiveNumber = hiveNumber_string.toInt()

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.Confirmation)
            builder.setMessage(R.string.AreYouSureUpdate)
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                //Toast.makeText(requireContext(), android.R.string.yes, Toast.LENGTH_SHORT).show()
                val updatedSnapshot = Beehive_snapshot(args.currentSnapshot.id,date,hiveNumber,notes)
                //update snapshot object
                mUserViewModel.updateRecord(updatedSnapshot)
                Toast.makeText(requireContext(),R.string.ToastSuccessfullyUpdated, Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_DBUpdateFragment_to_DBListFragment)
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(requireContext(), android.R.string.no, Toast.LENGTH_SHORT).show()
            }
            builder.show()


        }
        else {
            Toast.makeText(requireContext(),R.string.ToastDidntPassCheck, Toast.LENGTH_LONG).show()
        }
    }
    private fun inputCheck(date: String, hiveNumber : String, notes: String) : Boolean {
        return !(TextUtils.isEmpty(date) || TextUtils.isEmpty(hiveNumber) || TextUtils.isEmpty(date))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_delete) {
            deleteSnapshot()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteSnapshot() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(R.string.Yes) { _, _  ->
            mUserViewModel.deleteRecord(args.currentSnapshot)
            Toast.makeText(requireContext(), getString(R.string.ToastSuccessfullyRemoved) + args.currentSnapshot.hiveNumber,Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_DBUpdateFragment_to_DBListFragment)
        }
        builder.setNegativeButton(R.string.No) { _, _  ->
            //nothing
        }
        builder.setTitle( getString(R.string.ToastDeleteHive) + " ${args.currentSnapshot.hiveNumber}?")
        builder.setMessage( getString(R.string.ToastRemoveMsg)+" ${args.currentSnapshot.hiveNumber}?")
        builder.create().show()
    }
}