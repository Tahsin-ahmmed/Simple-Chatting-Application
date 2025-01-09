package ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import data.model.MessageType
import data.model.ModelMessage
import di.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.util.Base64
import javax.imageio.ImageIO

@Composable
fun ChatBubbles(message: ModelMessage) {

    val url = "https://lh3.googleusercontent.com/Qq8jgBfsLRsv_51_7cAOKHpCG_6NnXqrmfCVF9DOlVtVDu7-0NoMZBHd_v173vq-LtLiexyEY6HB318oM-1owQCVClHKvrXyLHA8"
    val imageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }

    fun decodeFile(encodedString: String, fileName: String) {
        val decodedBytes = Base64.getDecoder().decode(encodedString)
        val file = File(fileName)
        Files.write(file.toPath(), decodedBytes)
        try {
            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()
                if (file.exists()) {
                    desktop.open(file)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = if (message.from == App.id.toString()) Arrangement.End else Arrangement.Start,
    ) {
        Surface(
            modifier = Modifier.wrapContentWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (message.from == App.id.toString()) Color(33, 150, 243) else  Color.LightGray
        ) {
            if (message.type == MessageType.TEXT) {
                Text(
                    text = message.message,
                    modifier = Modifier.padding(all = 8.dp),
                    color = Color.Black
                )
            } else {
                imageBitmap.value?.let { bitmap ->
                    Row {
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Loaded Image",
                            modifier = Modifier.size(100.dp, 100.dp).padding(16.dp)
                                .clickable {
                                    decodeFile(message.message, message.fileName!!)
                                }
                        )

                        Text(text = message.fileName!!, modifier = Modifier.align(Alignment.CenterVertically).padding(16.dp))
                    }
                } ?: Text("Failed to load image")

                LaunchedEffect(url) {
                    imageBitmap.value = loadImageFromNetwork(url)
                }
            }
        }
    }
}