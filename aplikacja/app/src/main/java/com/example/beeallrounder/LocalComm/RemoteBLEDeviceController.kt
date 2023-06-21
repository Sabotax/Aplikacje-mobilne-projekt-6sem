package com.example.beeallrounder.LocalComm

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.beeallrounder.databases.dbEspSynch.model.SensorRecord
import com.example.beeallrounder.databases.dbEspSynch.viewmodel.UserViewModel

class RemoteBLEDeviceController(
    val deviceName: String,
    val deviceAddress: String,
    private val app: ViewModelStoreOwner
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
        enum class INSTRUCTION_TYPE_SENDING(val value: Byte) {
            START_SENDING(1),
            CONTINUE(2),
            STOP(3)
        }

        enum class INSTRUCTION_TYPE_RECEIVING(val value: Byte) {
            SENDING(1), // wysyłam dane
            STOP(2), // koniec wysyłania
            ERROR(3) // wydarzył się błąd w ESP
        }
    }

    val incomingDataQueue: MutableList<ByteArray?> = mutableListOf()
    val threadFlag = true

    val dataToLogQueue: MutableList<String> = mutableListOf()

    private var mUserViewModel: UserViewModel? = null
    //mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java) żeby rozpocząć

    init{
        execute()
    }


    private fun decodeInstructionReceived(data: ByteArray): INSTRUCTION_TYPE_RECEIVING? {
        return enumValues<INSTRUCTION_TYPE_RECEIVING>().find { it.value == data[0] }
    }

    private fun decodePacketData(data: ByteArray): String {
        return data.toString(Charsets.US_ASCII)
    }

    private fun decodedDataToSensorRecord(decodedData: String): SensorRecord {
        TODO()
    }

    fun execute() {
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
                        val decodedInstruction = decodeInstructionReceived(dataItem)
                        val dataWithoutInstruction = dataItem.slice(IntRange(1,dataItem.size-1)).toByteArray()
                        when (decodedInstruction) {
                            INSTRUCTION_TYPE_RECEIVING.SENDING -> {
                                val decodedData = decodePacketData(dataWithoutInstruction)
                                val row = decodedDataToSensorRecord(decodedData)

                                if(mUserViewModel == null) {
                                    mUserViewModel = ViewModelProvider(app).get(UserViewModel::class.java)
                                }
                                mUserViewModel!!.addSensorRecord(row)
                            }
                            INSTRUCTION_TYPE_RECEIVING.STOP -> {
                                val day: String = TODO() // esp w danych ma przesłać który dzień właśnie skończyło wysyłać
                                dataToLogQueue.add("Urządzenie $deviceName skończyło przesyłanie dnia $day")
                                // wysyla logowanie że już koniec dla tego dnia
                            }
                            INSTRUCTION_TYPE_RECEIVING.ERROR -> {
                                // log że error i wyjebalo :C z komunikatem z esp jak się da
                                val errorMsg = TODO() // esp przesyła co się stało (jeśli obsłuży)
                                dataToLogQueue.add("Wystąpił błąd na esp ")
                            }
                        }
                    }
                }
                Thread.sleep(100)
            }
        }.start()
    }


    fun prepareDataToSend(instruction: INSTRUCTION_TYPE_SENDING, data: String): ByteArray? { //będzie w pętli wysyłało prośby dzień po dniu
        val packet = (listOf<Byte>(instruction.value) + data.map { it.code.toByte() }).toTypedArray().toByteArray()

        if(packet.size > 500) {
            Log.e("BLE","data to send to big! size: ${packet.size}")
            return null
        }

        return packet
    }
}

//abstract class  LookableEnum<T> {
//    protected abstract val value: T
//    final fun <T> findFromValue(value: T) {
//        enumValues<T>().find { it.value == x }
//    }
//}