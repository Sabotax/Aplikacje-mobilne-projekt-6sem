package com.example.beeallrounder.LocalComm

import android.bluetooth.*
import android.bluetooth.BluetoothProfile.GATT
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.util.*
import kotlin.math.log


class BLEController {
    companion object {
        private var instance: BLEController? = null

        fun getInstance(ctx: Context): BLEController? {
            if (null == instance) instance = BLEController(ctx)
            return instance
        }
    }

    private var scanner: BluetoothLeScanner? = null
    private var device: BluetoothDevice? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothManager: BluetoothManager? = null

    private var btGattChar: BluetoothGattCharacteristic? = null

    private val listeners: ArrayList<BLEControllerListener> = ArrayList()
    private val devices: HashMap<String, BluetoothDevice> = HashMap()

    constructor(ctx: Context) {
        bluetoothManager = ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
    }

    fun addBLEControllerListener(l: BLEControllerListener) {
        if (!listeners.contains(l)) listeners.add(l)
    }

    fun removeBLEControllerListener(l: BLEControllerListener) {
        listeners.remove(l)
    }

    fun init() {
        devices.clear()
        scanner = bluetoothManager!!.adapter.bluetoothLeScanner // tu jest null
        fireLog( "skanuje")
        if(scanner == null) fireLog("scanner jest null")
        scanner!!.startScan(bleCallback)
    }

    private val bleCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device: BluetoothDevice = result.getDevice()
            //Log.d("Scan result", device.name ?: "null")
            if (!devices.containsKey(device.address) && isThisTheDevice(device)) {
                deviceFound(device)
            }
        }

        override fun onBatchScanResults(results: List<ScanResult?>) {
            for (sr in results) {
                val device: BluetoothDevice? = sr?.getDevice()
                if (device!=null && !devices.containsKey(device.address) && isThisTheDevice(device)) {
                    deviceFound(device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.i("[BLE]", "scan failed with errorcode: $errorCode")
        }
    }

    private fun isThisTheDevice(device: BluetoothDevice): Boolean {
        return null != device.name && device.name.startsWith("ESP")
    }

    private fun deviceFound(device: BluetoothDevice) {
        devices[device.address] = device
        fireDeviceFound(device)
    }

    fun connectToDevice(address: String) {
        device = devices[address]
        scanner!!.stopScan(bleCallback)
        fireLog( "connect to device " + device!!.address)
        bluetoothGatt = device!!.connectGatt(null, false, bleConnectCallback)
    }

    private val bleConnectCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("[BLE]", "start service discovery " + bluetoothGatt!!.discoverServices())
                // tymczasowo tutaj:
                fireConnected()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                btGattChar = null
                Log.w("[BLE]", "DISCONNECTED with status $status")
                fireDisconnected()
            } else {
                Log.i("[BLE]", "unknown state $newState and status $status")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)

            if(/*characteristic?.uuid.toString().uppercase() == "X"*/true) {
                fireIncomingData(characteristic?.value,device?.address)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (null == btGattChar) {
                for (service in gatt.services) {
                    if (/*service.uuid.toString().uppercase(Locale.getDefault()).startsWith("0000FFE0")*/true) {
                        val gattCharacteristics = service.characteristics
                        fireLog("service ${service.uuid}")
                        for (bgc in gattCharacteristics) {
                            if (/*bgc.uuid.toString().uppercase(Locale.getDefault()).startsWith("0000FFE1")*/true) {
                                fireLog("bgc ${service.uuid}")
                                val chprop = bgc.properties
                                //onCharacteristicChanged(gatt,bgc) pzrykładowe wywołanie,
                                // w realu muszę zrobić żeby były dane o tej pożądanej (TX) charakterystyce i żeby w callbacku porównywało, czy
                                // to ta się zmieniła co miała
                                if (chprop and BluetoothGattCharacteristic.PROPERTY_WRITE or (chprop and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                                    btGattChar = bgc
                                    Log.i("[BLE]", "CONNECTED and ready to send")
                                    sendData(ByteArray(255))
                                    fireConnected()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fireDisconnected() {
        for (l in listeners) l.BLEControllerDisconnected()
        device = null
    }

    private fun fireLog(s: String) {
        for (l in listeners) l.fireLog(s)
    }

    private fun fireConnected() {
        for (l in listeners) l.BLEControllerConnected()
    }

    private fun fireDeviceFound(device: BluetoothDevice) {
        for (l in listeners) l.BLEDeviceFound(device.name.trim { it <= ' ' }, device.address)
    }

    private fun fireIncomingData(data: ByteArray?,deviceAddress: String?) {
        for (l in listeners) l.BLEIncomingData(data,deviceAddress)
    }

    fun sendData(data: ByteArray) {
        btGattChar!!.value = data
        bluetoothGatt!!.writeCharacteristic(btGattChar)
    }

    fun checkConnectedState(): Boolean {
        return bluetoothManager!!.getConnectionState(device, GATT) == 2
    }

    fun disconnect() {
        bluetoothGatt!!.disconnect()
    }
}