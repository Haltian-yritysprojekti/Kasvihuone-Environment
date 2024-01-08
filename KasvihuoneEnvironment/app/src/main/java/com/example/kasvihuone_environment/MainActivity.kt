package com.example.kasvihuone_environment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var tvTemperature: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvLight: TextView
    private lateinit var doorState: TextView
    private val handler = Handler()
    data class BackendData(
        val door: Int,
        val temp: Int,
        val humd: Int,
        val lght: Int,
        val id: Int,
        val airp: Int
    )

    private var backendDataList = mutableListOf<BackendData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        doorState = findViewById(R.id.doorState_tv)
        val clickableLayout = findViewById<RelativeLayout>(R.id.lo_click1)
        val clickableLayout2 = findViewById<RelativeLayout>(R.id.lo_click2)
        val clickableLayout3 = findViewById<RelativeLayout>(R.id.lo_click3)
        val clickableLayout4 = findViewById<RelativeLayout>(R.id.lo_click4)
        val clickableLayout6 = findViewById<RelativeLayout>(R.id.lo_click6)
        tvLight = findViewById(R.id.tv_lux)
        tvTemperature = findViewById(R.id.tv_temperature)
        tvHumidity = findViewById(R.id.tv_humidity)


        requestQueue = Volley.newRequestQueue(this)

        fetchDataFromBackendPeriodically()
        startService(Intent(this, DoorStatusService::class.java))

        clickableLayout.setOnClickListener{
            val floraIntent = Intent(this, FloraActivity::class.java)
            startActivity(floraIntent)
            finish()
        }

        clickableLayout2.setOnClickListener{
            val addFloraIntent = Intent(this, AddFlora::class.java)
            startActivity(addFloraIntent)
            finish()
        }
        clickableLayout3.setOnClickListener{
            val tempIntent = Intent(this, TempOT::class.java)
            startActivity(tempIntent)
            finish()
        }
        clickableLayout4.setOnClickListener{
            val humdIntent = Intent(this, HumdOT::class.java)
            startActivity(humdIntent)
            finish()
        }
        clickableLayout6.setOnClickListener{
            val visitorIntent = Intent(this, VisitorActivity::class.java)
            startActivity(visitorIntent)
            finish()
        }
    }

    private fun fetchDataFromBackendPeriodically() {
        fetchDataFromBackend()
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchDataFromBackend()
                handler.postDelayed(this,  60 * 1000) // a minute of time
                Log.d("Data Fetched", "UIdata fetched")
            }
        },   60 * 1000)
    }

    private fun fetchDataFromBackend() {
        val url = "https://std7prto3fhwxwtyl24eg4fofu0gzazj.lambda-url.us-east-1.on.aws/"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Parse the JSON response
                val backendDataList = parseBackendData(response)

                // Update UI with the first item in the response
                if (backendDataList.isNotEmpty()) {
                    val firstItem = backendDataList[0]
                    updateUI(firstItem.temp, firstItem.humd, firstItem.lght, firstItem.door)
                }
            },
            { error ->
                // Handle errors here
            }
        )
        requestQueue.add(jsonArrayRequest)
    }

    private fun parseBackendData(jsonArray: JSONArray): List<BackendData> {
        backendDataList.clear()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val backendData = BackendData(
                jsonObject.getInt("door"),
                jsonObject.getInt("temp"),
                jsonObject.getInt("humd"),
                jsonObject.getInt("lght"),
                jsonObject.getInt("id"),
                jsonObject.getInt("airp")
            )
            backendDataList.add(backendData)
        }
        return backendDataList
    }
    private fun updateUI(temperature: Int, humidity: Int, lght: Int, door: Int) {
        tvTemperature.text = "$temperature°C"
        tvHumidity.text = "$humidity%"
        tvLight.text = "$lght lux"

        val doorIv: ImageView = findViewById(R.id.door_iv)
        val doorStateTv : TextView = findViewById(R.id.doorState_tv)

        if (door == 1 ){
            doorIv.setImageResource(R.drawable.icons8_door_100)
            doorStateTv.text = "Ovi on kiinni."
        } else {
            doorIv.setImageResource(R.drawable.icons8_dooropen_100)
            doorStateTv.text = "Ovi on auki!"
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        showExitDialog()
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Poistumisvarmistus")
        builder.setMessage("Oletko varma että haluat poistua?")
        builder.setPositiveButton("Kyllä") { _, _ ->
            finish()
        }
        builder.setNegativeButton("Ei") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}