package com.example.beeallrounder.LocalComm

import android.bluetooth.*
import android.bluetooth.BluetoothProfile.GATT
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import java.util.*


class BLEController {
    companion object {
        private var instance: BLEController? = null

        fun getInstance(ctx: Context): BLEController? {
            if (null == instance) instance = BLEController(ctx)
            return instance
        }

        const val TX_UUID = "391150c1-8fe8-47f3-b045-9be85e40683b"
        const val RX_UUID = "453a0b55-0417-4ee6-b458-83d3ff6fe054"
        const val SERVICE_UUID = "a9a5d9db-1d47-44a6-84cb-a3d00cf2a25f"
    }

    private var scanner: BluetoothLeScanner? = null
    private var device: BluetoothDevice? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothManager: BluetoothManager? = null

    private var btGattCharTX: BluetoothGattCharacteristic? = null
    private var btGattCharRX: BluetoothGattCharacteristic? = null

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
                btGattCharTX = null
                btGattCharRX = null
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

            if(characteristic?.uuid.toString() == TX_UUID) {
                // działa !!!!!!!!! characteristic.value.toString(Charsets.US_ASCII)
                fireIncomingData(characteristic?.value,device?.address)
                // TODO protokół BLE przesyła max 20 bajtów na raz
                // epoch Time (long) można zmieścić w 4 bajtach (do 2038 roku przynajmniej)
                // następnie id bierzemy z urządzenia itp
                // waga może być zmieszczona w uint8_t,uint8_t
                // rozkaz tak samo, starczy jeden uint8_t
                // zostaje użyte w ten sposób 7 bajtów, zamiast niemieszczenia się wczesniej stringa
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            var byla_potrzeba_przypisania = false

            var TX_przypisano = false
            if(btGattCharTX == null) {
                byla_potrzeba_przypisania = true
                for (service in gatt.services) {
                    if (service.uuid.toString() == SERVICE_UUID) {
                        for (bgc in service.characteristics) {
                            if(bgc.uuid.toString() == TX_UUID) {
                                btGattCharTX = bgc
                                TX_przypisano = true
                                fireLog("Charakterystyka TX znaleziona")

                                bluetoothGatt?.setCharacteristicNotification(btGattCharTX,true)
                            }
                        }
                    }
                }
            }

            var RX_przypisano = false
            if(btGattCharRX == null) {
                byla_potrzeba_przypisania = true
                for (service in gatt.services) {
                    if (service.uuid.toString() == SERVICE_UUID) {
                        for (bgc in service.characteristics) {
                            if(bgc.uuid.toString() == RX_UUID) {
                                btGattCharRX = bgc
                                RX_przypisano = true
                                fireLog("Charakterystyka RX znaleziona")
                            }
                        }
                    }
                }
            }

            if(TX_przypisano && RX_przypisano && byla_potrzeba_przypisania) {
                fireConnected()
            }
        }
    }

    private fun fireDisconnected() {
        for (l in listeners) l.BLEControllerDisconnected(device!!.address)
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
        btGattCharRX!!.value = data
        bluetoothGatt!!.writeCharacteristic(btGattCharRX)
    }

    fun checkConnectedState(): Boolean {
        return bluetoothManager!!.getConnectionState(device, GATT) == 2
    }

    fun disconnect() {
        bluetoothGatt!!.disconnect()
    }
}