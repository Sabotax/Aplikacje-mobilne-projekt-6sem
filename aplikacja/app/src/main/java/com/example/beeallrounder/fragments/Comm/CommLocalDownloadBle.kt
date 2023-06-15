package com.example.beeallrounder.fragments.Comm

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.beeallrounder.LocalComm.BLEController
import com.example.beeallrounder.LocalComm.BLEControllerListener
import com.example.beeallrounder.LocalComm.RemoteBLEDeviceController
import com.example.beeallrounder.R
import com.example.beeallrounder.data.viewmodel.UserViewModel


class CommLocalDownloadBle : Fragment(), AdapterView.OnItemSelectedListener, BLEControllerListener {
    private lateinit var mUserViewModel: UserViewModel

    private var bleController: BLEController? = null
    private val remoteControl: RemoteBLEDeviceController? = null
    private var deviceAddress: String? = null

    private lateinit var bluetoothManager: BluetoothManager

    private lateinit var logView: TextView
    private lateinit var btnScan: Button
    private lateinit var btnConnect: Button
    private lateinit var btnDisconnect: Button
    private lateinit var btnSynch: Button
    private lateinit var spinner: Spinner

    private var devicesList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        return inflater.inflate(R.layout.fragment_comm_local_download_ble, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        logView = view.findViewById<TextView>(R.id.logViewCommLocalDownloadBle)

        btnScan = view.findViewById<Button>(R.id.btnCommLocalDownloadBleScan)
        btnScan.setOnClickListener {
            bleController?.init()
            Toast.makeText(requireContext(),"scan",Toast.LENGTH_LONG).show()
        }

        btnConnect = view.findViewById<Button>(R.id.btnCommLocalDownloadBleConnect)
        btnConnect.setOnClickListener {
            Toast.makeText(requireContext(),"connect",Toast.LENGTH_LONG).show()
        }

        btnDisconnect = view.findViewById<Button>(R.id.btnCommLocalDownloadBleDisconnect)
        btnDisconnect.setOnClickListener {
            Toast.makeText(requireContext(),"disconnect",Toast.LENGTH_LONG).show()
            log("disconeccted")
        }

        btnSynch = view.findViewById<Button>(R.id.btnCommLocalDownloadBleSynch)
        btnSynch.setOnClickListener {
            Toast.makeText(requireContext(),"synch",Toast.LENGTH_LONG).show()
            log("synch")
        }

        spinner = view.findViewById(R.id.spinner)
        setSpinnerOptions(listOf("Twoja stara","Twój stary"))

        checkBLESupport();
        checkPermissions();


    }

    fun setSpinnerOptions(list: List<String>) {
        val adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,list)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item  = parent?.getItemAtPosition(position) as? String
        Toast.makeText(requireContext(),"$item",Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED
        ) {
            log("\"Access Fine Location\" permission not granted yet!")
            log("Whitout this permission Blutooth devices cannot be searched!")
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH_SCAN),
                42
            )
        }
    }

    private fun checkBLESupport() {
        // Check if BLE is supported on the device.
        if (!requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(requireContext(), "BLE not supported!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun log(s: String) {
        logView.text = "${logView.text}\n$s"
    }

    override fun BLEControllerConnected() {
        log("[BLE]\tConnected")
        btnDisconnect.isEnabled = true
        //switchLEDButton.setEnabled(true)
    }

    override fun BLEControllerDisconnected() {
        log("[BLE]\tDisconnected")
        btnScan.isEnabled = true
        btnConnect.isEnabled = false
    }

    override fun BLEDeviceFound(name: String, address: String) {
        log("Device $name found with address $address")
        deviceAddress = address
        devicesList.add(name) // todo pamiętać o usuwaniu tych których już z nami nie ma RIP
        setSpinnerOptions(devicesList)
        btnConnect.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        log("onResume");
        this.deviceAddress = null;
        this.bleController = BLEController.getInstance(requireContext());
        this.bleController?.addBLEControllerListener(this);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            log("[BLE]\tSearching for BlueCArd...");
            this.bleController?.init();
        }
    }

    override fun onStart() {
        super.onStart()

        if (!bluetoothManager.adapter.isEnabled) {
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requireActivity().startActivity(enableBTIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        this.bleController?.removeBLEControllerListener(this);
    }
}