package com.example.kasvihuone_environment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.ParseException
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Locale

class HumdOT : AppCompatActivity() {
    private lateinit var cardView: CardView
    private lateinit var backButton: ImageView
    private lateinit var humdDataChart: LineChart
    private lateinit var plotArea: FrameLayout
    private lateinit var palaaButton: Button
    private lateinit var datesListedView: RelativeLayout

    data class HumdData(
        val humd: Int,
        val sensor: String,
        val date: String,
        val time: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_humd_ot)
        supportActionBar?.hide()

        palaaButton = findViewById(R.id.returnDateView)
        humdDataChart = findViewById(R.id.humdDataChart)
        backButton = findViewById(R.id.backButton)
        plotArea = findViewById(R.id.plotArea)
        datesListedView = findViewById(R.id.datesListedView)
        cardView = findViewById(R.id.cardView)

        backButton.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        fetchDataFromBackend()
    }

    private fun fetchDataFromBackend() {
        val url = "https://nswnxeloqzbll5onj2lpjptw2y0tyyuk.lambda-url.us-east-1.on.aws/humdOt"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    Log.d("Response", response.toString())

                    if (response.length() > 0) {
                        val datesSet = mutableSetOf<String>()
                        val humdDataList = mutableListOf<HumdData>()

                        for (i in 0 until response.length()) {
                            val humdObject = response.getJSONObject(i)

                            // Filter data for the specific sensor
                            if (humdObject.getString("sensor") == "TSEN01E6830200388") {
                                val date = humdObject.getString("date")
                                datesSet.add(date)

                                // Parse temperature data
                                val humdData = HumdData(
                                    humdObject.getInt("humd"),
                                    humdObject.getString("sensor"),
                                    date,
                                    humdObject.getString("time")
                                )
                                humdDataList.add(humdData)
                            }
                        }

                        createAndAddDateTextViews(datesSet, humdDataList)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("VolleyError", error.toString())
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun createAndAddDateTextViews(datesSet: Set<String>, humdDataList: List<HumdData>) {
        val linearLayout = findViewById<LinearLayout>(R.id.dateData)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 70)

        val initialTextView = TextView(this)
        initialTextView.text = "YYYY-MM-DD"
        initialTextView.layoutParams = layoutParams
        initialTextView.setTextColor(getColor(R.color.brand2))
        initialTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        linearLayout.addView(initialTextView)

        val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val sdfOutput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (date in datesSet) {
            try {
                val parsedDate = sdfInput.parse(date)
                val formattedDate = sdfOutput.format(parsedDate)

                val dateTextView = TextView(this)
                dateTextView.text = formattedDate
                dateTextView.layoutParams = layoutParams
                dateTextView.setTextColor(getColor(R.color.brand2))
                dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23f)

                dateTextView.setOnClickListener {
                    // Filter data for the selected date
                    val filteredData = humdDataList.filter { it.date == date }
                    // Populate LineChart with filtered data
                    populateLineChart(filteredData)
                    // Show/hide views as needed
                    cardView.visibility = View.VISIBLE
                    datesListedView.visibility = View.GONE
                    palaaButton.visibility = View.VISIBLE
                }

                linearLayout.addView(dateTextView)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    private fun populateLineChart(filteredData: List<HumdData>) {
        val entries: MutableList<Entry> = ArrayList()

        // Sort the data by time
        val sortedData = filteredData.sortedBy { it.time }

        // Set the desired start time
        val desiredStartTime = "08:00:00"

        // Calculate the time range in minutes
        val startHour = desiredStartTime.split(":")[0].toInt()
        val endHour = 18

        for (data in sortedData) {
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(data.time)
            val minutesFromStart = ((time.time - SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse("$startHour:00:00").time) / (1000 * 60)).toFloat()
            entries.add(Entry(minutesFromStart, data.humd.toFloat()))
        }

        val dataSet = LineDataSet(entries, "Kosteus ajan funktiona")
        dataSet.color = getColor(R.color.blue)

        // Disable circles and values
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.lineWidth = 3f

        // Configure the y-axis
        humdDataChart.axisLeft.axisMinimum = 0f
        humdDataChart.axisLeft.axisMaximum = 45f
        humdDataChart.axisLeft.labelCount = 10

        // Configure the x-axis
        val xAxis = humdDataChart.xAxis
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Display bi-hourly labels as integers
                return ((value / 60) + startHour).toInt().toString()
            }
        }
        xAxis.labelCount = (endHour - startHour) * 2 + 1 // Display bi-hourly labels
        xAxis.granularity = 120f  // Set the granularity to 120 minutes (2 hours)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = (endHour - startHour) * 60f
        xAxis.setDrawGridLines(true)

        // Disable right y-axis
        humdDataChart.axisRight.isEnabled = false

        // Disable description
        humdDataChart.description.isEnabled = false

        val lineData = LineData(dataSet)
        humdDataChart.data = lineData
        humdDataChart.invalidate()
    }

    fun onReturnClick(view: View) {
        cardView.visibility = View.GONE
        datesListedView.visibility = View.VISIBLE
        palaaButton.visibility = View.GONE

        humdDataChart.clear()
    }
}
