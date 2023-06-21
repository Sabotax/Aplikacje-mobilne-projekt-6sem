package com.example.beeallrounder.fragments.DBOld

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
import com.example.beeallrounder.R
import com.example.beeallrounder.databases.oldDB.model.Beehive_snapshot
import com.example.beeallrounder.databases.oldDB.viewmodel.UserViewModelOld

class DBAddFragment : Fragment() {
    private lateinit var mUserViewModel: UserViewModelOld

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_d_b_add, container, false)

        mUserViewModel = ViewModelProvider(this).get(UserViewModelOld::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDBAddRecordSend = view.findViewById<Button>(R.id.btnDBAddRecordSend)
        btnDBAddRecordSend.setOnClickListener {
            insertDataToDatabase()
        }
    }

    private fun insertDataToDatabase() {
        val date = requireView().findViewById<EditText>(R.id.DBAddDateEdit).text.toString()
        val hiveNumber_string = requireView().findViewById<EditText>(R.id.DBAddMainNumberEdit).text.toString()
        val notes = requireView().findViewById<EditText>(R.id.DBAddNotatkiEdit).text.toString()

        if (inputCheck(date,hiveNumber_string,notes) ) {
            val hiveNumber = hiveNumber_string.toInt()

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.Confirmation)
            builder.setMessage(R.string.Areyousure)

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                val snapshot = Beehive_snapshot(0,date,hiveNumber,notes)
                mUserViewModel.addBeehive(snapshot)
                Toast.makeText(requireContext(),R.string.ToastSuccessfulyAddedSnapshot,Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_DBAddFragment_to_DBListFragment)
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(requireContext(), android.R.string.no, Toast.LENGTH_SHORT).show()
            }
            builder.show()


        }
        else {
            Toast.makeText(requireContext(),R.string.ToastDidntPassCheck,Toast.LENGTH_LONG).show()
        }

    }

    private fun inputCheck(date: String, hiveNumber : String, notes: String) : Boolean {
        return !(TextUtils.isEmpty(date) || TextUtils.isEmpty(hiveNumber) || TextUtils.isEmpty(date))
    }
}