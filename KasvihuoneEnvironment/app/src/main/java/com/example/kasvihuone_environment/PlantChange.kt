package com.example.kasvihuone_environment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


class PlantChange : AppCompatActivity() {
    private lateinit var displayName : TextView
    private lateinit var cycleLeft : ImageView
    private lateinit var cycleRight : ImageView
    private var plantList = mutableListOf<Plant>()
    data class Plant(val id: String, val name: String)
    private var currentPlantIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_change)
        supportActionBar?.hide()
        displayName = findViewById(R.id.displayName)
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
                        )
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
    }

    private fun updateDisplayedPlant() {
        val currentPlant = plantList[currentPlantIndex]
        displayName.text = currentPlant.name
        cycleLeft.visibility = if (currentPlantIndex > 0) View.VISIBLE else View.GONE
        cycleRight.visibility = if (currentPlantIndex < plantList.size - 1) View.VISIBLE else View.GONE
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
