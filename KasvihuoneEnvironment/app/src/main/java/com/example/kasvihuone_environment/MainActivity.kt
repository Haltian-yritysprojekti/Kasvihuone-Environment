package com.example.kasvihuone_environment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
        val clickableLayout = findViewById<RelativeLayout>(R.id.lo_click1)
        val clickableLayout2 = findViewById<RelativeLayout>(R.id.lo_click2)
        val clickableLayout3 = findViewById<RelativeLayout>(R.id.lo_click3)
        val clickableLayout4 = findViewById<RelativeLayout>(R.id.lo_click4)
        val clickableLayout5 = findViewById<RelativeLayout>(R.id.lo_click5)
        tvLight = findViewById(R.id.tv_lux)
        tvTemperature = findViewById(R.id.tv_temperature)
        tvHumidity = findViewById(R.id.tv_humidity)


        requestQueue = Volley.newRequestQueue(this)

        fetchDataFromBackendPeriodically()

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

        }
        clickableLayout4.setOnClickListener{

        }
        clickableLayout5.setOnClickListener{

        }
    }

    private fun fetchDataFromBackendPeriodically() {
        fetchDataFromBackend()
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchDataFromBackend()
                handler.postDelayed(this, 5 * 60 * 1000) // 5 minutes of time
            }
        }, 5 * 60 * 1000)
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
                    updateUI(firstItem.temp, firstItem.humd, firstItem.lght)
                }
            },
            { error ->
                // Handle errors here
            }
        )

        // Add the request to the RequestQueue
        requestQueue.add(jsonArrayRequest)
    }

    private fun parseBackendData(jsonArray: JSONArray): List<BackendData> {
        backendDataList.clear() // Clear the previous data before parsing the new data

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

    private fun updateUI(temperature: Int, humidity: Int, lght: Int) {
        tvTemperature.text = "$temperature°C"
        tvHumidity.text = "$humidity%"
        tvLight.text = "$lght lux"
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
    //Testipusku
}