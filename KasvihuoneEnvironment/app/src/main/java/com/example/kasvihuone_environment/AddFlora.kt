package com.example.kasvihuone_environment


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class AddFlora : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private val url = "https://mkac3lxwmduijwjdh6qjn3rlna0jwtvq.lambda-url.us-east-1.on.aws/"
    private lateinit var humdEdit : EditText
    private lateinit var nameEdit : EditText
    private lateinit var tempEdit : EditText
    private lateinit var descEdit : EditText
    private lateinit var lightEdit : EditText




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_flora)
        supportActionBar?.hide()

        humdEdit = findViewById(R.id.editHumidity)
        nameEdit = findViewById(R.id.editPlantName)
        tempEdit = findViewById(R.id.editTemperature)
        descEdit = findViewById(R.id.editDescription)
        lightEdit = findViewById(R.id.editLight)

        val addFlora : ImageView = findViewById(R.id.addFlora)
        val addChanges : ImageView = findViewById(R.id.addChanges)
        val backButton : ImageView = findViewById(R.id.backButton)


        requestQueue = Volley.newRequestQueue(this)

        backButton.setOnClickListener {
            val faIntent = Intent(this, FloraActivity::class.java)
            startActivity(faIntent)
            finish()
        }

        addChanges.setOnClickListener {
            val changeIntent = Intent (this, PlantChange::class.java)
            startActivity(changeIntent)
            finish()
        }


        fun sendDataToBackend(humd: String, name: String, temp: String, desc: String, light: String) {
            val jsonBody = JSONObject().apply {
                put("humd", humd)
                put("name", name)
                put("temp", temp)
                put("descp", desc)
                put("lght", light)
                put("image", "")
                put("fileName", "")
            }

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                { response ->
                    val result = response.getString("result")
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                },
                { error ->
                    error.printStackTrace()
                }
            )

            requestQueue.add(jsonObjectRequest)
        }

        fun postPlant() {
            val humd = humdEdit.text.toString()
            val name = nameEdit.text.toString()
            val temp = tempEdit.text.toString()
            val desc = descEdit.text.toString()
            val light = lightEdit.text.toString()

            val editTexts = listOf(humdEdit, nameEdit, tempEdit, descEdit, lightEdit)
            var allFieldsValid = true

            for (editText in editTexts) {
                val fieldValue = editText.text.toString()
                if (fieldValue.isBlank()) {
                    editText.setBackgroundResource(R.color.redReject)
                    allFieldsValid = false
                } else {
                    editText.setBackgroundResource(android.R.color.transparent)
                }
            }

            if (allFieldsValid) {
                // Display a dialog asking the user if they want to add a picture
                val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
                val titleTextView = dialogView.findViewById<TextView>(R.id.dialogTitle)
                val messageTextView = dialogView.findViewById<TextView>(R.id.dialogMessage)

                titleTextView.text = "Lisää kuva"
                messageTextView.text = "Haluatko lisätä kuvan?"

                val builder = AlertDialog.Builder(this)
                builder.setView(dialogView)
                builder.setPositiveButton("Yes") { _, _ ->

                }
                builder.setNegativeButton("No") { _, _ ->
                    // User selected "No"
                    // Proceed to send data to the backend
                    sendDataToBackend(humd, name, temp, desc, light)
                }

                val dialog = builder.create()
                dialog.show()
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                // Set background color
                positiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.brand1))
                negativeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.brand1))

                // Set text color
                positiveButton.setTextColor(ContextCompat.getColor(this, R.color.brand2))
                negativeButton.setTextColor(ContextCompat.getColor(this, R.color.brand2))
            } else {
                Toast.makeText(this, "Syötä kaikkiin kenttiin tarvittavat tiedot", Toast.LENGTH_SHORT).show()
            }
        }



        addFlora.setOnClickListener {
            postPlant()
        }


    }


}

/* Toimiva koodi joka lähettää datan ilman kuvia kun painetaan ei toastissa


class AddFlora : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private val url = "https://mkac3lxwmduijwjdh6qjn3rlna0jwtvq.lambda-url.us-east-1.on.aws/"
    private lateinit var humdEdit : EditText
    private lateinit var nameEdit : EditText
    private lateinit var tempEdit : EditText
    private lateinit var descEdit : EditText
    private lateinit var lightEdit : EditText




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_flora)
        supportActionBar?.hide()

        humdEdit = findViewById(R.id.editHumidity)
        nameEdit = findViewById(R.id.editPlantName)
        tempEdit = findViewById(R.id.editTemperature)
        descEdit = findViewById(R.id.editDescription)
        lightEdit = findViewById(R.id.editLight)

        val addFlora : ImageView = findViewById(R.id.addFlora)
        val addChanges : ImageView = findViewById(R.id.addChanges)
        val backButton : ImageView = findViewById(R.id.backButton)


        requestQueue = Volley.newRequestQueue(this)

        backButton.setOnClickListener {
            val faIntent = Intent(this, FloraActivity::class.java)
            startActivity(faIntent)
            finish()
        }

        addChanges.setOnClickListener {
            val changeIntent = Intent (this, PlantChange::class.java)
            startActivity(changeIntent)
            finish()
        }


        fun sendDataToBackend(humd: String, name: String, temp: String, desc: String, light: String) {
            val jsonBody = JSONObject().apply {
                put("humd", humd)
                put("name", name)
                put("temp", temp)
                put("descp", desc)
                put("lght", light)
                put("image", "")
                put("fileName", "")
            }

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                { response ->
                    val result = response.getString("result")
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                },
                { error ->
                    error.printStackTrace()
                }
            )

            requestQueue.add(jsonObjectRequest)
        }

         fun postPlant() {
            val humd = humdEdit.text.toString()
            val name = nameEdit.text.toString()
            val temp = tempEdit.text.toString()
            val desc = descEdit.text.toString()
            val light = lightEdit.text.toString()

            val editTexts = listOf(humdEdit, nameEdit, tempEdit, descEdit, lightEdit)
            var allFieldsValid = true

            for (editText in editTexts) {
                val fieldValue = editText.text.toString()
                if (fieldValue.isBlank()) {
                    editText.setBackgroundResource(R.color.redReject)
                    allFieldsValid = false
                } else {
                    editText.setBackgroundResource(android.R.color.transparent)
                }
            }

             if (allFieldsValid) {
                 // Display a dialog asking the user if they want to add a picture
                 val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
                 val titleTextView = dialogView.findViewById<TextView>(R.id.dialogTitle)
                 val messageTextView = dialogView.findViewById<TextView>(R.id.dialogMessage)

                 titleTextView.text = "Lisää kuva"
                 messageTextView.text = "Haluatko lisätä kuvan?"

                 val builder = AlertDialog.Builder(this)
                 builder.setView(dialogView)
                 builder.setPositiveButton("Yes") { _, _ ->

                 }
                 builder.setNegativeButton("No") { _, _ ->
                     // User selected "No"
                     // Proceed to send data to the backend
                     sendDataToBackend(humd, name, temp, desc, light)
                 }

                 val dialog = builder.create()
                 dialog.show()
                 val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                 val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                 // Set background color
                 positiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.brand1))
                 negativeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.brand1))

                 // Set text color
                 positiveButton.setTextColor(ContextCompat.getColor(this, R.color.brand2))
                 negativeButton.setTextColor(ContextCompat.getColor(this, R.color.brand2))
             } else {
                Toast.makeText(this, "Syötä kaikkiin kenttiin tarvittavat tiedot", Toast.LENGTH_SHORT).show()
            }
        }



        addFlora.setOnClickListener {
            postPlant()
        }


    }


}









*/