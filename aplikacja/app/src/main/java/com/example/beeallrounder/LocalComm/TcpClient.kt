package com.example.beeallrounder.LocalComm

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class TcpClient private constructor() {
    companion object {
        private const val SERVER_IP = "192.168.0.1"
        private const val SERVER_PORT = 80

        fun client() {
            val client = Socket(SERVER_IP, SERVER_PORT)
            val output = PrintWriter(client.getOutputStream(), true)
            val input = BufferedReader(InputStreamReader(client.inputStream))

            //TODO(throw some try-catch (nie ma wifi, nie ma urzadzenia itp))
            //TODO(esp przerobic zeby wysylalo millis() i je pokazywalo w Toast i zapisywalo do pliku i gitara)

            Log.d("TcpClient debugging","Client sending [Hello]")
            output.println("Hello")
            Log.d("TcpClient dbg receive","Client receiving [${input.readLine()}]")
            client.close()
        }
    }
}