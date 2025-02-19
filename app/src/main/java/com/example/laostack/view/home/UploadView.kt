package com.example.laostack.view.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.laostack.R
import com.example.laostack.ui.theme.pretendard
import com.example.laostack.view.BackTopBar
import com.example.laostack.view.showToast
import com.example.laostack.viewModel.home.UploadViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UploadView(navController: NavController) {
    val context = LocalContext.current
    val viewModel: UploadViewModel = viewModel(factory = UploadViewModel.provideFactory())

    val attachedImageUri by viewModel.attachedImageUri.collectAsState()

    var tempUri: Uri? by remember { mutableStateOf(null) }
    val file = remember { context.createImageFile() }
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    // 문자열 리소스
    val cameraPermissionRequired = stringResource(R.string.camera_permission_required)
    val storagePermissionRequired = stringResource(R.string.storage_permission_required)
    val takePhoto = stringResource(R.string.take_photo)
    val uploadFile = stringResource(R.string.upload_file)
    val complete = stringResource(R.string.complete)
    val noUploadedFile = stringResource(R.string.no_uploaded_file)

    // 카메라 실행
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempUri?.let { uri ->
                viewModel.onImageAttached(context, uri)
            }
        }
    }

    // 카메라 권한 요청
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            showToast(context, cameraPermissionRequired)
        }
    }

    // 외부 저장소에서 파일 선택
    val fileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            viewModel.onFileSelected(context, selectedUri)
        }
    }

    // 저장소 권한 요청
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fileLauncher.launch("*/*")
        } else {
            showToast(context, storagePermissionRequired)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
            BackTopBar(navController, stringResource(id = R.string.file_upload), "main")
            Spacer(modifier = Modifier.height(verticalSpacing))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xff486284), shape = RoundedCornerShape(30.dp))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.speak_seal),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp
                        ),
                        color = Color(0xff486284)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(R.drawable.seal),
                    contentDescription = "물개",
                    modifier = Modifier.size(maxWidth * 0.1f) // 반응형 크기
                )
            }

            Spacer(modifier = Modifier.height(verticalSpacing))

            Box(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
                    .fillMaxWidth()
                    .dashedBorder(1.dp, 8.dp, Color.Gray)
                    .height(maxHeight * 0.5f), // 반응형 높이
                contentAlignment = Alignment.Center
            ) {
                if (attachedImageUri != null) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .border(1.dp, color = Color(0xff8C8C8C), RoundedCornerShape(30.dp))
                            .wrapContentHeight()
                            .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = attachedImageUri?.toString() ?: "없음",
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp
                            ),
                        )
                        Image(
                            painter = painterResource(R.drawable.delete),
                            contentDescription = "Remove image",
                            modifier = Modifier
                                .clickable { viewModel.removeAttachedImage() }
                                .padding(10.dp)
                                .size(20.dp)
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(R.drawable.icon_upload),
                        contentDescription = "첨부",
                        modifier = Modifier
                            .size(maxHeight * 0.25f) // 반응형 크기
                    )
                }
            }

            Spacer(modifier = Modifier.height(verticalSpacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            )
                                -> {
                                tempUri = uri
                                cameraLauncher.launch(uri)
                            }

                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    modifier = Modifier
                        .border(1.dp, color = Color(0xffB2B2B2), shape = RoundedCornerShape(8.dp))
                        .width(maxWidth * 0.4f) // 반응형 너비
                        .height(maxHeight * 0.12f), // 반응형 높이
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.icon_camera),
                            contentDescription = "사진",
                            modifier = Modifier.size(45.dp)
                        )
                        Spacer(modifier = Modifier.height(maxHeight * 0.01f)) // 반응형 간격
                        Text(
                            text = takePhoto,
                            color = Color.Black,
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(maxWidth * 0.06f)) // 반응형 간격

                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            when {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_MEDIA_IMAGES
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    fileLauncher.launch("*/*")
                                }

                                else -> {
                                    storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                }
                            }
                        } else {
                            when {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    fileLauncher.launch("*/*")
                                }

                                else -> {
                                    storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .border(1.dp, color = Color(0xffB2B2B2), shape = RoundedCornerShape(8.dp))
                        .width(maxWidth * 0.4f) // 반응형 너비
                        .height(maxHeight * 0.12f), // 반응형 높이
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.icon_file),
                            contentDescription = "파일",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(maxHeight * 0.013f)) // 반응형 간격
                        Text(
                            text = uploadFile,
                            color = Color.Black,
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(verticalSpacing))

            Button(
                onClick = {
                    val imageUri = attachedImageUri
                    if (imageUri != null) {
                        val encodedUri = Uri.encode(imageUri.toString())
                        navController.navigate("Creating/null/${encodedUri}")
                    } else {
                        showToast(context, noUploadedFile)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .height(50.dp), // 반응형 높이
                colors = ButtonDefaults.buttonColors(Color(0xffD3DCE7)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = complete,
                    color = Color(0xff486284),
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

// 이미지 파일 생성 함수
fun Context.createImageFile(): File {
    val timeStamp: String = SimpleDateFormat(
        "yyyyMMdd_HHmmss",
        Locale.getDefault()
    ).format(Date())

    val storageDir: File? = getExternalFilesDir(null)

    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

// 커스텀 테두리 Modifier
fun Modifier.dashedBorder(
    width: Dp,
    radius: Dp,
    color: Color
) = drawBehind {
    drawRoundRect(
        color = color,
        style = Stroke(
            width = width.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(10f, 10f),
                0f
            )
        ),
        cornerRadius = CornerRadius(radius.toPx())
    )
}