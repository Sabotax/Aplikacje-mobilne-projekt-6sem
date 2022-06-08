package com.example.beeallrounder.LocalComm

import android.util.Log
import com.example.beeallrounder.fragments.CommLocalDownloadFragment
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ConnectException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class TcpClient private constructor() {
    companion object {
        private const val SERVER_IP = "192.168.0.1"
        private const val SERVER_PORT = 80

        fun DownloadAll() : Array<Any?> {
            // java.net.SocketException: connection reset pid 6817 - gdy zle wifi
            // java.net.ConnectException: failed to connect ... - gdy nie ma wifi
            try {
                val client = Socket(SERVER_IP, SERVER_PORT)
                val output = PrintWriter(client.getOutputStream(), true)
                val input = BufferedReader(InputStreamReader(client.inputStream))
                //TODO(zapisywanie tej wiadomosci do pliku i na Toast)

                var returning_msg:String? = null

                Log.d("TcpClient debugging","Client sending /CommDownloadAll")
                output.println("GET /CommDownloadAll HTTP/1.1  \r\n")
                input.forEachLine {
                    //Log.d("TcpClient dbg receive3", it)
                    if (it.contains("MSG:") ) {
                        returning_msg = it
                    }
                }

                client.close()

                return arrayOf(true as Any,returning_msg as Any)
            }
            catch ( e: SocketException) {
                // TODO(jak nie ma wifi to też łapie to a nie .connectExcp)
                Log.e("TcpClient Error","Nie znaleziono hosta o adresie $SERVER_IP:$SERVER_PORT")
            }
            catch (e: ConnectException) {
                Log.e("TcpClient Error", "Nie ma wifi")
            }
            return arrayOf(false as Any,null)

        }



        private fun prepare_packet_content(address: String) : String {
            return """
            GET /$address HTTP/1.1  \r\n
            Host: $SERVER_IP \r\n
            Connection: keep-alive \r\n
            Cache-Control: max-age=0 \r\n
            Upgrade-Insecure-Requests: 1 \r\n
            User-Agent: Mozilla/5.0 (Linux; Android 11; M2101K7BNY) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.61 Mobile Safari/537.36 \r\n
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9 \r\n
            Accept-Encoding: gzip, deflate \r\n
            Accept-Language: pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7 \r\n
            """
        }


    }
}