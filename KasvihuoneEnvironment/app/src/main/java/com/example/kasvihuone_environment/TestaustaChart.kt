package com.example.kasvihuone_environment

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class TestaustaChart : AppCompatActivity() {
    private lateinit var lineChart: LineChart
    private lateinit var xValues: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testausta_chart)
        lineChart = findViewById(R.id.tempDataChart)
        lineChart.setBackgroundColor(getResources().getColor(R.color.brand1))

        val description = Description()
        description.text = "Students Record"
        description.setPosition(150f, 15f)
        lineChart.description = description
        lineChart.axisRight.setDrawLabels(false)
        xValues = mutableListOf("Nadun", "Kamaa", "Jhon", "Jerry")
        val xAxis = lineChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        xAxis.labelCount = 4
        xAxis.granularity = 1f
        val yAxis = lineChart.getAxisLeft()
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 100f
        yAxis.axisLineWidth = 5f
        yAxis.axisLineColor = getResources().getColor(R.color.brand2)
        yAxis.labelCount = 5
        val entries1: MutableList<Entry> = ArrayList()
        entries1.add(Entry(0f, 15f))
        entries1.add(Entry(1f, 30f))
        entries1.add(Entry(2f, 12f))
        entries1.add(Entry(3f, 45f))
        val entries2: MutableList<Entry> = ArrayList()
        entries2.add(Entry(0f, 10f))
        entries2.add(Entry(1f, 25f))
        entries2.add(Entry(2f, 17f))
        entries2.add(Entry(3f, 42f))
        val dataSet1 = LineDataSet(entries1, "Maths")
        dataSet1.color = Color.BLUE
        val dataSet2 = LineDataSet(entries2, "Science")
        dataSet2.color = Color.RED



        val lineData = LineData(dataSet1, dataSet2)
        lineChart.setData(lineData)
        lineChart.invalidate()
    }
}