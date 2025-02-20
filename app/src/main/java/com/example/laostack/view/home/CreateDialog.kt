package com.example.laostack.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.laostack.R
import com.example.laostack.ui.theme.pretendard

// CreateDialog.kt
// Dialog that receives a response to regeneration
@Composable
fun CustomConfirmDialog(
    showDialog: Boolean, // Boolean flag to determine whether the dialog is displayed
    onDismissRequest: () -> Unit, // Event triggered when the dialog is dismissed
    onConfirmClick: () -> Unit // Event triggered when the confirm button is clicked
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = true, // Allows dismissal when the back button is pressed
                dismissOnClickOutside = true // Allows dismissal when clicking outside the dialog
            )
        ) {
            BoxWithConstraints {
                val maxWidth = maxWidth
                val maxHeight = maxHeight
                val horizontalPadding = maxWidth * 0.05f

                Surface(
                    shape = MaterialTheme.shapes.medium, // Apply rounded corners to the dialog
                    tonalElevation = 8.dp, // Add shadow effect
                    color = Color.White,
                    modifier = Modifier.padding(horizontalPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(maxWidth * 0.06f)
                            .fillMaxWidth()
                            .background(Color.White),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Title text
                        Text(
                            text = stringResource(R.string.problem_recreation),
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(maxHeight * 0.02f))
                        // Description text
                        Text(
                            text = stringResource(R.string.recreate_confirmation),
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(maxHeight * 0.03f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Cancel button
                            Button(
                                onClick = onDismissRequest,
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, Color(0xffD3DCE7), RoundedCornerShape(10.dp))
                                    .height(maxHeight * 0.05f),
                            ) {
                                Text(
                                    stringResource(R.string.cancel),
                                    color = Color(0xFF486284),
                                    style = TextStyle(
                                        fontFamily = pretendard,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(maxWidth * 0.04f))
                            // Confirm button
                            Button(
                                onClick = onConfirmClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(maxHeight * 0.05f),
                                colors = ButtonDefaults.buttonColors(Color(0xFFD3DCE7)),
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                Text(
                                    stringResource(R.string.confirm),
                                    color = Color(0xff486284),
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
        }
    }
}

// Dialog that receives a response to save PDF
@Composable
fun CustomPDFAlertDialog(
    showDialog: Boolean, // Boolean flag to determine whether the dialog is displayed
    onDismissRequest: () -> Unit, // Event triggered when the dialog is dismissed
    onConfirmClick: (String) -> Unit // Event triggered when the confirm button is clicked, passing a specific string
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = true, // Allows dismissal when the back button is pressed
                dismissOnClickOutside = true // Allows dismissal when clicking outside the dialog
            ),
        ) {
            BoxWithConstraints {
                val maxWidth = maxWidth
                val maxHeight = maxHeight
                val horizontalPadding = maxWidth * 0.05f

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp, // Add shadow effect
                    color = Color.White,
                    modifier = Modifier.padding(horizontalPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(maxWidth * 0.06f)
                            .fillMaxWidth()
                            .background(Color.White),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Title text
                        Text(
                            text = stringResource(R.string.save_as_pdf_title),
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(maxHeight * 0.03f))
                        // First instruction message
                        Text(
                            text = stringResource(R.string.pdf_save_instruction1),
                            color = Color.Black,
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp
                            ),
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(maxHeight * 0.01f))
                        // Second instruction message
                        Text(
                            text = stringResource(R.string.pdf_save_instruction2),
                            color = Color.Black,
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp
                            ),
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(maxHeight * 0.03f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Cancel button
                            Button(
                                onClick = { onDismissRequest() },
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, Color(0xffD3DCE7), RoundedCornerShape(10.dp))
                                    .height(maxHeight * 0.05f),
                            ) {
                                Text(
                                    stringResource(R.string.cancel),
                                    style = TextStyle(
                                        fontFamily = pretendard,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 13.sp
                                    ),
                                    color = Color(0xFF3B4A66)
                                )
                            }

                            Spacer(modifier = Modifier.width(maxWidth * 0.04f))

                            // Save button
                            Button(        // Pass "noStackWorkbook" to trigger PDF saving
                                onClick = { onConfirmClick("noStackWorkbook") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(maxHeight * 0.05f),
                                colors = ButtonDefaults.buttonColors(Color(0xFFD3DCE7)),
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                Text(
                                    stringResource(R.string.save),
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
}

// Dialog displayed while Workbook recreation is in progress
@Composable
fun CustomLoadingDialog(
    showDialog: Boolean, // Boolean flag to determine whether the dialog is displayed
    onDismissRequest: () -> Unit // Event triggered when the dialog is dismissed
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = false, // Prevents dismissing the dialog when the back button is pressed
                dismissOnClickOutside = false // Prevents dismissing the dialog when tapping outside
            )
        ) {
            BoxWithConstraints {
                val maxWidth = maxWidth
                val maxHeight = maxHeight
                val horizontalPadding = maxWidth * 0.05f

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontalPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(maxWidth * 0.06f)
                            .fillMaxWidth()
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Displaying an image icon while processing
                        Image(
                            painter = painterResource(R.drawable.icon_creating),
                            contentDescription = "Creating...",
                            modifier = Modifier.size(maxHeight * 0.12f)
                        )

                        Spacer(modifier = Modifier.height(maxHeight * 0.02f))

                        // Main message indicating the problem is being recreated
                        Text(
                            text = stringResource(R.string.recreating_problem),
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(maxHeight * 0.01f))

                        // Subtext asking the user to wait
                        Text(
                            text = stringResource(R.string.please_wait),
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.height(maxHeight * 0.03f))

                        // Cancel button allowing the user to stop the process
                        Button(
                            onClick = onDismissRequest,
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .border(1.dp, Color(0xffD3DCE7), RoundedCornerShape(10.dp))
                                .fillMaxWidth()
                                .height(maxHeight * 0.05f),
                        ) {
                            Text(
                                stringResource(R.string.cancel),
                                color = Color(0xFF3B4A66),
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
    }
}