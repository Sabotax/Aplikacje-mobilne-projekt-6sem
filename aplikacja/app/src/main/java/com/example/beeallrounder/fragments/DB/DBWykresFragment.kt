package com.example.beeallrounder.fragments.DB

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.beeallrounder.R
import com.example.beeallrounder.databases.dbEspSynch.viewmodel.UserViewModel
import com.example.beeallrounder.fragments.toast
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import java.util.logging.Logger


class DBWykresFragment : Fragment(),AdapterView.OnItemSelectedListener {

    lateinit var graph: GraphView
    lateinit var editTextDateFrom: EditText
    lateinit var editTextDateTo: EditText
    lateinit var btnPlot: Button
    lateinit var spinner: Spinner

    private var currentSpinnerOption: Int? = null
    private var spinnerOptions: List<String> = listOf()

    lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_d_b_wykres, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graph = view.findViewById(R.id.DBWykresPlot)

        editTextDateFrom = view.findViewById(R.id.DBWykresInputDateFrom)
        editTextDateTo = view.findViewById(R.id.DBWykresInputDateTo)
        btnPlot = view.findViewById(R.id.DBWykresBtnShow)
        spinner = view.findViewById(R.id.DBWykresIDSpinner)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        val spinnerObserver = Observer<List<String>> { list ->
            spinnerOptions = list
            setSpinnerOptions()
        }
        mUserViewModel.getEspList().observe(viewLifecycleOwner,spinnerObserver)

        btnPlot.setOnClickListener {
            val dateFrom = editTextDateFrom.text.toString()
            val dateTo = editTextDateTo.text.toString()

            val regexDate = Regex("^\\d{4}-\\d{1,2}-\\d{1,2}\$")
            if(dateFrom.matches(regexDate) && dateTo.matches(regexDate)) {
                val epochFrom = getSecondsFromEpoch(dateFrom)
                val epochTo = getSecondsFromEpoch(dateTo)
                val espId = if(currentSpinnerOption != null) spinnerOptions[currentSpinnerOption!!] else {
                    requireContext().toast("Nie wybrano urządzenia")
                    return@setOnClickListener
                }

                mUserViewModel.readBetween(espId,epochFrom,epochTo).observe(viewLifecycleOwner) { response ->
                    val response = response.sortedBy { it.timestampEsp }

                    val series: LineGraphSeries<DataPoint> = LineGraphSeries(
                        response.mapNotNull {
                            if(it.timestampEsp == null || it.waga == null) {
                                Log.w("Wykres","wartosc nullowa w rekordzie ${it.id}, pomijam")
                                null
                            }
                            else
                                DataPoint(Date(it.timestampEsp),it.waga)
                        }.toTypedArray()
                    )
                    graph.addSeries(series)
                }
            }
            else {
                requireContext().toast("Zły format daty")
                return@setOnClickListener
            }

        }

    }

    fun getSecondsFromEpoch(dateString: String): Long {
        val date = LocalDate.parse(dateString)
        return date.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
    }

    fun setSpinnerOptions() {
        val adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,spinnerOptions)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentSpinnerOption = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        currentSpinnerOption = null
    }
}
