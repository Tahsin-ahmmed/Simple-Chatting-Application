package ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.Screens
import data.model.ModelFriend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.imageio.ImageIO
import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Composable
fun FriendListItem(friend: ModelFriend, click: (String) -> Unit) {

    val imageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(friend.image) {
        isLoading = true
        imageBitmap.value = loadImageFromNetwork(friend.image)
        isLoading = false
    }

    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
        Row(
            modifier = Modifier
                .clickable { click.invoke(friend.name) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(2.dp, Color.Black, CircleShape)
                    .padding(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    imageBitmap.value?.let { bitmap ->
                        Image(bitmap = bitmap, contentDescription = "Loaded Image", modifier = Modifier.fillMaxSize())
                    } ?: Text("Failed to load image")
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(friend.name, fontWeight = FontWeight.Bold)
        }
    }
}

suspend fun loadImageFromNetwork(url: String): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        runCatching {
            URL(url).openStream().use { inputStream ->
                val bufferedImage: BufferedImage? = ImageIO.read(inputStream)
                bufferedImage?.let { image ->
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    ImageIO.write(image, "png", byteArrayOutputStream)
                    val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
                    loadImageBitmap(byteArrayInputStream)
                }
            }
        }.getOrNull()
    }
}
