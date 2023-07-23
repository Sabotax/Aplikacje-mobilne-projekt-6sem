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
import com.example.beeallrounder.databases.oldDB.viewmodel.UserViewModelOld
import com.example.beeallrounder.fragments.toast


class CommLocalDownloadBle : Fragment(), AdapterView.OnItemSelectedListener, BLEControllerListener {
    private lateinit var mUserViewModel: UserViewModelOld

    private var bleController: BLEController? = null
    private val remoteControl: RemoteBLEDeviceController? = null

    private lateinit var bluetoothManager: BluetoothManager

    private lateinit var logScrollView: ScrollView
    private lateinit var logTextView: TextView

    private lateinit var btnScan: Button
    private lateinit var btnConnect: Button
    private lateinit var btnDisconnect: Button
    private lateinit var btnSynch: Button
    private lateinit var btnPomiar: Button
    private lateinit var inputDate: EditText

    private lateinit var btnSetOffset: Button
    private lateinit var editTextSetOffset: EditText
    private lateinit var btnSetScale: Button
    private lateinit var editTextSetScale: EditText
    private lateinit var btnSetSleepTime: Button
    private lateinit var editTextSetSleepTime: EditText

    private lateinit var spinnerDevice: Spinner
    private lateinit var spinnerSubmenu: Spinner
    private val submenusList = listOf("pomiarSynch","weightSet")

    private lateinit var viewSubmenuPomiarSynch: View
    private lateinit var viewSubmenuWeightSet: View

    object ButtonsController {
        var flag: Boolean = false
        var btnScan: Boolean = true
        var btnConnect: Boolean = false
        var btnDisconnect: Boolean = false
        var btnSynch: Boolean = false
        var btnPomiar: Boolean = false
        var spinner: Boolean = true
    }

    private val queueToLog: MutableList<String> = mutableListOf()

    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )
    private val PERMISSIONS_LOCATION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )

    //private val devicesList = mutableListOf<Pair<String,String>?>()
    private val devicesList = mutableListOf<RemoteBLEDeviceController>()
    private var currentSpinnerDeviceOption: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mUserViewModel = ViewModelProvider(this).get(UserViewModelOld::class.java)

//        val requestPermissionLauncher =
//            registerForActivityResult(
//                ActivityResultContracts.RequestPermission()
//            ) { isGranted: Boolean ->
//                if (isGranted) {
//                    // Permission is granted. Continue the action or workflow in your
//                    // app.
//                }
//            }

        return inflater.inflate(R.layout.fragment_comm_local_download_ble, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        logScrollView = view.findViewById(R.id.viewLog)
        logTextView = view.findViewById<TextView>(R.id.logViewCommLocalDownloadBle)

        inputDate = view.findViewById(R.id.inputDate)

        btnScan = view.findViewById<Button>(R.id.btnCommLocalDownloadBleScan)
        btnScan.setOnClickListener {
            scanResume()
        }

        btnConnect = view.findViewById<Button>(R.id.btnCommLocalDownloadBleConnect)
        btnConnect.setOnClickListener {
            if(currentSpinnerDeviceOption != null) {
                log("attempting connection to $currentSpinnerDeviceOption")
                val address = devicesList[currentSpinnerDeviceOption!!].deviceAddress
                if (address == null) log("err2, z jakiegoś powodu adres urządzenia to null")
                else
                    bleController?.connectToDevice(address)
            }
            else {
                log("Wiadomość nie powinna być widoczna, skontaktuj się z administratorem, err1")
            }


        }
        // TODO connect blokuje zmiany spinnera dopóki nie będzie disconnect

        btnDisconnect = view.findViewById<Button>(R.id.btnCommLocalDownloadBleDisconnect)
        btnDisconnect.setOnClickListener {
            log("disconeccted")
            bleController?.disconnect()
        }

        btnSynch = view.findViewById<Button>(R.id.btnCommLocalDownloadBleSynch)
        btnSynch.setOnClickListener {
            if(!inputDate.text.matches(Regex("^\\d{4}-\\d{1,2}-\\d{1,2}\$"))) {
                log("Zły format daty")
            }
            else {
                if(currentSpinnerDeviceOption != null) {
                    devicesList[currentSpinnerDeviceOption!!].sendData(
                        RemoteBLEDeviceController.Companion.INSTRUCTION_TYPE_SENDING.START_SENDING,
                        inputDate.text.toString()
                    )
                }
            }
        }

        btnPomiar = view.findViewById<Button>(R.id.btnCommLocalDownloadBlePomiar)
        btnPomiar.setOnClickListener {
            log("Wykonuję pomiar")
            if(currentSpinnerDeviceOption != null) {
                devicesList[currentSpinnerDeviceOption!!].sendData(
                    RemoteBLEDeviceController.Companion.INSTRUCTION_TYPE_SENDING.WYKONAJ_POMIAR,
                    ""
                )
            }
        }

        spinnerDevice = view.findViewById(R.id.spinnerDevice)
        spinnerSubmenu = view.findViewById(R.id.spinnerSubmenu)
        setSpinnerOptions(submenusList,spinnerSubmenu)

        viewSubmenuPomiarSynch = view.findViewById(R.id.viewBleSubmenuDatePomiar)
        viewSubmenuWeightSet = view.findViewById(R.id.viewBleSubmenuWeightSet)

        btnSetOffset = view.findViewById(R.id.btnCommLocalDownloadBleSetWeightOffset)
        editTextSetOffset = view.findViewById(R.id.editTextCommLocalDownloadBleSetWeightOffset)
        btnSetOffset.setOnClickListener {
            val valueToSet = editTextSetOffset.text.toString()
            log("Ustawianie offsetu wagi urządzenia na $valueToSet")
            devicesList[currentSpinnerDeviceOption!!].sendData(
                RemoteBLEDeviceController.Companion.INSTRUCTION_TYPE_SENDING.SET_OFFSET,
                valueToSet
            )
        }

        btnSetScale = view.findViewById(R.id.btnCommLocalDownloadBleSetWeightScale)
        editTextSetScale = view.findViewById(R.id.editTextCommLocalDownloadBleSetWeightScale)
        btnSetScale.setOnClickListener {
            val valueToSet = editTextSetScale.text.toString()
            log("Ustawianie skali wagi urządzenia na $valueToSet")
            devicesList[currentSpinnerDeviceOption!!].sendData(
                RemoteBLEDeviceController.Companion.INSTRUCTION_TYPE_SENDING.SET_SCALE,
                valueToSet
            )
        }

        Thread{
            //update UI base on backend thread input
            while(true) {
                activity?.runOnUiThread {
                    while (queueToLog.isNotEmpty()) {
                        val s = queueToLog.removeFirstOrNull()
                        if (s != null) activity?.runOnUiThread {
                            log(s)
                        }
                    }

                    if (ButtonsController.flag) {

                        btnScan.isEnabled = ButtonsController.btnScan
                        btnConnect.isEnabled = ButtonsController.btnConnect
                        btnDisconnect.isEnabled = ButtonsController.btnDisconnect
                        btnSynch.isEnabled = ButtonsController.btnSynch
                        btnPomiar.isEnabled = ButtonsController.btnPomiar
                        spinnerDevice.isEnabled = ButtonsController.spinner

                        ButtonsController.flag = false
                    }

                    while (devicesList.any { it.dataToLogQueue.isNotEmpty() }) {
                        devicesList.forEach {
                            log(it.dataToLogQueue.removeFirst())
                        }
                    }
                }
                    Thread.sleep(500)

            }
        }.start()

        checkBLESupport();
        checkPermissions();
    }

    fun setSpinnerOptions(list: List<String>, spinner: Spinner) {
        val adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,list)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //val item  = parent?.getItemAtPosition(position) as? String
        when(parent?.id) {
            spinnerDevice.id -> {
                currentSpinnerDeviceOption = position
            }
            spinnerSubmenu.id -> {
                when(position) {
                    0 -> {
                        viewSubmenuPomiarSynch.visibility = View.VISIBLE
                        viewSubmenuWeightSet.visibility = View.GONE
                    }
                    1 -> {
                        viewSubmenuPomiarSynch.visibility = View.GONE
                        viewSubmenuWeightSet.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        when(parent?.id) {
            spinnerDevice.id -> {
                currentSpinnerDeviceOption = null
                btnConnect.isEnabled = false
                ButtonsController.btnConnect = false
            }
            spinnerSubmenu.id -> {
                log("bb")
            }
        }
    }

//    private fun checkPermissions() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//            ||
//            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            log("\"Access Fine Location\" permission not granted yet!")
//            log("Whitout this permission Blutooth devices cannot be searched!")
//            ActivityCompat.requestPermissions(
//                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH_SCAN),
//                42
//            )
//        }
//    }

    private fun checkBLESupport() {
        // Check if BLE is supported on the device.
        if (!requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(requireContext(), "BLE not supported!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun log(s: String) {
        if(logTextView.text.length + s.length > 999) {
            requireContext().toast("log limit osiagniety")
            logTextView.text = "${logTextView.text.drop(s.length+1)}\n$s"
        }
        else
            logTextView.text = "${logTextView.text}\n$s"

        //logScrollView.scrollTo(0, logScrollView.bottom);
        logScrollView.fullScroll(View.FOCUS_DOWN);
    }

    override fun BLEControllerConnected() {
        fireLog("[BLE]\tConnected")
        ButtonsController.btnDisconnect = true
        ButtonsController.btnScan = false
        ButtonsController.btnSynch = true
        ButtonsController.btnPomiar = true
        ButtonsController.spinner = false
        ButtonsController.flag = true
    }

    override fun BLEControllerDisconnected(address: String) {
        fireLog("[BLE]\tDisconnected")

        val el = devicesList.find { it.deviceAddress == address }
        el?.threadFlag = false
        devicesList.remove(el)

        ButtonsController.btnScan = true
        ButtonsController.btnConnect = false
        ButtonsController.btnSynch = false
        ButtonsController.btnPomiar = false
        ButtonsController.spinner = true
        ButtonsController.flag = true
    }

    override fun BLEDeviceFound(name: String, address: String) {
        fireLog("Device $name found with address $address")
        devicesList.add(RemoteBLEDeviceController(name,address,this,bleController!!))
        setSpinnerOptions(devicesList.map { it.deviceName },spinnerDevice)
        ButtonsController.btnConnect = true
        ButtonsController.flag = true
    }

    override fun BLEIncomingData(data: ByteArray?, device: String?) {
        val device = devicesList.find { it.deviceAddress == device }
        if(device != null) {
            device.incomingDataQueue.add(data)
        }
    }

    override fun onResume() {
        super.onResume()
        scanResume()
    }

    private fun scanResume() {
        log("onResume");
        devicesList.clear()
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

    private fun checkPermissions() {
        val permission1 =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN)
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONS_STORAGE,
                1
            )
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONS_LOCATION,
                1
            )
        }
    }

    override fun fireLog(s: String) {
        queueToLog.add(s)
    }
}