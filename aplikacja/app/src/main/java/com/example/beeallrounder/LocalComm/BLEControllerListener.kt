package com.example.beeallrounder.LocalComm

interface BLEControllerListener {
    fun BLEControllerConnected()
    fun BLEControllerDisconnected(address: String)
    fun BLEDeviceFound(name: String, address: String)
    fun BLEIncomingData(data: ByteArray?,device: String?)
    fun fireLog(s: String)
}