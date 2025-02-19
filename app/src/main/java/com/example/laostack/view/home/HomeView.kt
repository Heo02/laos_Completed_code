package com.example.laostack.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.laostack.LocalLanguageManager
import com.example.laostack.R
import com.example.laostack.ui.theme.pretendard
import com.example.laostack.view.LanguageButton
import com.example.laostack.view.MainBottomBar

@Composable
fun HomeView(navController: NavHostController) {

    val languageManager = LocalLanguageManager.current // 글자 설정
    Box(Modifier.safeDrawingPadding()) {
        Scaffold(
            bottomBar = { MainBottomBar(navController, "main") }
        ) { innerpadding ->
            Image(
                painter = painterResource(R.drawable.home_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerpadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .height(60.dp)
                        .padding(horizontal = 25.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Language",
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    LanguageButton("lo", "laos", languageManager, R.drawable.flag_lo)
                    Spacer(modifier = Modifier.width(10.dp))
                    LanguageButton("en", "en", languageManager, R.drawable.flag_en)
                    Spacer(modifier = Modifier.width(10.dp))
                    LanguageButton("ko", "ko", languageManager, R.drawable.flag_ko)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(25.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.home_circle),
                        contentDescription = "",
                        modifier = Modifier.size(350.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { navController.navigate("select_method") },
                            modifier = Modifier
                                .size(200.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.home_create),
                                contentDescription = "생성하기",
                                modifier = Modifier.size(200.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            Text(
                                text = stringResource(R.string.create_now1),
                                lineHeight = 24.sp,
                                style = TextStyle(
                                    fontFamily = pretendard,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = stringResource(R.string.create_now2),
                                lineHeight = 24.sp,
                                style = TextStyle(
                                    fontFamily = pretendard,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = stringResource(R.string.create_now3),
                                lineHeight = 24.sp,
                                style = TextStyle(
                                    fontFamily = pretendard,
                                    fontSize = 16.sp
                                )
                            )
                        }
                        Text(
                            text = stringResource(R.string.create_your_own),
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontSize = 16.sp
                            ),
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}