package com.example.kasvihuone_environment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class FloraActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private val url = "https://2aryxtaek2rffdxspfy2vr3krq0xuadg.lambda-url.us-east-1.on.aws/"
    private var currentPlantIndex = 0
    private lateinit var tempStat : TextView
    private lateinit var humdStat : TextView
    private lateinit var lghtStat : TextView
    private lateinit var description : TextView
    private lateinit var plantName : TextView
    private lateinit var faunaPic : ImageView
    private lateinit var deletePlant : ImageView

    private var plantList = mutableListOf<Plant>()

    data class Plant(
        val temp: Int,
        val humd: Int,
        val lght: Int,
        val name: String,
        val id: Int,
        val descp: String,
        val image: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flora)
        supportActionBar?.hide()
        tempStat = findViewById(R.id.tempStat)
        humdStat = findViewById(R.id.humdStat)
        lghtStat = findViewById(R.id.lghtStat)
        description = findViewById(R.id.description)
        plantName = findViewById(R.id.plantName)
        faunaPic = findViewById(R.id.flowerFocus)
        deletePlant = findViewById(R.id.deletePlant)

        requestQueue = Volley.newRequestQueue(this)

        val backButton : ImageView = findViewById(R.id.backButton)
        val addFlora : ImageView = findViewById(R.id.iv_AddFlora)
        val cycleButton : Button = findViewById(R.id.cyclePlants)

        cycleButton.setOnClickListener {
            cycleToNextPlant()
        }


        backButton.setOnClickListener{
            val MainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(MainActivityIntent)
        }

        addFlora.setOnClickListener{
            val addFloraIntent = Intent(this, AddFlora::class.java)
            startActivity(addFloraIntent)
            finish()
        }
        fetchData()

    }
    private fun fetchData(){
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                parseJsonResponse(response)
                showPlantData(currentPlantIndex)
            },
            { error ->
                error.printStackTrace()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    private fun parseJsonResponse(response: JSONArray) {
        for (i in 0 until response.length()) {
            val plantObject = response.getJSONObject(i)

            val temp = plantObject.getInt("temp")
            val humd = plantObject.getInt("humd")
            val lght = plantObject.getInt("lght")
            val name = plantObject.getString("name")
            val id = plantObject.getInt("id")
            val descp = plantObject.getString("descp")
            val image = plantObject.getString("image")


            val plant = Plant(temp, humd, lght, name, id, descp, image)
            plantList.add(plant)
        }

        plantList.sortBy { it.id }
    }

    private fun cycleToNextPlant() {
        if (plantList.isNotEmpty()) {
            currentPlantIndex = (currentPlantIndex % plantList.size) + 1
            showPlantData(currentPlantIndex - 1)
        } else {
            // Display default information or placeholders
            tempStat.text = "30°C"
            humdStat.text = "30%"
            lghtStat.text = "300 lux"
            description.text = "Tietojen lataus epäonnistui, näytetään epäaitoja tietoja"
            plantName.text = "Latausongelma"

            Picasso.get()
                .load(R.drawable.icons8_plant_100)
                .error(R.drawable.icons8_plant_100)
                .resize(300, 300)
                .into(faunaPic)
        }
    }

    private fun showPlantData(index: Int) {
        val selectedPlant = plantList[index]

        tempStat.text = "${selectedPlant.temp}°C"
        humdStat.text = "${selectedPlant.humd}%"
        lghtStat.text = "${selectedPlant.lght} lux"
        description.text = "${selectedPlant.descp}"
        plantName.text = "${selectedPlant.name}"

        if (selectedPlant.image.isNotEmpty()) {
             Picasso.get()
                 .load(selectedPlant.image)
                 .error(R.drawable.icons8_plant_100)
                 .resize(300,300)
                 .into(faunaPic)
        } else {
            Picasso.get().load(R.drawable.icons8_plant_100)
                .resize(300, 300)
                .into(faunaPic)
        }

        fun deletePlant(plantId: Int) {
            val deleteUrl = "https://ixegw7rqi6g62jg22eijyxqnlm0zziwf.lambda-url.us-east-1.on.aws/"

            val jsonBody = JSONObject().apply {
                put("id", plantId.toString())
            }

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, deleteUrl, jsonBody,
                { response ->
                    val result = response.getString("result")
                    if (result == "Plant deleted from database") {
                        val deletedPlantIndex = plantList.indexOfFirst { it.id == plantId }
                        if (deletedPlantIndex != -1) {
                            plantList.removeAt(deletedPlantIndex)
                            cycleToNextPlant()
                            Toast.makeText(this, "Kukka poistettu palvelimelta", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Virhe poistaessa kasvia. Yritä myöhemmin uudelleen.", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    error.printStackTrace()
                    Log.e("DeletePlant", "Error: ${error.networkResponse?.statusCode}, ${String(error.networkResponse?.data ?: ByteArray(0))}")
                }
            )

            requestQueue.add(jsonObjectRequest)
        }

        fun showDeleteConfirmationDialog() {
            val selectedPlant = plantList[currentPlantIndex]

            val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
            val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)

            dialogTitle.text = "Kasvinpoisto"
            dialogMessage.text = "Haluatko poistaa kasvin: ${selectedPlant.name}?"

            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Poista") { dialog, _ ->
                    deletePlant(selectedPlant.id)
                    dialog.dismiss()
                }
                .setNegativeButton("Palaa") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog = dialogBuilder.create()
            dialog.show()

            // Customize the positive and negative buttons if needed
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            // Set background color
            positiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.brand1))
            negativeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.brand1))

            // Set text color
            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.brand2))
            negativeButton.setTextColor(ContextCompat.getColor(this, R.color.brand2))
        }

        deletePlant.setOnClickListener {
            showDeleteConfirmationDialog()
        }


    }
}