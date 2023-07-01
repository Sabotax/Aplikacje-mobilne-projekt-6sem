package com.example.beeallrounder.LocalComm

import android.util.Log
import androidx.core.os.persistableBundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.beeallrounder.databases.dbEspSynch.model.SensorRecord
import com.example.beeallrounder.databases.dbEspSynch.viewmodel.UserViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId

class RemoteBLEDeviceController(
    val deviceName: String,
    val deviceAddress: String,
    private val app: ViewModelStoreOwner,
    val bleController: BLEController
) {
    // nieaktualne, v1
    //ramka komunikacji (BLE ma max 512 bajtów): 6 bajtów na adres? | 1 bajt na typ instrukcji |
    // zostaje 505 na długość i wiadomość
    // czyli 4040 bitów
    // żeby zmieścic informację o długości wiadomości gdy liczba bitów przekracza 2048, to potrzeba 12 bitów

    // ergo ramka:
    // 48 bitów adresu urządzenia| 8 bitów typu instrukcji | 12 bitów mówiących o długości wiadomości | 4028 bitów na wiadomość
    // jeden char ma 8 bitów, czyli starczy na 503 znaki
    // oczywiście można o wiele bardziej zaosczędzić miejsce używając od razu bitów w odpowiednich pozycjach na informacje, jednakże przesyłanie stringów
    // chyba będzie okej


    // v2
    // 1 bajt na typ instrukcji | reszta bajtów na dane
    // w końcu rzeczy takie jak adres są i tak znane, a i wielkość wiadomości też jest ogarniana w pętli po prostu, że dopisuje do char[] póki ma co dopisywać

    // więc przykładowa komunikacja:
    // telefon: 0001 | 21062023 oznacza rozpocznij wysyłanie od 21 czerwca 2023 roku
    // esp: 0002 (nie musi być ten sam) | dane (np. esp01;20.13;314.13) itp oznacza że wysyła i dane
    // telefon: 0002 | -- : oznacza kontynuuj, dotarło
    // itp
    // w końcu esp wysyła 0003 | -- : oznacza: koniec wysyłania

    // edit w sumie lepiej jakby tylko dzień był wysyłany i esp by po prostu brało dla tego dnia albo zwracało jeśli nie znajdzie pliku odpowiednią wiadomość
    // a na telefonie by był wybierany zakres itp i telefon dzień po dniu by brał, to by też pomogło śledzić progress i odporne na wyjebanie w trakcie bardziej by było?

    // od każdego urządzenia powinien być tworzony osobny obiekt a powinny być w fragmencie BLE przechowywane

    companion object {
        enum class INSTRUCTION_TYPE_SENDING(val value: String) {
            START_SENDING("1"),
            CONTINUE("2"),
            WYKONAJ_POMIAR("4"),
        }

        enum class INSTRUCTION_TYPE_RECEIVING(val value: Int) {
            HAVE_STARTED_SENDING(1), // wysyłam dane
            AM_CONTINUING_SENDING(2), // wysyłam dane
            STOP(3), // koniec wysyłania
            ERROR(5), // wydarzył się błąd w ESP
            POMIAR(4), // jednorazowy pomiar
            STOP_EMPTY(6)
        }
    }

    val incomingDataQueue: MutableList<ByteArray?> = mutableListOf()
    var threadFlag = true

    val dataToLogQueue: MutableList<String> = mutableListOf()

    private var mUserViewModel: UserViewModel? = null
    //mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java) żeby rozpocząć

    init{
        executeReceiving()
    }

    private fun dataToSensorRecord(decodedData: ReceivedMsg): SensorRecord {
        return SensorRecord(
            espId = deviceName,
            waga = decodedData.msgWaga,
            timestampEsp = decodedData.msgTime
        )
    }

    private fun addRow(row: SensorRecord) {
        if(mUserViewModel == null) {
            mUserViewModel = ViewModelProvider(app).get(UserViewModel::class.java)
        }
        mUserViewModel!!.addSensorRecord(row)
    }

    fun decodeMsgToData(msg: ByteArray): ReceivedMsg? {
        val rozkaz = msg[0].toInt()
        when(rozkaz) {
            2,3 -> {
                val calkowite = msg[1].toInt()
                val ulamki = msg[2].toInt()
                val waga = "$calkowite.$ulamki".toDoubleOrNull()

                val epochSeconds: Long =
                    ((msg[6].toLong() and 0xFF) shl 24) or
                            ((msg[5].toLong() and 0xFF) shl 16) or
                            ((msg[4].toLong() and 0xFF) shl 8) or
                            (msg[3].toLong() and 0xFF)

                //val epochTime = Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime()

                return ReceivedMsg(
                    enumValues<INSTRUCTION_TYPE_RECEIVING>().find { it.value == rozkaz },
                    null,
                    waga,
                    epochSeconds
                )
            }
            1,4,5,6 -> {
                return ReceivedMsg(
                    enumValues<INSTRUCTION_TYPE_RECEIVING>().find { it.value == rozkaz },
                    if(msg.size>1) msg.copyOfRange(1,msg.size-1).toString(Charsets.US_ASCII) else "",
                    null,
                    null
                )
            }
            else -> {
                dataToLogQueue.add("Nie rozpoznano odebranego rozkazu i danych")
                return null
            }
        }
    }

    fun executeReceiving() {
        Thread {
            // czy thread się zamknie gdy obiekt zostanie usunięty?
            // dla pewności threadFlag ustawić na false w razie gdy po usunięciu z listy(czyli rozłączeniu) obiekt by wciąż żył i thread chodził
            while(threadFlag) {
                if(incomingDataQueue.isNotEmpty()) {
                    val dataItem = incomingDataQueue.removeFirst()
                    if(dataItem == null) {
                        //TODO nima danych error
                    }
                    else {
                        val message = decodeMsgToData(dataItem)

                        if(message != null)
                            when (message.instruction) {
                                INSTRUCTION_TYPE_RECEIVING.HAVE_STARTED_SENDING -> {
                                    sendData(INSTRUCTION_TYPE_SENDING.CONTINUE,"")
                                }
                                INSTRUCTION_TYPE_RECEIVING.AM_CONTINUING_SENDING -> {
                                    val row = dataToSensorRecord(message)
                                    addRow(row)

                                    // send continue
                                    sendData(INSTRUCTION_TYPE_SENDING.CONTINUE,"")
                                }
                                INSTRUCTION_TYPE_RECEIVING.STOP -> {
                                    val row = dataToSensorRecord(message)
                                    addRow(row)
                                    dataToLogQueue.add("Urządzenie $deviceName skończyło przesyłanie dnia")
                                }
                                INSTRUCTION_TYPE_RECEIVING.ERROR -> {
                                    dataToLogQueue.add("Wystąpił błąd na esp ")
                                }
                                INSTRUCTION_TYPE_RECEIVING.POMIAR -> {
                                    dataToLogQueue.add("Obecna waga na urządzeniu $deviceName wynosi ${message.msgString}")
                                }
                                INSTRUCTION_TYPE_RECEIVING.STOP_EMPTY -> {
                                    dataToLogQueue.add("Urządzenie $deviceName skończyło przesyłanie dnia")
                                }
                                else -> {
                                    dataToLogQueue.add("Instrukcja nierozpoznana ")
                                }
                            }
                    }
                }
                Thread.sleep(100)
            }
        }.start()
    }


    fun prepareDataToSend(instruction: INSTRUCTION_TYPE_SENDING, data: String): ByteArray? {
        val packet = "${instruction.value}|$data".toByteArray(Charsets.US_ASCII)

        if(packet.size > 500) {
            Log.e("BLE","data to send to big! size: ${packet.size}")
            return null
        }

        return packet
    }

    fun sendData(instruction: INSTRUCTION_TYPE_SENDING,data: String) {
        val preparedData = prepareDataToSend(instruction, data)
        if(preparedData == null) {
            dataToLogQueue.add("Nie udało się przygotować danych do wysyłki")
            return
        }
        bleController.sendData(preparedData)
    }
}

data class ReceivedMsg(
    val instruction: RemoteBLEDeviceController.Companion.INSTRUCTION_TYPE_RECEIVING?,
    val msgString: String?,
    val msgWaga: Double?,
    val msgTime: Long?
)
