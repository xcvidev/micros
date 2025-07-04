package com.xcvi.micros.ui.feature_food.scan

import android.annotation.SuppressLint
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.R
import com.xcvi.micros.ui.FoodGraph
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
    scanFailureMessage: String = stringResource(R.string.product_not_found),
    scanHintText : String= stringResource(R.string.scan_product_barcode),
    allowButtonText : String= stringResource(R.string.allow),
    cancelButtonText : String= stringResource(R.string.cancel),
    permissionDialogTitle: String = stringResource(R.string.permission_required),
    permissionDialogText: String = stringResource(R.string.this_app_requires_camera_permission_to_scan_barcodes),
    permissionDeniedText : String= stringResource(R.string.camera_permission_denied_text),
    openSettingsButtonText : String= stringResource(R.string.open_settings),
    failureDialogText : String= stringResource(R.string.retry_dialog_text),
    retryButtonText : String= stringResource(R.string.retry),
) {
    val state = viewModel.state
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val goToSearch = {
        navController.navigate(
            FoodGraph.Search(meal = meal, date = date)
        ) {
            popUpTo(FoodGraph.Search(meal = meal, date = date)) {
                inclusive = true
            }
        }
    }

    val resetScan = {
        navController.navigate(
            FoodGraph.Scan(meal = meal, date = date)
        ) {
            popUpTo(FoodGraph.Scan(meal = meal, date = date)) {
                inclusive = true
            }
        }
    }

    val goToDetails = { barcode: String ->
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
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false

            },
            title = {
                Text(scanFailureMessage)
            },
            text = { Text(failureDialogText) },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        resetScan()
                    }
                ) {
                    Text(retryButtonText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        goToSearch()
                    }
                ) {
                    Text(cancelButtonText)
                }
            }
        )
    }

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
                        onSuccess = { goToDetails(barcode) },
                        onFailure = {
                            barcodeScanner?.close()
                            showDialog = true
                        }
                    )
                },
                onGoBack = { goToSearch() },
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
                    .padding(4.dp),
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "",
                    tint = Color.White,
                )
            }
        }
    }
}



