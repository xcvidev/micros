package com.xcvi.micros.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.xcvi.micros.R
import com.xcvi.micros.ui.core.slidingComposable
import com.xcvi.micros.ui.feature_food.search.SearchScreen
import com.xcvi.micros.ui.feature_food.dashoard.FoodScreen
import com.xcvi.micros.ui.feature_food.dashoard.FoodViewModel
import com.xcvi.micros.ui.feature_food.details.DetailsScreen
import com.xcvi.micros.ui.feature_food.meal.MealScreen
import com.xcvi.micros.ui.feature_food.search.SearchViewModel
import com.xcvi.micros.ui.feature_food.scan.ScanScreen
import com.xcvi.micros.ui.feature_stats.StatsScreen
import com.xcvi.micros.ui.feature_weight.WeightScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Destinations(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val foodViewModel: FoodViewModel = koinViewModel() // shared viewModel

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        bottomBar = { BottomBar(navController) }
    ) { scaffoldPadding ->

        NavHost(
            modifier = modifier
                .fillMaxSize(),
            navController = navController,
            startDestination = FoodGraph.label
        ) {
            composable(WeightGraph.label) {
                WeightScreen()
            }
            composable(StatsGraph.label) {
                StatsScreen(
                    bottomBarPadding = scaffoldPadding.calculateBottomPadding()
                )
            }

            //food graph
            composable(
                route = FoodGraph.label,
            ) {
                FoodScreen(
                    navController = navController,
                    viewModel = foodViewModel
                )
            }

            slidingComposable<FoodGraph.Meal> {
                val args = it.toRoute<FoodGraph.Meal>()
                MealScreen(
                    navController = navController,
                    date = args.date,
                    meal = args.meal,
                    viewModel = foodViewModel
                )
            }
            slidingComposable<FoodGraph.Search> {
                val args = it.toRoute<FoodGraph.Search>()
                SearchScreen(
                    navController = navController,
                    date = args.date,
                    meal = args.meal,
                )
            }
            slidingComposable<FoodGraph.Details> {
                val args = it.toRoute<FoodGraph.Details>()
                DetailsScreen(
                    navController = navController,
                    date = args.date,
                    meal = args.meal,
                    amount = args.amount,
                    barcode = args.barcode,
                )
            }
            slidingComposable<FoodGraph.Scan> {
                val args = it.toRoute<FoodGraph.Scan>()
                ScanScreen(
                    navController = navController,
                    date = args.date,
                    meal = args.meal,
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
) {
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentDestination = getCurrentTypedDestination(currentEntry)

    val bottomBarDestinations = listOf(
        FoodGraph, WeightGraph, StatsGraph
    )
    val showBottomBar = currentDestination in bottomBarDestinations
    if (showBottomBar) {
        BottomAppBar {
            bottomBarDestinations.forEach { destination ->
                val selected = currentDestination == destination
                val label = when (destination) {
                    FoodGraph -> stringResource(R.string.bottombar_label_food)
                    WeightGraph -> stringResource(R.string.bottombar_label_weight)
                    StatsGraph -> stringResource(R.string.bottombar_label_stats)
                }
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(destination.label) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    },
                    icon = {
                        if (currentDestination == destination) {
                            Icon(
                                painterResource(destination.selectedIcon),
                                contentDescription = destination.label
                            )
                        } else {
                            Icon(
                                painterResource(destination.unselectedIcon),
                                contentDescription = destination.label
                            )
                        }
                    },
                    label = { Text(label) }
                )
            }
        }
    }

}


sealed interface Destination {
    val label: String
    val selectedIcon: Int
    val unselectedIcon: Int
}

@Serializable
data object FoodGraph : Destination {
    override val label = "food_label"
    override val selectedIcon = R.drawable.ic_food
    override val unselectedIcon = R.drawable.ic_food_outlined

    @Serializable
    data class Meal(
        val meal: Int,
        val date: Int,
    )

    @Serializable
    data class Search(
        val meal: Int,
        val date: Int
    )

    @Serializable
    data class Scan(
        val meal: Int,
        val date: Int,
    )

    @Serializable
    data class Details(
        val meal: Int,
        val date: Int,
        val amount: Int,
        val barcode: String,
    )

}

@Serializable
data object WeightGraph : Destination {
    override val label = "weight_label"
    override val selectedIcon = R.drawable.ic_scale_filled
    override val unselectedIcon = R.drawable.ic_scale
}

@Serializable
data object StatsGraph : Destination {
    override val label = "stats_label"
    override val selectedIcon = R.drawable.ic_graph
    override val unselectedIcon = R.drawable.ic_graph_outlined
}


fun getCurrentTypedDestination(entry: NavBackStackEntry?): Destination? {
    entry?.destination?.hierarchy?.forEach { destination ->
        when (destination.route) {
            FoodGraph.label -> return FoodGraph
            WeightGraph.label -> return WeightGraph
            StatsGraph.label -> return StatsGraph
        }
    }
    return null
}

