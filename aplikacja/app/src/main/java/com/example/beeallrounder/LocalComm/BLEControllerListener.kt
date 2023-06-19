package com.example.beeallrounder.LocalComm

interface BLEControllerListener {
    fun BLEControllerConnected()
    fun BLEControllerDisconnected()
    fun BLEDeviceFound(name: String, address: String)
    fun fireLog(s: String)
}