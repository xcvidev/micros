package com.xcvi.micros.ui.destinations.food.scan

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.ui.destinations.FoodGraph
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    navController: NavHostController,
    date: Int,
    meal: Int,
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel = koinViewModel(),
    scanFailureMessage: String = "Product not found.",
    scanHintText: String = "Scan product barcode",
    allowButtonText: String = "Allow",
    cancelButtonText: String = "Cancel",
    permissionDialogTitle: String = "Permission required",
    permissionDialogText: String = "This app requires camera permission to scan barcodes.",
    permissionDeniedText: String = "Camera permission denied. Please grant the permission in app settings.",
    openSettingsButtonText: String = "Open settings",
) {
    val state = viewModel.state
    val context = LocalContext.current


    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Text("Fetching product...", color = Color.White)
                }
            }
        } else {
            MaterialCameraScreen(
                context = context,
                onScan = { barcode, barcodeScanner ->
                    barcodeScanner?.close()
                    viewModel.cacheScan(
                        barcode = barcode,
                        onSuccess = {
                            navController.navigate(
                                FoodGraph.Details(
                                    meal = meal,
                                    date = date,
                                    barcode = barcode,
                                    amount = 100
                                )
                            ) {
                                popUpTo(FoodGraph.Scan(meal = meal, date = date)) {
                                    inclusive = true
                                }
                            }
                        },
                        onFailure = {
                            Toast.makeText(
                                context,
                                scanFailureMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate(
                                FoodGraph.Scan(meal = meal, date = date)
                            ) {
                                popUpTo(FoodGraph.Scan(meal = meal, date = date)) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                },
                onGoBack = { navController.popBackStack() },
                scanHintText = scanHintText,
                allowButtonText = allowButtonText,
                cancelButtonText = cancelButtonText,
                permissionDialogTitle = permissionDialogTitle,
                permissionDialogText = permissionDialogText,
                permissionDeniedText = permissionDeniedText,
                openSettingsButtonText = openSettingsButtonText

            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(4.dp)
                ,
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = ""
                )
            }
        }
    }
}



