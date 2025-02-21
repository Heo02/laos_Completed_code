package com.example.laostack.view.meaning

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.laostack.R
import com.example.laostack.api.APIInterface
import com.example.laostack.ui.theme.pretendard
import com.example.laostack.viewModel.meaning.CreateCardViewModel
import java.io.File

// CreateCardView.kt
// Workbook Card List
@Composable
fun WorkbookList(
    context: Context,
    navController: NavController,
    apiService: APIInterface
) {
    val viewModel: CreateCardViewModel =
        viewModel(factory = CreateCardViewModel.provideFactory(context, apiService))

    // State variables
    val workbooks by viewModel.workbooks.collectAsState()
    val deleteResult by viewModel.deleteResult.collectAsState()
    val pdfFilePath by viewModel.pdfFilePath.collectAsState()
    var pdfOpened by remember { mutableStateOf(false) }

    // Dialog state variables
    var showDeleteDialog by remember { mutableStateOf(false) }
    var workbookToDelete by remember { mutableStateOf<Int?>(null) }

    // Refresh the current page
    fun refreshCurrentPage(navController: NavController) {
        navController.navigate("meaning") {
            popUpTo("main") { saveState = true }
            launchSingleTop = true
        }
    }
    // Handle delete result
    LaunchedEffect(deleteResult) {
        deleteResult?.fold(
            onSuccess = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                refreshCurrentPage(navController)
            },
            onFailure = { error ->
                Toast.makeText(
                    context,
                    context.getString(R.string.delete_failed, error.message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    // Open PDF file when download is complete
    LaunchedEffect(pdfFilePath) {
        if (pdfFilePath != null && !pdfOpened) {
            val file = File(pdfFilePath!!)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                context.startActivity(intent) // Launch PDF viewer
                pdfOpened = true
                viewModel.resetPdfFilePath()
                refreshCurrentPage(navController)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.no_pdf_viewer),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    // Create to Workbook card list
    LazyColumn {
        items(workbooks) { workbook ->
            CreateCard(
                wbId = workbook.wb_id,
                wbCreate = workbook.wb_create,
                onDeleteClick = {
                    workbookToDelete = workbook.wb_id
                    showDeleteDialog = true
                },
                onWbOpenClick = { viewModel.downloadWorkbook(workbook.wb_id) },
                onAlOpenClick = { viewModel.downloadAnswer(workbook.wb_id) }
            )
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        DeleteDialog(
            onConfirm = {
                workbookToDelete?.let { viewModel.deleteWorkbook(it) }
            },
            onDismiss = {
                showDeleteDialog = false
                workbookToDelete = null
            }
        )
    }
}

// CreateCardView.kt
// Workbook Card
@Composable
fun CreateCard(
    wbId: Int, // Workbook ID
    wbCreate: String, // Creation date
    onDeleteClick: () -> Unit, // Callback for delete button click
    onWbOpenClick: () -> Unit, // Callback for opening the problem sheet
    onAlOpenClick: () -> Unit // Callback for opening the answer sheet
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color(0xff486284), RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display workbook title with its ID
                Text(
                    text = stringResource(R.string.workbook) + " " + wbId.toString(),
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                )
                // Delete button
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "Delete",
                        tint = Color(0xff486284), // Icon color
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Display workbook creation date
            Text(
                text = wbCreate,
                style = TextStyle(
                    fontFamily = pretendard,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Open Problem Sheet Button
                Button(
                    onClick = onWbOpenClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .border(1.dp, Color(0xff486284), RoundedCornerShape(5.dp))
                ) {
                    Text(
                        text = stringResource(R.string.workbook),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xff486284)
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Open Answer Sheet Button
                Button(
                    onClick = onAlOpenClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .border(1.dp, Color(0xff486284), RoundedCornerShape(5.dp))
                ) {
                    Text(
                        text = stringResource(R.string.answer),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xff486284)
                        )
                    )
                }
            }
        }
    }
}

// CreateCardView.kt
// Delete Workbook Dialog
@Composable
fun DeleteDialog(
    onConfirm: () -> Unit, // Function to execute when delete is confirmed
    onDismiss: () -> Unit // Function to execute when dialog is dismissed
) {
    BoxWithConstraints {
        val maxHeight = maxHeight
        val buttonPadding = maxHeight * 0.02f

        // AlertDialog to confirm workbook deletion
        AlertDialog(
            onDismissRequest = onDismiss, // Close dialog when tapped outside
            title = {
                // Dialog title
                Text(
                    text = stringResource(R.string.delete_workbook),
                    modifier = Modifier.padding(bottom = maxHeight * 0.01f)
                )
            },
            text = {
                // Dialog content
                Text(
                    text = stringResource(R.string.delete_confirmation),
                    modifier = Modifier.padding(bottom = maxHeight * 0.02f)
                )
            },
            confirmButton = {
                // Delete button
                TextButton(
                    onClick = {
                        onConfirm() // Execute delete action
                        onDismiss() // Close dialog
                    },
                    modifier = Modifier.padding(vertical = buttonPadding)
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    )
                }
            },
            dismissButton = {
                // Cancel button
                TextButton(
                    onClick = onDismiss, // Close dialog
                    modifier = Modifier.padding(vertical = buttonPadding)
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    )
                }
            },
            containerColor = Color.White
        )
    }
}


// Create Preview at the bottom of CreateCardView.kt
@Preview(showBackground = true)
@Composable
fun CreateCardPreview() {
    CreateCard(
        wbId = 1,
        wbCreate = "2025-00-00",
        onDeleteClick = {  },
        onWbOpenClick = {  },
        onAlOpenClick = {  }
    )
}