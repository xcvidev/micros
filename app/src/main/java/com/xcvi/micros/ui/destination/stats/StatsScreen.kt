package com.xcvi.micros.ui.destination.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import com.xcvi.micros.ui.core.OnNavigation
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    bottomBarPadding: Dp,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = koinViewModel()
) {
    OnNavigation {
        viewModel.getData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Trends")
                }
            )
        },
    ) { padding ->
        Column(
            modifier = modifier.padding(bottom = bottomBarPadding, top = padding.calculateTopPadding())
        ) {

        }
    }

}

