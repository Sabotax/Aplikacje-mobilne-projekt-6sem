package com.example.beeallrounder

import android.app.AlertDialog
import android.content.DialogInterface
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
import com.example.beeallrounder.data.Beehive_snapshot
import com.example.beeallrounder.data.UserDao
import com.example.beeallrounder.data.UserViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DBAddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DBAddFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mUserViewModel: UserViewModel

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
        val view = inflater.inflate(R.layout.fragment_d_b_add, container, false)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDBAddRecordSend = view.findViewById<Button>(R.id.btnDBAddRecordSend)
        btnDBAddRecordSend.setOnClickListener {
            //UserDao.addRecord(view.findViewById<EditText>(R.id.edittextDBAddRecordName).text.toString())
//            val text = "Hello toast!"
//            val duration = Toast.LENGTH_SHORT
//
//            val toast = Toast.makeText(requireContext(), text, duration)
//            toast.show()

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
            builder.setTitle("Potwierdzenie") // TODO stringi
            builder.setMessage("Czy na pewno chcesz dodać")
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                //Toast.makeText(requireContext(), android.R.string.yes, Toast.LENGTH_SHORT).show()
                val snapshot = Beehive_snapshot(0,date,hiveNumber,notes)
                mUserViewModel.addBeehive(snapshot)
                Toast.makeText(requireContext(),R.string.ToastSuccessfulyAddedSnapshot,Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_DBAddFragment_to_DBMainFragment)
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(requireContext(), android.R.string.no, Toast.LENGTH_SHORT).show()
            }
            builder.show()


        }
        else {
            Toast.makeText(requireContext(),"Nie przeszlo sprawdzenia (data,numer,notatki)",Toast.LENGTH_LONG).show()
        }

    }

    private fun inputCheck(date: String, hiveNumber : String, notes: String) : Boolean {
        return !(TextUtils.isEmpty(date) || TextUtils.isEmpty(hiveNumber) || TextUtils.isEmpty(date))
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DBAddTodayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DBAddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}