package com.example.fishingforecastappstav

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalendarActivity : AppCompatActivity() {

    // Месяцы для шапки
    private val months = arrayOf("Янв","Фев","Мар","Апр","Май","Июн","Июл","Авг","Сен","Окт","Ноя","Дек")

    // Пример структуры данных
    data class FishCalendarData(
        val fishName: String,
        val monthlyActivity: List<Int>
    )

    // Пример списка с данными
    private val fishCalendarList = listOf(
        FishCalendarData("Карп", listOf(1,2,3,4,5,5,4,4,3,2,1,1)),
        FishCalendarData("Белый амур", listOf(1,1,2,3,4,5,5,4,3,2,1,1)),
        FishCalendarData("Карась", listOf(1,2,4,5,5,5,5,5,4,3,2,1)),
        FishCalendarData("Лещ", listOf(0,1,2,3,4,5,4,4,3,2,1,0))
        // Добавьте другие рыбы при необходимости
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)  // Убедитесь, что у вас есть такой layout

        val tableLayout = findViewById<TableLayout>(R.id.tlCalendar)

        // Создаём заголовок (шапку) таблицы
        val headerRow = TableRow(this)
        // Первая ячейка шапки - "Рыба"
        val fishHeaderCell = TextView(this).apply {
            text = "Рыба"
            setPadding(16, 16, 16, 16)
            textSize = 14f
            setTextColor(Color.BLACK)
        }
        headerRow.addView(fishHeaderCell)

        // Ячейки для месяцев (Янв...Дек)
        for (month in months) {
            val monthCell = TextView(this).apply {
                text = month
                setPadding(16, 16, 16, 16)
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(Color.BLACK)
            }
            headerRow.addView(monthCell)
        }
        // Добавляем шапку в TableLayout
        tableLayout.addView(headerRow)

        // Теперь строки с данными по каждой рыбе
        for (fishData in fishCalendarList) {
            val row = TableRow(this).apply {
                layoutParams = TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            // Первая колонка - название рыбы
            val fishNameCell = TextView(this).apply {
                text = fishData.fishName
                setPadding(16, 16, 16, 16)
                textSize = 14f
                setTextColor(Color.DKGRAY)
            }
            row.addView(fishNameCell)

            // Следующие 12 колонок - активность по месяцам
            for (i in fishData.monthlyActivity.indices) {
                val activityValue = fishData.monthlyActivity[i]
                val cell = TextView(this).apply {
                    text = activityValue.toString() // например, от 0 до 5
                    textSize = 14f
                    gravity = Gravity.CENTER
                    setPadding(16, 16, 16, 16)
                    setTextColor(Color.BLACK)
                    // Можно раскрашивать фон
                    setBackgroundColor(getColorForActivity(activityValue))
                }
                row.addView(cell)
            }
            // Добавляем строку в таблицу
            tableLayout.addView(row)
        }
    }

    // Функция для выбора цвета (heatmap) в зависимости от активности
    private fun getColorForActivity(activity: Int): Int {
        return when (activity) {
            0 -> Color.parseColor("#FFFFFF")
            1 -> Color.parseColor("#FFF5F5")
            2 -> Color.parseColor("#FFDFDF")
            3 -> Color.parseColor("#FFBFBF")
            4 -> Color.parseColor("#FF9F9F")
            5 -> Color.parseColor("#FF7F7F")
            else -> Color.WHITE
        }
    }
}
