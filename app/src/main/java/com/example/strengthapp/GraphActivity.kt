package com.example.strengthapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_graph.*
import java.sql.Timestamp
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap


class GraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val data: HashMap<String, HashMap<Timestamp, Int>> = intent.getSerializableExtra("data") as HashMap<String, HashMap<Timestamp, Int>>

        System.out.println("data " + data)
        System.out.println(intent.extras["data"])

        val user_value: Int = intent.extras["userID"] as Int
        val tc_value: Int = intent.extras["tcID"] as Int

        graph_view.viewport.isScalable = true
        graph_view.viewport.isScrollable = true
        graph_view.viewport.setScalableY(true)
        graph_view.viewport.setScrollableY(true)
        graph_view.gridLabelRenderer.verticalAxisTitle = "Volume (kg x reps)"
        graph_view.gridLabelRenderer.horizontalAxisTitle = "Date"

        graph_view.title = "Progress graph"

        graph_view.gridLabelRenderer.numHorizontalLabels = 5

        graph_view.viewport.isXAxisBoundsManual = true
        graph_view.viewport.isYAxisBoundsManual = true

        graph_view.viewport.setMinX(0.0)
        graph_view.viewport.setMaxX(0.1)


        graph_view.gridLabelRenderer.setLabelFormatter(MyLabelFormat())


        for(key in data.keys) {
            val values = data[key]
            var list: MutableList<DataPoint> = mutableListOf()
            for (i in values!!) {
                list.add(DataPoint(i.key, i.value.toDouble()))
            }
            list.sortBy { it.x }
            graph_view.viewport.setMaxY(list.maxBy { it.y }?.y!!)
            graph_view.viewport.setMinY(list.minBy { it.y }?.y!!)
            val series: LineGraphSeries<DataPoint> = LineGraphSeries(list.toTypedArray())
            series.title = key
            graph_view.addSeries(series)
        }

        graph_view.legendRenderer.isVisible = true
        graph_view.legendRenderer.align = LegendRenderer.LegendAlign.TOP

        backb.setOnClickListener { v ->
            val intent = Intent(applicationContext, CalendarActivity::class.java)
            intent.putExtra("userID", user_value)
            intent.putExtra("tcID", tc_value)
            startActivity(intent)
        }
    }
}

class MyLabelFormat : DefaultLabelFormatter() {
    override fun formatLabel(value: Double, isValueX: Boolean): String {
        if(isValueX) {
            return DateFormat.getDateInstance().format(Date(value.toLong()))
        } else {
            return super.formatLabel(value, isValueX)
        }
    }
}
