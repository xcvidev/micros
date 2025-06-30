package com.xcvi.micros.ui.destinations.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.ui.core.DropDownChip
import com.xcvi.micros.ui.core.OnNavigation
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    bottomBarPadding: Dp,
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = koinViewModel()
) {
    OnNavigation {
        viewModel.getData()
    }

    var selectedFilter by remember { mutableStateOf("Week") }
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Trends")
                },
                actions = {
                    if (viewModel.state.hasData) {
                        DropDownChip(
                            options = listOf("Week", "Month"),
                            selectedOption = selectedFilter,
                            onOptionSelected = {
                                selectedFilter = it
                            }
                        )
                    }
                }
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = modifier.padding(
                bottom = bottomBarPadding,
                top = padding.calculateTopPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                FoodGraph(
                    data = if(selectedFilter == "Week") state.foodsByWeek else state.foodsByMonth,
                    modifier = modifier.fillMaxWidth(),
                    showDate = selectedFilter == "Week"
                )
            }

            item {
                WeightGraph(
                    data = if(selectedFilter == "Week") state.weightsByWeek else state.weightsByMonth,
                    modifier = modifier.fillMaxWidth(),
                    showDate = selectedFilter == "Week"
                )
            }
        }
    }

}


