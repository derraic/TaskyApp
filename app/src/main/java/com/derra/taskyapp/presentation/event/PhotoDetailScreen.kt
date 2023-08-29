package com.derra.taskyapp.presentation.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.rememberAsyncImagePainter
import com.derra.taskyapp.R
import com.derra.taskyapp.data.objectsviewmodel.Photo

@Composable
fun PhotoDetailScreen(
    viewModel: EventViewModel,
    photo: Photo,
    window: android.view.Window
) {

    WindowCompat.setDecorFitsSystemWindows(window, false)
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFF16161C))
        .padding(top = 13.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .padding(start = 22.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween)
        {
            Image(modifier = Modifier.clickable { viewModel.onEvent(EventEvent.PhotoScreenCloseClick) },painter = painterResource(id = R.drawable.cross_image_detail_screen), contentDescription = "cross")
            Text(
                text = "Photo",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 12.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(600),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Right,
                )
            )
            Image(modifier = Modifier.clickable { viewModel.onEvent(EventEvent.PhotoScreenDeleteClick) }
                , painter = painterResource(id = R.drawable.trash_can_photo_delete),
                contentDescription = "delete")
        }
        Spacer(modifier = Modifier.height(55.dp))
        Box(modifier = Modifier
            .width(328.dp)
            .height(555.dp)) {
            if (photo.key.startsWith("picture")){
                val painter: Painter = rememberAsyncImagePainter(viewModel.retrieveSavedImageUriByFileName(photo.url))
                Image(painter = painter, contentDescription = "event image"
                    ,modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
            }
            else {
                Image(
                    painter = rememberAsyncImagePainter(photo.url),
                    contentDescription = "Event Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,

                    )


            }
            
        }




    }

}