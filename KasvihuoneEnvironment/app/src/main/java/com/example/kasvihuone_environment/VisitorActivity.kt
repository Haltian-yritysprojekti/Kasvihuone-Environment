package com.example.kasvihuone_environment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.Locale

class VisitorActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visitor)
        supportActionBar?.hide()

        var backButton : ImageView = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        requestQueue = Volley.newRequestQueue(this)

        fetchDataFromBackend("https://pnafmwfqpqxsdpixgnyh7m4slm0oimqm.lambda-url.us-east-1.on.aws/")
    }

    private fun fetchDataFromBackend(url: String) {
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Parse the JSON response as an array
                for (i in 0 until response.length()) {
                    val entry = response.getJSONObject(i)

                    val cardView = createCardView(entry.getString("date"), entry.getInt("count"))
                    val scrollView = findViewById<LinearLayout>(R.id.scrollViewLayout)
                    scrollView.addView(cardView)
                }
            },
            { error ->
                // Log the error for debugging
                Log.e("VisitorActivity", "Error fetching data from backend: $error")
            }
        )

        // Add the request to the RequestQueue
        requestQueue.add(jsonArrayRequest)
    }

    private fun createCardView(date: String, count: Int): MaterialCardView {
        val cardView = MaterialCardView(this)

        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cardView.layoutParams = layoutParams

        val constraintLayout = ConstraintLayout(this)
        cardView.addView(constraintLayout)

        val dateTextView = MaterialTextView(this)
        dateTextView.id = View.generateViewId()

        // Format the date string to display only the immediate date
        val formattedDate = formatDate(date)
        dateTextView.text = "$formattedDate"

        dateTextView.setTextColor(ContextCompat.getColor(this, R.color.brand2))
        constraintLayout.addView(dateTextView)

        val countTextView = MaterialTextView(this)
        countTextView.id = View.generateViewId()
        countTextView.text = "Vierailijamäärä: $count"
        countTextView.setTextColor(ContextCompat.getColor(this, R.color.brand2))
        constraintLayout.addView(countTextView)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        // Center dateTextView horizontally
        constraintSet.centerHorizontally(dateTextView.id, ConstraintSet.PARENT_ID)

        // Center countTextView horizontally
        constraintSet.centerHorizontally(countTextView.id, ConstraintSet.PARENT_ID)

        // Set top-to-bottom constraint between dateTextView and countTextView
        constraintSet.connect(countTextView.id, ConstraintSet.TOP, dateTextView.id, ConstraintSet.BOTTOM)

        constraintSet.applyTo(constraintLayout)

        return cardView
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }

    override fun onStop() {
        super.onStop()
        // Cancel any ongoing Volley requests when the activity is stopped
        requestQueue.cancelAll(this)
    }
}