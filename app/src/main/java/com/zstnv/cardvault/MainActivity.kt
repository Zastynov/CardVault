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

    var cards by remember {
        mutableStateOf(listOf<CardItem>())
    }

    var showAdd by remember {
        mutableStateOf(false)
    }

    var selected by remember {
        mutableStateOf<CardItem?>(null)
    }

    var showSettings by remember {
        mutableStateOf(false)
    }

    var userName by remember {
        mutableStateOf("")
    }

    val hour = java.util.Calendar
        .getInstance()
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
                    .statusBarsPadding()
                    .padding(18.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = greeting,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (userName.isNotBlank()) {
                            Text(
                                text = userName,
                                fontSize = 20.sp,
                                color = Color.Gray
                            )
                        }

                        Text(
                            text = "Твои бонусные карты",
                            color = Color.Gray
                        )
                    }

                    TextButton(
                        onClick = {
                            showSettings = true
                        }
                    ) {
                        Text(
                            text = "⚙",
                            fontSize = 24.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Button(
                    onClick = {
                        showAdd = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("＋ Добавить карту")
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                if (cards.isEmpty()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "У тебя пока нет карт",
                            color = Color.Gray
                        )
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        items(cards) { card ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selected = card
                                    },
                                shape = RoundedCornerShape(22.dp)
                            ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(card.color)
                                        .padding(22.dp)
                                ) {

                                    Column {

                                        Text(
                                            text = card.name,
                                            color = Color.White,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(
                                            modifier = Modifier.height(6.dp)
                                        )

                                        Text(
                                            text = if (card.code.isBlank()) {
                                                "Код не добавлен"
                                            } else {
                                                "Нажмите, чтобы открыть"
                                            },
                                            color = Color.White.copy(
                                                alpha = 0.85f
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAdd) {

            AddDialog(
                onDismiss = {
                    showAdd = false
                },
                onAdd = { name, code, color ->

                    cards = cards + CardItem(
                        name = name,
                        code = code,
                        color = color
                    )

                    showAdd = false
                }
            )
        }

        selected?.let { card ->

            AlertDialog(
                onDismissRequest = {
                    selected = null
                },

                title = {
                    Text(card.name)
                },

                text = {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (card.code.isBlank()) {

                            Text(
                                text = "Код карты не добавлен"
                            )

                        } else {

                            Text(
                                text = "Код карты: ${card.code}",
                                fontSize = 18.sp
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        TextButton(
                            onClick = {

                                cards = cards.filter {
                                    it != card
                                }

                                selected = null
                            }
                        ) {

                            Text(
                                text = "Удалить карту",
                                color = Color.Red
                            )
                        }
                    }
                },

                confirmButton = {

                    TextButton(
                        onClick = {
                            selected = null
                        }
                    ) {
                        Text("Закрыть")
                    }
                }
            )
        }

        if (showSettings) {

            AlertDialog(

                onDismissRequest = {
                    showSettings = false
                },

                title = {
                    Text("Настройки")
                },

                text = {

                    OutlinedTextField(
                        value = userName,
                        onValueChange = {
                            userName = it
                        },
                        label = {
                            Text("Твоё имя")
                        },
                        singleLine = true
                    )
                },

                confirmButton = {

                    TextButton(
                        onClick = {
                            showSettings = false
                        }
                    ) {
                        Text("Сохранить")
                    }
                }
            )
        }
    }
}

@Composable
fun AddDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, Color) -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var code by remember {
        mutableStateOf("")
    }

    var hex by remember {
        mutableStateOf("#673AB7")
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Новая карта")
        },

        text = {

            Column {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Название")
                    },
                    singleLine = true
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it
                    },
                    label = {
                        Text("QR / штрихкод / номер")
                    },
                    singleLine = true
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = hex,
                    onValueChange = {
                        hex = it
                    },
                    label = {
                        Text("Цвет #RRGGBB")
                    },
                    singleLine = true
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    val color = try {

                        Color(
                            android.graphics.Color.parseColor(hex)
                        )

                    } catch (_: Exception) {

                        Color(0xFF673AB7)
                    }

                    onAdd(
                        name.ifBlank {
                            "Моя карта"
                        },
                        code,
                        color
                    )
                }
            ) {
                Text("Добавить")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Отмена")
            }
        }
    )
}
