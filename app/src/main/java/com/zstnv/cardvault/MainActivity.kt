package com.zstnv.cardvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CardItem(
    val name: String,
    val code: String,
    val color: Color
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardVault()
        }
    }
}

@Composable
fun CardVault() {
    var cards by remember { mutableStateOf(listOf<CardItem>()) }
    var showAdd by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<CardItem?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    val hour = java.util.Calendar.getInstance()
        .get(java.util.Calendar.HOUR_OF_DAY)

    val greeting = when (hour) {
        in 5..11 -> "Доброе утро"
        in 12..17 -> "Добрый день"
        in 18..23 -> "Добрый вечер"
        else -> "Доброй ночи"
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF6F6F6)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                       Text(greeting, fontSize = 28.sp, fontWeight = FontWeight.Bold)

Text(
    "Твои бонусные карты",
    color = Color.Gray
)
