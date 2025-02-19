package com.example.laostack.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.laostack.LanguageManager
import com.example.laostack.R
import com.example.laostack.ui.theme.pretendard

// 메인화면 바텀 바
@Composable
fun MainBottomBar(
    navController: NavHostController,
    currentScreen: String
) {
    BottomAppBar(
        containerColor = Color.White,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarButton(
                icon = R.drawable.icon_main,
                contentDescription = "메인",
                isSelected = currentScreen == "main",
                onClick = {
                    if (currentScreen != "main") {
                        navController.navigate("main") {
                            popUpTo("main") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
            Divider(
                color = Color.LightGray,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(vertical = 8.dp)
            )
            BottomBarButton(
                icon = R.drawable.icon_storage,
                contentDescription = "보관함",
                isSelected = currentScreen == "meaning_view",
                onClick = {
                    if (currentScreen != "meaning_view") {
                        navController.navigate("meaning_view") {
                            popUpTo("meaning_view") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// 바텀 바 아이콘 버튼
@Composable
fun BottomBarButton(
    icon: Int,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        val iconSize = maxWidth * 0.9f
        val backgroundSize =maxWidth * 0.4f
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(backgroundSize)
                    .background(
                        color = Color(0xFFD3DCE7),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
        Image(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}

// 언어 지정 버튼
@Composable
fun LanguageButton(
    languageCode: String,
    languageName: String,
    languageManager: LanguageManager,
    imageId: Int
) {
    BoxWithConstraints(
        modifier = Modifier
            .size(35.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
    ) {
        IconButton(
            onClick = {
                languageManager.updateLanguage(languageCode)
            }
        ) {
            val imageSize = maxWidth * 0.9f
            Image(
                painter = painterResource(id = imageId),
                contentDescription = languageName,
                modifier = Modifier.size(imageSize)
            )
        }
    }
}

// 상단 바
@Composable
fun BackTopBar(
    navController: NavController,
    title: String,
    route: String,
    showBackButton: Boolean = true
) {
    BoxWithConstraints {
        val height = maxHeight * 0.08f
        val padding = maxWidth * 0.04f
        val backButtonSize = maxWidth * 0.1f

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = padding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                Image(
                    painter = painterResource(R.drawable.topbar_back),
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .size(backButtonSize)
                        .clickable {
                            navController.navigate(route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                )
            } else {
                Spacer(modifier = Modifier.width(backButtonSize))
            }
            Text(
                text = title,
                fontFamily = pretendard,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xff333333),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = if (showBackButton) padding * 2 else padding * 2.5f)
            )
        }
    }
}

// 카테고리 버튼
@Composable
fun CategoryButton(
    iconResId: Int,
    contentDescription: String,
    category: String,
    navController: NavController,
    isBackground: Boolean,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val backgroundColor = if (isBackground) {
        Color(0xFFD3DCE7)
    } else {
        Color.White
    }

    Button(
        onClick = { showDialog = true },
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        border = BorderStroke(3.dp, Color(0xFFD3DCE7))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = contentDescription,
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                ),
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 8.dp)
            )

            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = contentDescription,
                tint = Color.DarkGray,
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 8.dp)
            )
        }
    }
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = contentDescription,
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(id = R.string.category_title),
                        color = Color.Black,
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showDialog = false },
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xffD3DCE7), RoundedCornerShape(10.dp))
                                .height(40.dp),
                        ) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                style = TextStyle(
                                    fontFamily = pretendard,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13.sp
                                ),
                                color = Color(0xFF3B4A66)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                showDialog = false
                                navController.navigate("Creating/${category}/null") {
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFFD3DCE7)),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text(
                                text = stringResource(id = R.string.confirm),
                                style = TextStyle(
                                    fontFamily = pretendard,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13.sp
                                ),
                                color = Color(0xFF3B4A66)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 토스트 간결화
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}