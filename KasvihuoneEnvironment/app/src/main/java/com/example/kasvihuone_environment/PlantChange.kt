package com.example.kasvihuone_environment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class PlantChange : AppCompatActivity() {
    private lateinit var displayName: TextView
    private lateinit var cycleLeft: ImageView
    private lateinit var cycleRight: ImageView
    private lateinit var backButton: ImageView
    private lateinit var editPlantName: EditText
    private lateinit var editHumidity: EditText
    private lateinit var editTemperature: EditText
    private lateinit var editLight: EditText
    private lateinit var editDescription: EditText
    private lateinit var changePlants : ImageView

    private var plantList = mutableListOf<Plant>()
    data class Plant(val id: String, val name: String, val lght: String, val temp: String, val descp: String, val humd: String)
    private var currentPlantIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_change)
        supportActionBar?.hide()

        changePlants = findViewById(R.id.changePlant)
        backButton = findViewById(R.id.backButton)
        displayName = findViewById(R.id.displayName)
        editPlantName = findViewById(R.id.editPlantName)
        editHumidity = findViewById(R.id.editHumidity)
        editTemperature = findViewById(R.id.editTemperature)
        editLight = findViewById(R.id.editLight)
        editDescription = findViewById(R.id.editDescription)
        cycleLeft = findViewById(R.id.cycleLeft)
        cycleRight = findViewById(R.id.cycleRight)
        cycleLeft.setImageResource(R.drawable.twotone_label_important_24)
        cycleLeft.scaleX = -1f
        cycleRight.setImageResource(R.drawable.twotone_label_important_24)

        val url = "https://2aryxtaek2rffdxspfy2vr3krq0xuadg.lambda-url.us-east-1.on.aws/"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    for (i in 0 until response.length()) {
                        val plantJson = response.getJSONObject(i)
                        val plant = Plant(
                            plantJson.getString("id"),
                            plantJson.getString("name"),
                            plantJson.getString("lght"),
                            plantJson.getString("temp"),
                            plantJson.getString("descp"),
                            plantJson.getString("humd")
                        )
                        Log.d("PlantChange", "Fetched Plant ID: ${plant.id}")
                        plantList.add(plant)
                    }
                    updateDisplayedPlant()
                    cycleLeft.setOnClickListener { cyclePlant(false) }
                    cycleRight.setOnClickListener { cyclePlant(true) }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            })
        Volley.newRequestQueue(this).add(request)

        backButton.setOnClickListener {
            val backIntent = Intent(this, AddFlora::class.java)
            startActivity(backIntent)
            finish()
        }

        changePlants.setOnClickListener{
            updateBackendData()
        }


    }

    private fun updateDisplayedPlant() {
        val currentPlant = plantList[currentPlantIndex]
        displayName.text = currentPlant.name
        editPlantName.hint = "Vaihda kasvin nimi | ${currentPlant.name}"
        editHumidity.hint = "Vaihda optimikosteus | ${currentPlant.humd}"
        editTemperature.hint = "Vaihda optimilämpötila | ${currentPlant.temp}"
        editLight.hint = "Vaihda valotaso | ${currentPlant.lght}"

        val truncatedDesc = if (currentPlant.descp.length > 15) {
            "${currentPlant.descp.substring(0, 15)}..."
        } else {
            currentPlant.descp
        }
        editDescription.hint = "Vaihda kuvaus | $truncatedDesc"

        cycleLeft.visibility = if (currentPlantIndex > 0) View.VISIBLE else View.GONE
        cycleRight.visibility = if (currentPlantIndex < plantList.size - 1) View.VISIBLE else View.GONE
    }

    private fun updateBackendData() {
        val currentPlant = plantList[currentPlantIndex]

        val url = "https://4mjqxutmvce4k3ff5z7jn2rfxi0houki.lambda-url.us-east-1.on.aws/"
        val requestBody = JSONObject().apply {
            put("id", currentPlant.id)
            put("name", currentPlant.name)
            put("lght", editLight.text.toString())
            put("temp", editTemperature.text.toString())
            put("descp", editDescription.text.toString())
            put("humd", editHumidity.text.toString())
        }

        val request = JsonObjectRequest(
            Request.Method.PUT, url, requestBody,
            { response ->
                try {
                    val result = response.getString("result")
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Virhe kasvin tietojen päivittämisessä.", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Virhe kasvin tietojen päivittämisessä.", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun cyclePlant(next: Boolean) {
        if (next && currentPlantIndex < plantList.size - 1) {
            currentPlantIndex++
        } else if (!next && currentPlantIndex > 0) {
            currentPlantIndex--
        }
        updateDisplayedPlant()
    }

}
