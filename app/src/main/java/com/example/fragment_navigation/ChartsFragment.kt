package com.example.fragment_navigation

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DateIterator(
    val startDate: LocalDate,
    val endDateInclusive: LocalDate,
    val stepDays: Long
) : Iterator<LocalDate> {
    private var currentDate = startDate

    override fun hasNext() = currentDate <= endDateInclusive

    override fun next(): LocalDate {

        val next = currentDate

        currentDate = currentDate.plusDays(stepDays)

        return next

    }

}

class DateProgression(
    override val start: LocalDate,
    override val endInclusive: LocalDate,
    val stepDays: Long = 1
) :
    Iterable<LocalDate>, ClosedRange<LocalDate> {

    override fun iterator(): Iterator<LocalDate> =
        DateIterator(start, endInclusive, stepDays)

    infix fun step(days: Long) = DateProgression(start, endInclusive, days)

}

operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)
public object Store {
    val dataList = ArrayList<ChartsFragment.Data1>()
    public var startDate = "";
    public var endDate = "";
    public var valTemp = 99;
    public var valHumid = 99;
    public var selectedOperator1 = 3;
    public var selectedOperator2 = 3;

    public fun fillData2(dataArray: DataForCharsets) { //ГЕНЕРАЦИЯ ТЕСТОВЫХ ДАННЫХ!!! ЗАМЕНИТЬ
        for (i in 0 until dataArray.ARR.size) {
            dataList.add(
                ChartsFragment.Data1(
                    temperature = dataArray.ARR[i].sensor_0[0].toDouble(),
                    humidity = dataArray.ARR[i].sensor_0[1].toDouble(),
                    temperature2 = dataArray.ARR[i].sensor_1[0].toDouble(),
                    humidity2 = dataArray.ARR[i].sensor_1[1].toDouble(),
                    date = "${i}.11.2022",
                    time = dataArray.ARR[i].time[0].toString() + ":" + dataArray.ARR[i].time[1].toString() + ":" + dataArray.ARR[i].time[2].toString(),
                    timeS = dataArray.ARR[i].time[0].toDouble()*3600 + dataArray.ARR[i].time[1].toDouble()*60 + dataArray.ARR[i].time[2].toDouble()
                )
            )
        }
        for (i in 0 until dataList.size - 1){
            for (j in i + 1 until dataList.size){
                if(dataList[i].timeS > dataList[j].timeS){
                    val buf = dataList[i]
                    dataList[i] = dataList[j]
                    dataList[j] = buf
                }
            }
        }
    }

    public fun filter() {
        filterByRange()
        filterByTemp()
        filterByHumid()
    }

    public fun filterByDay() {
        if (startDate != "") {
            val removalList = ArrayList<ChartsFragment.Data1>()
            for (data in dataList) {
                if (data.date != startDate) {
                    removalList.add(data)
                }
            }
            dataList.removeAll(removalList.toSet())
        }
    }

    public fun filterByTemp() {
        if (valTemp != 99) {
            val removalList = ArrayList<ChartsFragment.Data1>()
            if (selectedOperator1 == 0) {
                for (data in dataList) {
                    if (data.temperature != valTemp.toDouble()) {
                        removalList.add(data)
                    }
                }
            } else if (selectedOperator1 == 1) {
                for (data in dataList) {
                    if (data.temperature <= valTemp.toDouble()) {
                        removalList.add(data)
                    }
                }
            } else {
                for (data in dataList) {
                    if (data.temperature >= valTemp.toDouble()) {
                        removalList.add(data)
                    }
                }
            }
            dataList.removeAll(removalList.toSet())
        }
    }

    public fun filterByHumid() {
        if (valHumid != 99) {
            val removalList = ArrayList<ChartsFragment.Data1>()
            if (selectedOperator2 == 0) {
                for (data in dataList) {
                    if (data.humidity != valHumid.toDouble()) {
                        removalList.add(data)
                    }
                }
            }
            if (selectedOperator2 == 1) {
                for (data in dataList) {
                    if (data.humidity <= valHumid.toDouble()) {
                        removalList.add(data)
                    }
                }
            }
            if (selectedOperator2 == 2) {
                for (data in dataList) {
                    if (data.humidity >= valHumid.toDouble()) {
                        removalList.add(data)
                    }
                }
            }
            dataList.removeAll(removalList.toSet())
        }
        println(valHumid)
    }

    public fun filterByRange() {
        if (startDate != "" && endDate != "") {
            val formatDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val sDate = LocalDate.parse(startDate, formatDate)
            val eDate = LocalDate.parse(endDate, formatDate)
            val removalList = ArrayList<ChartsFragment.Data1>()
            val filterData = ArrayList<String>()
            for (date in sDate..eDate step 1) {
                filterData.add(date.format(formatDate))
                println(date.toString())
            }
            for (data in dataList) {
                if (!filterData.contains(data.date)) {
                    removalList.add(data)
                }
            }
            dataList.removeAll(removalList.toSet())

        } else filterByDay()
        println(endDate.toString())
    }

    public fun resetFilter() {
        startDate = "";
        endDate = "";
        valTemp = 99;
        valHumid = 99;
        selectedOperator1 = 3;
        selectedOperator2 = 3;
    }
}

class ChartsFragment : Fragment() {
    var h: Handler? = null
    val RECEIVE_MESSAGE = 1
    private var btAdapter: BluetoothAdapter? = null
    private var btSocket: BluetoothSocket? = null
    private val sb = StringBuilder()
    private var mConnectedThread: ConnectedThread? = null
    var dataArray: DataForCharsets = DataForCharsets(listOf())

    data class Data1(
        var date: String = "",
        var time: String = "",
        var temperature: Double = 0.0,
        var humidity: Double = 0.0,
        var temperature2: Double = 0.0,
        var humidity2: Double = 0.0,
        var timeS: Double = 0.0
    )

    lateinit var aaChartModel1: AAChartModel
    lateinit var aaChartModel2: AAChartModel

    private fun mainEvent() {
        //Store.dataList.clear()
        //Store.filter()
        //Работаем с температурой
        val temperatureData = ArrayList<Double>()
        for (index in 0 until Store.dataList.size) {
            temperatureData.add(Store.dataList[index].temperature)
        }
        val temperatureData2 = ArrayList<Double>()
        for (index in 0 until Store.dataList.size) {
            temperatureData2.add(Store.dataList[index].temperature2)
        }

        //Работаем с влажностью
        val humidityData = ArrayList<Double>()
        for (index in 0 until Store.dataList.size) {
            humidityData.add(Store.dataList[index].humidity)
        }
        val humidityData2 = ArrayList<Double>()
        for (index in 0 until Store.dataList.size) {
            humidityData2.add(Store.dataList[index].humidity2)
        }

        //Горизонтальная ось графика
        val xAxisText = ArrayList<String>()
        for (index in 0 until Store.dataList.size) {
            xAxisText.add(Store.dataList[index].time)
        }

        //val aaChartView = view?.findViewById<AAChartView>(com.example.fragment_navigation.R.id.aa_chart_view)
        val aaChartModel1: AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .dataLabelsEnabled(true)
            .legendEnabled(false)
            .title("Влажность")
            .yAxisTitle("Влажность, %")
            //.backgroundColor("#ffffff")
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Влажность")
                        .data(humidityData.toArray())
                        .color("#44BBCC"),
                    AASeriesElement()
                        .data(humidityData2.toArray())
                        .color("#4682B4")

                ),
            ).categories(xAxisText.toArray(arrayOf(String())))

        //val aaChartView2 = view?.findViewById<AAChartView>(com.example.fragment_navigation.R.id.aa_chart_view2)
        val aaChartModel2: AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .dataLabelsEnabled(true)
            .legendEnabled(false)
            .yAxisTitle("Градусы °C")
            .title("Температура")
            //.backgroundColor("#ffffff")
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Температура")
                        .data(temperatureData.toArray())
                        .color("#FF8E18"),

                    AASeriesElement()
                        .data(temperatureData2.toArray())
                        .color("#DDBB22")

                ),

            ).categories(xAxisText.toArray(arrayOf(String())))
        aachart1.aa_drawChartWithChartModel(aaChartModel1)
        aachart2.aa_drawChartWithChartModel(aaChartModel2)
    }
    var root : View ?= null
    lateinit var aachart1 : AAChartView
    lateinit var aachart2 : AAChartView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(com.example.fragment_navigation.R.layout.fragment_charts, container, false)

        h = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    RECEIVE_MESSAGE -> {
                        val readBuf = msg.obj as ByteArray
                        val strIncom = String(readBuf, 0, msg.arg1)
                        sb.append(strIncom) // формируем строку
                        val endOfLineIndex = sb.indexOf("@") // определяем символы конца строки
                        if (endOfLineIndex > 0) {                                            // если встречаем конец строки,
                            var sbprint = sb.substring(0, endOfLineIndex) // то извлекаем строку
                            sb.delete(0, sb.length)
                            var gson = Gson()
                            try {
                                dataArray = gson.fromJson(sbprint, DataForCharsets::class.java)
                                Store.fillData2(dataArray)
                                Store.resetFilter()
                                mainEvent()
                            }catch (e: Exception){
                                Log.d(TAG, "ERROR "+e.message.toString())
                            }
                        }
                    }
                }
            }
        }
        btAdapter = BluetoothAdapter.getDefaultAdapter()
        checkBTState()


        aachart1 = rootView.findViewById<AAChartView>(com.example.fragment_navigation.R.id.aa_chart_view)
        aachart2 = rootView.findViewById<AAChartView>(com.example.fragment_navigation.R.id.aa_chart_view2)
        val btnRefresh = rootView.findViewById<FloatingActionButton>(com.example.fragment_navigation.R.id.floatingActionButton1)
        val btnFilter = rootView.findViewById<FloatingActionButton>(com.example.fragment_navigation.R.id.floatingActionButton2)
        mainEvent()
        btnFilter.setOnClickListener {
            val nextFrag = PickFilterFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }
        btnRefresh.setOnClickListener {
            mConnectedThread!!.write("%{\"type\":\"get_data_arr\"}@")
        }


        return rootView
    }

    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "...onResume - попытка соединения...")
        val device = btAdapter!!.getRemoteDevice(address)
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID)
        } catch (e: SecurityException) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.message + ".")
        }
        try {
            btAdapter!!.cancelDiscovery()
        }catch (e: SecurityException){

        }
        Log.d(TAG, "...Соединяемся...")
        try {
            btSocket!!.connect()
            Log.d(TAG, "...Соединение установлено и готово к передачи данных...")
        } catch (e: SecurityException) {
            try {
                btSocket!!.close()
                Log.d(TAG, "Соединение не установлено")
            } catch (e2: IOException) {
                errorExit(
                    "Fatal Error",
                    "In onResume() and unable to close socket during connection failure" + e2.message + "."
                )
            }
        }
        Log.d(TAG, "...Создание Socket...")
        mConnectedThread = ConnectedThread(btSocket!!)
        mConnectedThread!!.start()
    }

    public override fun onPause() {
        super.onPause()
        Log.d(TAG, "...In onPause()...")
        try {
            btSocket!!.close()
        } catch (e2: IOException) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.message + ".")
        }
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?
        override fun run() {
            //val buffer = ByteArray(32)
            //var bytes: Int
            while (true) {
                try {
                    var bytes = mmInStream!!.available()
                    if (bytes == 0) {
                        sleep(10)
                        continue
                    }
                    val buffer = ByteArray(bytes)

                    bytes =mmInStream!!.read(buffer)
                    h?.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer)?.sendToTarget()
                } catch (e: IOException) {
                    Log.d(TAG, e.message.toString());
                    break
                }
            }
        }
        fun write(message: String) {
            try {
                mmOutStream!!.write(message.toByteArray())
                Log.d(TAG, "Данные отправлены")
                Log.d(TAG, message.toString())
            } catch (e: IOException) {
                Log.d(TAG, "...Ошибка отправки данных: " + e.message + "...")
            }
        }
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
            }
        }
        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null
            try {
                tmpIn = mmSocket.inputStream
                tmpOut = mmSocket.outputStream
            } catch (e: IOException) {
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }
    }

    private fun checkBTState() {
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth не поддерживается")
        } else {
            if (btAdapter!!.isEnabled) {
                Log.d(TAG, "...Bluetooth включен...")
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }
    }
    private fun errorExit(title: String, message: String) {
        Toast.makeText(context, "$title - $message", Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "bluetooth"
        private const val REQUEST_ENABLE_BT = 1
        private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private const val address = "20:16:01:19:68:59"
    }


}