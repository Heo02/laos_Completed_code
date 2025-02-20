package com.example.laostack.view.home

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.laostack.CreateTextMessage
import com.example.laostack.ImageQuestion
import com.example.laostack.R
import com.example.laostack.api.APIInterface
import com.example.laostack.ui.theme.pretendard
import com.example.laostack.view.BackTopBar
import com.example.laostack.viewModel.home.CreateViewModel
import com.example.laostack.viewModel.home.ProblemContent
import com.example.laostack.viewModel.home.UploadStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// CreateView.kt
@Composable
fun CreateView(
    navController: NavHostController,
    apiService: APIInterface,
    message: CreateTextMessage?,
    createToOCR: Boolean
) {
    val context = LocalContext.current
    val viewModel: CreateViewModel = viewModel(factory = CreateViewModel.provideFactory(context, apiService))

    // Get the state variable values defined in ViewModel
    val reCreateResult by viewModel.reCreateResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uploadStatus by viewModel.uploadStatus.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showPDFDialog by remember { mutableStateOf(false) }

    // String resource ID
    val workbookSavedNotification = stringResource(R.string.workbook_saved_notification)
    val recreationFailedFormat = stringResource(R.string.recreation_failed)
    val pdfSaveComplete = stringResource(R.string.pdf_save_complete)
    val pdfSaving = stringResource(R.string.pdf_saving)

    // Guidance toast when switching Current view
    LaunchedEffect(Unit) {
        Toast.makeText(context, workbookSavedNotification, Toast.LENGTH_SHORT).show()
    }

    // Handle recreation result
    LaunchedEffect(reCreateResult) {
        if (reCreateResult != null) {
            reCreateResult?.fold(
                onSuccess = { result ->
                    val messageJson = Json.encodeToString(result)
                    navController.navigate("Create/${Uri.encode(messageJson)}/$createToOCR") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onFailure = { error ->
                    Toast.makeText(context, recreationFailedFormat.format(error.message), Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Handle upload result
    LaunchedEffect(uploadStatus) {
        when (uploadStatus) {
            is UploadStatus.Success -> {
                Toast.makeText(context, pdfSaveComplete, Toast.LENGTH_SHORT).show()
                navController.navigate("main") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is UploadStatus.Error -> {
                Toast.makeText(context, (uploadStatus as UploadStatus.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    //UI
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        val maxWidth = maxWidth
        val maxHeight = maxHeight
        val horizontalPadding = maxWidth * 0.06f
        val buttonHeight = maxHeight * 0.07f

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackTopBar(navController, stringResource(R.string.workbook_creation_complete), "main")

            Spacer(modifier = Modifier.height(maxHeight * 0.02f))

            CreateBar(message)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
            ) {
                Spacer(modifier = Modifier.height(maxHeight * 0.02f))

                // ReCreate Button
                Button(
                    onClick = {
                        showConfirmDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, color = Color(0xffD3DCE7)),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Text(
                        text = stringResource(R.string.recreate_workbook),
                        color = Color(0xff273C72),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(maxHeight * 0.02f))

                // Save as PDF Button
                Button(
                    onClick = {
                        showPDFDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xffD3DCE7))
                ) {
                    Text(
                        text = stringResource(R.string.save_as_pdf),
                        color = Color(0xff273C72),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
    CustomConfirmDialog(
        showDialog = showConfirmDialog,
        onDismissRequest = { showConfirmDialog = false },
        onConfirmClick = {
            showConfirmDialog = false
            // viewModel.recreateText(createToOCR)
        }
    )

    CustomLoadingDialog(
        showDialog = isLoading,
        onDismissRequest = {
            viewModel.cancelReCreate()
        }
    )

    CustomPDFAlertDialog(
        showDialog = showPDFDialog,
        onDismissRequest = { showPDFDialog = false },
        onConfirmClick = {
            showPDFDialog = false
            message?.let { msg ->
                val problemContents = buildList {
                    msg.imageQuestions.forEach { imageQuestion ->
                        add(ProblemContent(imageQuestion.question, imageQuestion.imageUrl))
                    }
                    add(ProblemContent(msg.textQuestions))
                }
                val answerContent = msg.answer
                viewModel.createPDFs(context, problemContents, answerContent, msg.wb_id)
                Toast.makeText(context, pdfSaving, Toast.LENGTH_SHORT).show()
            }

        }
    )
    if (uploadStatus is UploadStatus.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreateBar(message: CreateTextMessage?) {
    // A state variable that tracks the index of the selected tab
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Tab Title List
    val tabs = listOf(stringResource(R.string.problem_sheet), stringResource(R.string.answer_sheet))

    // Box containing tabs and indicators
    Box(modifier = Modifier.fillMaxWidth()) {
        // Row containing tab buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            // Definition for each tab
            tabs.forEachIndexed { index, title ->
                // Box for individual tabs
                Box(
                    modifier = Modifier
                        .weight(1f) // Use all assigned weight
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() }, // Managing click interactions
                            indication = null // Remove click effect
                        ) { selectedTabIndex = index } // Update selected tab on click
                        .padding(vertical = 10.dp)
                        .height(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Tab title text
                    Text(
                        text = title,
                        color = if (selectedTabIndex == index) Color(0xFF273C72) else Color.Black, // Change the text color of the selected tab
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp
                        )
                    )
                }
            }
        }

        // Animated indicator
        val transition = updateTransition(selectedTabIndex, label = "Tab indicator")
        val indicatorOffset by transition.animateDp(
            transitionSpec = {
                spring(stiffness = Spring.StiffnessLow) // Smooth spring animation
            },
            label = "Indicator offset"
        ) { page ->
            (page * (LocalConfiguration.current.screenWidthDp.dp - 36.dp) / tabs.size) + 18.dp // Calculating indicator position
        }

        // Indicator Box Settings
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = indicatorOffset) // Apply animated offset
                .width((LocalConfiguration.current.screenWidthDp.dp - 36.dp) / tabs.size) // Calculate and set the width of each tab
                .height(2.dp)
                .background(Color(0xFFD3DCE7))
        )
    }
    // AnimatedContent that displays content based on the selected tab
    AnimatedContent(
        targetState = selectedTabIndex, // Save the currently selected tab state
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300)) // Defining fade in/out animations
        }
    ) { targetState ->
        when (targetState) {
            0 -> if (message != null) {
                ProblemSheetContent(message.imageQuestions, message.textQuestions) // Show the contents of the problem
            }

            1 -> if (message != null) {
                AnswerSheetContent(message.answer) // Show the contents of the Answer
            }
        }
    }
}

// Output the generated Problem Sheet contents
@Composable
fun ProblemSheetContent(
    imageQuestions: List<ImageQuestion>,
    textQuestions: String
) {
    BoxWithConstraints(modifier = Modifier.background(Color.White)) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight
        val horizontalPadding = maxWidth * 0.04f
        val contentHeight = maxHeight * 0.65f

        Box(
            modifier = Modifier
                .padding(horizontal = horizontalPadding, vertical = maxHeight * 0.02f)
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .height(contentHeight),
            contentAlignment = Alignment.Center
        ) {

            // Vertical scrollable list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = maxHeight * 0.02f)
            ) {
                // Show image Questions
                items(imageQuestions) { imageQuestion ->
                    Text(
                        text = imageQuestion.question,
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(bottom = maxHeight * 0.01f)
                    )
                    // Asynchronous image loading
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageQuestion.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Question Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(contentHeight * 0.4f)
                            .padding(bottom = maxHeight * 0.02f)
                    )
                }
                // Show text Questions
                item {
                    Text(
                        text = textQuestions,
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}

// Output the generated Answer Sheet contents
@Composable
fun AnswerSheetContent(answer: String) {
    BoxWithConstraints(modifier = Modifier.background(Color.White)) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight
        val horizontalPadding = maxWidth * 0.04f
        val contentHeight = maxHeight * 0.65f

        Box(
            modifier = Modifier
                .padding(horizontal = horizontalPadding, vertical = maxHeight * 0.02f)
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .height(contentHeight),
            contentAlignment = Alignment.Center
        ) {
            // Vertical scrollable list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = maxHeight * 0.02f)
            ) {
                // Show Answer
                item {
                    Text(
                        text = answer,
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}