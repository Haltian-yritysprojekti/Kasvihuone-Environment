package com.example.kasvihuone_environment


import android.content.Intent
import android.media.Image
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
    private val originalHints = mutableMapOf<EditText, String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_flora)
        supportActionBar?.hide()

        humdEdit = findViewById(R.id.editHumidity)
        nameEdit = findViewById(R.id.editPlantName)
        tempEdit = findViewById(R.id.editTemperature)
        descEdit = findViewById(R.id.editDescription)
        lightEdit = findViewById(R.id.editLight)


        originalHints[humdEdit] = humdEdit.hint.toString()
        originalHints[nameEdit] = nameEdit.hint.toString()
        originalHints[tempEdit] = tempEdit.hint.toString()
        originalHints[descEdit] = descEdit.hint.toString()
        originalHints[lightEdit] = lightEdit.hint.toString()



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


        fun sendDataToBackend(humd: String, name: String, temp: String, desc: String, light: String, image: String, fileName: String) {
            val jsonBody = JSONObject().apply {
                put("humd", humd)
                put("name", name)
                put("temp", temp)
                put("descp", desc)
                put("lght", light)
                put("image", image)
                put("fileName", fileName)
            }

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                { response ->
                    val result = response.getString("result")
                    if (result == "Plant added to database successfully") {
                        val messageToast = "Kasvi $name lisätty onnistuneesti"
                        Toast.makeText(this, messageToast, Toast.LENGTH_SHORT).show()
                    }
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
                // No need to show the dialog, directly send data to the backend
                val image = ""
                val fileName = ""
                sendDataToBackend(humd, name, temp, desc, light, image, fileName)
            } else {
                Toast.makeText(this, "Syötä kaikkiin kenttiin tarvittavat tiedot", Toast.LENGTH_SHORT).show()
            }

            for ((editText, originalHint) in originalHints) {
                editText.hint = originalHint
            }
        }



        addFlora.setOnClickListener {
            postPlant()
        }


    }


}