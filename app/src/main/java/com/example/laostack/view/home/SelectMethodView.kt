package com.example.laostack.view.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.laostack.R
import com.example.laostack.ui.theme.pretendard
import com.example.laostack.view.BackTopBar
import com.example.laostack.view.CategoryButton

@Composable
fun SelectMethodView(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            val maxWidth = maxWidth
            val maxHeight = maxHeight
            val horizontalPadding = maxWidth * 0.06f
            val verticalSpacing = maxHeight * 0.04f

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BackTopBar(navController, stringResource(id = R.string.select_method), "main")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
//                        .verticalScroll(rememberScrollState()) 높이 초과 시 주석 해제
                ) {
                    Spacer(modifier = Modifier.height(verticalSpacing))

                    // 업로드 버튼
                    Button(
                        onClick = {
                            navController.navigate("upload_file")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFD3DCE7))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.go_upload),
                                contentDescription = "upload",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(100.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.go_upload),
                                style = TextStyle(
                                    fontFamily = pretendard,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp
                                ),
                                color = Color(0xFF8C8C8C)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(verticalSpacing))

                    // 카테고리 안내 문구
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(R.drawable.penguin),
                            contentDescription = "penguin",
                            modifier = Modifier.size(maxWidth * 0.1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color(0xff486284), shape = RoundedCornerShape(30.dp))
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.select_category),
                                style = TextStyle(
                                    fontFamily = pretendard,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 10.sp
                                ),
                                color = Color(0xff486284)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(verticalSpacing))

//                     카테고리 버튼s
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CategoryButton(
                                iconResId  = R.drawable.category_conversation,
                                contentDescription = stringResource(id = R.string.category_conversation),
                                category = "conversation",
                                navController = navController,
                                isBackground = false,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(horizontalPadding))

                            CategoryButton(
                                iconResId  = R.drawable.category_object,
                                contentDescription = stringResource(id = R.string.category_object),
                                category = "object",
                                navController = navController,
                                isBackground = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(verticalSpacing))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CategoryButton(
                                iconResId  = R.drawable.category_food,
                                contentDescription = stringResource(id = R.string.category_food),
                                category = "food",
                                navController = navController,
                                isBackground = true,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(horizontalPadding))

                            CategoryButton(
                                iconResId  = R.drawable.category_culture,
                                contentDescription = stringResource(id = R.string.category_culture),
                                category = "culture",
                                navController = navController,
                                isBackground = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}