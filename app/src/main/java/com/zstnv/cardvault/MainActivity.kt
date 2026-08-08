package com.zstnv.cardvault

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.util.Calendar

data class CardItem(
    val name: String,
    val code: String,
    val format: Int,
    val color: Color
)

class MainActivity : ComponentActivity() {

    private var imageResult: ((String, Int) -> Unit)? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri == null) return@registerForActivityResult

            try {

                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap == null) {
                    return@registerForActivityResult
                }

                scanImage(bitmap)

            } catch (_: Exception) {
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CardVault(
                onPickImage = {
                    pickImage.launch("image/*")
                },
                onScanCamera = {
                    scanWithCamera()
                }
            )
        }
    }

    private fun scanImage(bitmap: Bitmap) {

        val image = InputImage.fromBitmap(bitmap, 0)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            )
            .build()

        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->

                val barcode = barcodes.firstOrNull()

                if (barcode != null && barcode.rawValue != null) {

                    imageResult?.invoke(
                        barcode.rawValue!!,
                        barcode.format
                    )
                }
            }
            .addOnCompleteListener {
                scanner.close()
            }
    }

    private fun scanWithCamera() {

        val options = GmsBarcodeScannerOptions.Builder()
            .enableAutoZoom()
            .build()

        val scanner =
            GmsBarcodeScanning.getClient(this, options)

        scanner.startScan()
            .addOnSuccessListener { barcode ->

                barcode.rawValue?.let { value ->

                    imageResult?.invoke(
                        value,
                        barcode.format
                    )
                }
            }
    }

    fun setScanCallback(
        callback: (String, Int) -> Unit
    ) {
        imageResult = callback
    }
}

@Composable
fun CardVault(
    onPickImage: () -> Unit,
    onScanCamera: () -> Unit
) {

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

    var scannedCode by remember {
        mutableStateOf("")
    }

    var scannedFormat by remember {
        mutableStateOf(Barcode.FORMAT_UNKNOWN)
    }

    val hour = Calendar.getInstance()
        .get(Calendar.HOUR_OF_DAY)

    val greeting = when (hour) {
        in 5..11 -> "Доброе утро"
        in 12..17 -> "Добрый день"
        in 18..23 -> "Добрый вечер"
        else -> "Доброй ночи"
    }

    val activity = androidx.compose.ui.platform.LocalContext.current
        as MainActivity

    DisposableEffect(Unit) {

        activity.setScanCallback { code, format ->

            scannedCode = code
            scannedFormat = format
            showAdd = true
        }

        onDispose {
        }
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
                        scannedCode = ""
                        scannedFormat = Barcode.FORMAT_UNKNOWN
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
                                            text = formatName(card.format),
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
                initialCode = scannedCode,
                initialFormat = scannedFormat,

                onDismiss = {
                    showAdd = false
                },

                onCamera = {
                    showAdd = false
                    onScanCamera()
                },

                onImage = {
                    showAdd = false
                    onPickImage()
                },

                onAdd = { name, code, format, color ->

                    cards = cards + CardItem(
                        name = name,
                        code = code,
                        format = format,
                        color = color
                    )

                    showAdd = false
                    scannedCode = ""
                    scannedFormat = Barcode.FORMAT_UNKNOWN
                }
            )
        }

        selected?.let { card ->

            CardPreviewDialog(
                card = card,

                onDismiss = {
                    selected = null
                },

                onDelete = {

                    cards = cards.filter {
                        it != card
                    }

                    selected = null
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
    initialCode: String,
    initialFormat: Int,
    onDismiss: () -> Unit,
    onCamera: () -> Unit,
    onImage: () -> Unit,
    onAdd: (String, String, Int, Color) -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var code by remember {
        mutableStateOf(initialCode)
    }

    var format by remember {
        mutableStateOf(initialFormat)
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
                        Text("Данные QR / штрихкода")
                    },
                    singleLine = true
                )

                if (format != Barcode.FORMAT_UNKNOWN) {

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Тип: ${formatName(format)}",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                OutlinedButton(
                    onClick = onCamera,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("📷 Сканировать камерой")
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                OutlinedButton(
                    onClick = onImage,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("🖼️ Выбрать изображение")
                }

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
                        format,
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

@Composable
fun CardPreviewDialog(
    card: CardItem,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {

    val bitmap = remember(
        card.code,
        card.format
    ) {

        generateBarcode(
            content = card.code,
            format = card.format
        )
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text(card.name)
        },

        text = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                if (bitmap != null) {

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Код карты",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = formatName(card.format),
                    color = Color.Gray
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = card.code,
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                TextButton(
                    onClick = onDelete
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
                onClick = onDismiss
            ) {

                Text("Закрыть")
            }
        }
    )
}

fun generateBarcode(
    content: String,
    format: Int
): Bitmap? {

    if (content.isBlank()) {
        return null
    }

    val barcodeFormat = when (format) {

        Barcode.FORMAT_QR_CODE ->
            BarcodeFormat.QR_CODE

        Barcode.FORMAT_EAN_13 ->
            BarcodeFormat.EAN_13

        Barcode.FORMAT_EAN_8 ->
            BarcodeFormat.EAN_8

        Barcode.FORMAT_UPC_A ->
            BarcodeFormat.UPC_A

        Barcode.FORMAT_UPC_E ->
            BarcodeFormat.UPC_E

        Barcode.FORMAT_CODE_128 ->
            BarcodeFormat.CODE_128

        Barcode.FORMAT_CODE_39 ->
            BarcodeFormat.CODE_39

        Barcode.FORMAT_CODE_93 ->
            BarcodeFormat.CODE_93

        Barcode.FORMAT_CODABAR ->
            BarcodeFormat.CODABAR

        Barcode.FORMAT_ITF ->
            BarcodeFormat.ITF

        else ->
            BarcodeFormat.QR_CODE
    }

    return try {

        val width =
            if (barcodeFormat == BarcodeFormat.QR_CODE) {
                700
            } else {
                1000
            }

        val height =
            if (barcodeFormat == BarcodeFormat.QR_CODE) {
                700
            } else {
                400
            }

        val matrix: BitMatrix =
            MultiFormatWriter().encode(
                content,
                barcodeFormat,
                width,
                height
            )

        val bitmap =
            Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )

        for (x in 0 until width) {

            for (y in 0 until height) {

                bitmap.setPixel(
                    x,
                    y,
                    if (matrix[x, y]) {
                        android.graphics.Color.BLACK
                    } else {
                        android.graphics.Color.WHITE
                    }
                )
            }
        }

        bitmap

    } catch (_: Exception) {

        null
    }
}

fun formatName(format: Int): String {

    return when (format) {

        Barcode.FORMAT_QR_CODE ->
            "QR-код"

        Barcode.FORMAT_EAN_13 ->
            "EAN-13"

        Barcode.FORMAT_EAN_8 ->
            "EAN-8"

        Barcode.FORMAT_UPC_A ->
            "UPC-A"

        Barcode.FORMAT_UPC_E ->
            "UPC-E"

        Barcode.FORMAT_CODE_128 ->
            "Code 128"

        Barcode.FORMAT_CODE_39 ->
            "Code 39"

        Barcode.FORMAT_CODE_93 ->
            "Code 93"

        Barcode.FORMAT_CODABAR ->
            "Codabar"

        Barcode.FORMAT_ITF ->
            "ITF"

        Barcode.FORMAT_AZTEC ->
            "Aztec"

        Barcode.FORMAT_DATA_MATRIX ->
            "Data Matrix"

        Barcode.FORMAT_PDF417 ->
            "PDF417"

        else ->
            "Код"
    }
}
