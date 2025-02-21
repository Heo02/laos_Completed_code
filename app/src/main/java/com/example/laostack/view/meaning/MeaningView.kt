package com.example.laostack.view.meaning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.laostack.R
import com.example.laostack.api.APIInterface
import com.example.laostack.ui.theme.pretendard
import com.example.laostack.view.MainBottomBar

// MeaningView.kt
@Composable
fun MeaningView(
    navController: NavHostController,
    apiService: APIInterface
) {
    val context = LocalContext.current

    // Used to include the bottom navigation bar
    Scaffold(
        bottomBar = { MainBottomBar(navController, "meaning") }
    ) { innerPadding ->
        BoxWithConstraints(
            Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(innerPadding) // Apply padding to avoid overlapping with system bars
        ) {
            val maxWidth = maxWidth
            val maxHeight = maxHeight

            // Set padding and sizes proportionally for a responsive UI
            val hopadding = maxWidth * 0.08f // Horizontal padding
            val vepadding = maxHeight * 0.05f // Vertical padding
            val imageSize = maxWidth * 0.1f // Image size
            val boxBorderSize = maxWidth * 0.002f // Box border thickness
            val spacerHeight = maxHeight * 0.02f // Spacer height for spacing between elements
            val boxPadding = maxWidth * 0.02f // Inner padding for the box

            Column(modifier = Modifier.padding(hopadding, vepadding)) {
                // Title text
                Text(
                    text = stringResource(R.string.storage),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(spacerHeight))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Box containing description and image
                    Box(
                        modifier = Modifier
                            .border(
                                boxBorderSize,
                                Color(0xff486284),
                                RoundedCornerShape(30.dp)
                            )
                            .padding(boxPadding)
                            .wrapContentSize() // Adjust size based on text length
                    ) {
                        Text(
                            text = stringResource(R.string.storage_description),
                            color = Color(0xff486284),
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(spacerHeight))

                    // Penguin image
                    Image(
                        painter = painterResource(R.drawable.penguin),
                        contentDescription = "penguin",
                        modifier = Modifier.size(imageSize)
                    )
                }

                Spacer(modifier = Modifier.height(spacerHeight * 2))

                // Section to display the workbook list
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Display workbook list
                     WorkbookList(context, navController, apiService)
                }
            }
        }
    }
}