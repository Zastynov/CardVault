package com.zstnv.cardvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CardItem(val name:String, val code:String, val color:Color)

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CardVault() }
    }
}

@Composable
fun CardVault() {
    var cards by remember { mutableStateOf(listOf(
        CardItem("Пятёрочка", "", Color(0xFFFF6A00)),
        CardItem("Магнит", "", Color(0xFFE53935))
    )) }
    var showAdd by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<CardItem?>(null) }

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color=Color(0xFFF6F6F6)) {
            Column(Modifier.padding(18.dp)) {
                Text("Мои карты", fontSize=30.sp, fontWeight=FontWeight.Bold)
                Text("Все бонусные карты в одном месте", color=Color.Gray)
                Spacer(Modifier.height(18.dp))
                Button(onClick={showAdd=true}, modifier=Modifier.fillMaxWidth()) {
                    Text("＋ Добавить карту")
                }
                Spacer(Modifier.height(12.dp))
                LazyColumn(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(cards) { _, card ->
                        Card(
                            modifier=Modifier.fillMaxWidth().clickable { selected=card },
                            shape=RoundedCornerShape(22.dp)
                        ) {
                            Box(Modifier.fillMaxWidth().background(card.color).padding(22.dp)) {
                                Column {
                                    Text(card.name, color=Color.White, fontSize=22.sp, fontWeight=FontWeight.Bold)
                                    Text(if(card.code.isBlank()) "Нажмите, чтобы добавить код" else "Карта готова", color=Color.White.copy(alpha=.85f))
                                }
                            }
                        }
                    }
                }
            }
        }

        if(showAdd) AddDialog(
            onDismiss={showAdd=false},
            onAdd={name,code,color ->
                cards = cards + CardItem(name,code,color)
                showAdd=false
            }
        )

        selected?.let { card ->
            AlertDialog(
                onDismissRequest={selected=null},
                title={Text(card.name)},
                text={
                    Column(horizontalAlignment=Alignment.CenterHorizontally) {
                        Text(if(card.code.isBlank()) "Код карты ещё не добавлен" else "Штрихкод / QR: ${card.code}")
                        Spacer(Modifier.height(12.dp))
                        Text("Здесь можно будет добавить настоящий сканер и генерацию штрихкода в следующей версии.")
                    }
                },
                confirmButton={TextButton(onClick={selected=null}){Text("Закрыть")}}
            )
        }
    }
}

@Composable
fun AddDialog(onDismiss:()->Unit,onAdd:(String,String,Color)->Unit) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var hex by remember { mutableStateOf("#673AB7") }
    AlertDialog(
        onDismissRequest=onDismiss,
        title={Text("Новая карта")},
        text={
            Column {
                OutlinedTextField(name, {name=it}, label={Text("Название")})
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(code, {code=it}, label={Text("Штрихкод / номер")})
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(hex, {hex=it}, label={Text("Цвет #RRGGBB")})
            }
        },
        confirmButton={
            TextButton(onClick={
                val c=try { Color(android.graphics.Color.parseColor(hex)) } catch(_:Exception){Color(0xFF673AB7)}
                onAdd(name.ifBlank{"Моя карта"},code,c)
            }){Text("Добавить")}
        },
        dismissButton={TextButton(onClick=onDismiss){Text("Отмена")}}
    )
}
