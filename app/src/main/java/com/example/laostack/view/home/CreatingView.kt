package com.example.laostack.view.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.laostack.LanguageManager
import com.example.laostack.R
import com.example.laostack.api.APIInterface
import com.example.laostack.ui.theme.pretendard
import com.example.laostack.viewModel.home.CreateTextResult
import com.example.laostack.viewModel.home.CreatingViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Composable
fun CreatingView(
    navController: NavHostController,
    apiService: APIInterface,
    category: String? = null,
    imageUri: Uri? = null
) {
    val viewModel: CreatingViewModel = viewModel(factory = CreatingViewModel.provideFactory(apiService))

    val context = LocalContext.current
    val languageManager = LanguageManager(context)
    val language = languageManager.getSavedLanguage()

    // 선택된 방식에 따라 문제 생성 시작
    LaunchedEffect(category, imageUri) {
        when {
            category != null -> viewModel.processCategory(category, language)
            imageUri != null -> viewModel.performOCR(context, language, imageUri)
        }
    }

    // 문제 생성 결과
    val createTextResult by viewModel.createTextResult.collectAsState()
    // 뭐로 생성된 것인지
    val createToOCR by viewModel.createToOCR.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight
        val horizontalPadding = maxWidth * 0.06f
        val verticalSpacing = maxHeight * 0.04f
        val imageSize = maxWidth * 0.4f
        val warningRowWidth = maxWidth * 0.8f
        val warningImageSize = maxWidth * 0.05f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_creating),
                contentDescription = "creating",
                modifier = Modifier
                    .size(imageSize)
            )

            Spacer(modifier = Modifier.height(verticalSpacing))

            Text(
                text = stringResource(R.string.creating_in_progress),
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(verticalSpacing))

            CircularProgressIndicator(color = Color(0xff486284))

            Spacer(modifier = Modifier.height(verticalSpacing))

            Row(
                modifier = Modifier
                    .width(warningRowWidth)
                    .wrapContentHeight()
                    .background(Color(0xffefefef), shape = RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.massege),
                    contentDescription = "경고",
                    Modifier.size(warningImageSize)
                )
                Spacer(modifier = Modifier.width(maxWidth * 0.02f))
                Text(
                    text = stringResource(R.string.warning_message),
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp
                    ),
                    lineHeight = 24.sp,
                    color = Color(0xff8D8D8D)
                )
            }
            when (val result = createTextResult) {
                is CreateTextResult.Success -> {
                    LaunchedEffect(result) {
                        val messageJson = Json.encodeToString(result.message)
                        navController.navigate("Create/${Uri.encode(messageJson)}/$createToOCR") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
                is CreateTextResult.Error -> {
                    Spacer(modifier = Modifier.height(verticalSpacing))
                    Text(
                        text = stringResource(R.string.creation_failed, result.errorMessage),
                        color = Color.Red
                    )
                }
                CreateTextResult.Loading -> {
                    // 이미 로딩 중 상태를 표시하고 있으므로 추가 작업 불필요
                }
                CreateTextResult.Idle -> {
                    // 초기 상태이므로 추가 작업 불필요
                }
            }
        }
    }
}