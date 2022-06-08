package com.example.beeallrounder.fragments.DB

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.beeallrounder.R
import com.example.beeallrounder.data.model.Beehive_snapshot
import com.example.beeallrounder.data.viewmodel.UserViewModel

class DBUpdateFragment : Fragment() {

    private val args by navArgs<DBUpdateFragmentArgs>()
    private lateinit var mUserViewModel: UserViewModel

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
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        return view
    }

    private fun updateItem() {
        val date = requireView().findViewById<EditText>(R.id.DBUpdateDateEdit).text.toString()
        val hiveNumber_string = requireView().findViewById<EditText>(R.id.DBUpdateMainNumberEdit).text.toString()
        val notes = requireView().findViewById<EditText>(R.id.DBUpdateNotatkiEdit).text.toString()

        if (inputCheck(date,hiveNumber_string,notes) ) {
            val hiveNumber = hiveNumber_string.toInt()

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Potwierdzenie") // TODO stringi
            builder.setMessage("Czy na pewno chcesz dodać")
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                //Toast.makeText(requireContext(), android.R.string.yes, Toast.LENGTH_SHORT).show()
                val updatedSnapshot = Beehive_snapshot(args.currentSnapshot.id,date,hiveNumber,notes)
                //update snapshot object
                mUserViewModel.updateRecord(updatedSnapshot)
                Toast.makeText(requireContext(),R.string.ToastSuccessfulyAddedSnapshot, Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_DBUpdateFragment_to_DBListFragment)
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(requireContext(), android.R.string.no, Toast.LENGTH_SHORT).show()
            }
            builder.show()


        }
        else {
            Toast.makeText(requireContext(),"Nie przeszlo sprawdzenia (data,numer,notatki)", Toast.LENGTH_LONG).show()
        }
    }
    private fun inputCheck(date: String, hiveNumber : String, notes: String) : Boolean {
        return !(TextUtils.isEmpty(date) || TextUtils.isEmpty(hiveNumber) || TextUtils.isEmpty(date))
    }

}