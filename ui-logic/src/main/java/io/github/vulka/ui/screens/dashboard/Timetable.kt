package io.github.vulka.ui.screens.dashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
object Timetable

@Composable
fun TimetableScreen() {
    Text(text = "Timetable")
}