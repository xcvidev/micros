package com.xcvi.micros.ui.destinations

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.xcvi.micros.ui.destinations.food.add.AddScreen
import com.xcvi.micros.ui.destinations.food.dashboard.FoodScreen
import com.xcvi.micros.ui.destinations.food.details.DetailsScreen
import com.xcvi.micros.ui.destinations.food.meal.MealScreen
import com.xcvi.micros.ui.destinations.food.scan.ScanScreen
import com.xcvi.micros.ui.destinations.stats.StatsScreen
import com.xcvi.micros.ui.destinations.weight.WeightScreen
import kotlinx.serialization.Serializable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Destinations(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = { BottomBar(navController) }
        ) { scaffoldPadding ->
            NavHost(
                modifier = modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
                navController = navController,
                startDestination = FoodGraph.label
            ) {
                //main graph
                composable(FoodGraph.label) {
                    FoodScreen(
                        navController = navController,
                        topAppBarTitle = stringResource(R.string.food_log_app_bar_title),
                        aminoTitle = stringResource(R.string.amino_acids),
                        macroTitle = stringResource(R.string.macros),
                        mineralTitle = stringResource(R.string.minerals),
                        vitaminTitle = stringResource(R.string.vitamins),
                    )
                }
                composable(WeightGraph.label) {
                    WeightScreen(
                        topAppBarTitle = stringResource(R.string.weight_manager),
                        deleteDialogTitle = stringResource(R.string.delete),
                        deleteDialogText = stringResource(R.string.are_you_sure_you_want_to_delete_this_weight_entry),
                        noWeightsText = stringResource(R.string.no_weights_measured_this_week),
                        deleteDialogButtonText = stringResource(R.string.delete),
                        saveButtonText = stringResource(R.string.save)
                    )
                }
                composable(StatsGraph.label) {
                    StatsScreen(
                        topAppBarTitle = stringResource(R.string.stats),
                        bottomBarPadding = scaffoldPadding.calculateBottomPadding(),
                        noFoodDataText = stringResource(R.string.no_food_data),
                        noWeightDataText = stringResource(R.string.no_measured_weights),
                        maxLabel = stringResource(R.string.max),
                        minLabel = stringResource(R.string.min),
                        avgLabel = stringResource(R.string.avg)
                    )
                }

                //food graph
                slidingComposable<FoodGraph.Meal> {
                    val args = it.toRoute<FoodGraph.Meal>()
                    MealScreen(
                        navController = navController,
                        date = args.date,
                        meal = args.meal,
                        topAppBarText = stringResource(R.string.meal) + " ${args.meal}",
                        inputDialogTitle = stringResource(R.string.save),
                        inputDialogPlaceholder = stringResource(R.string.enter_a_name),
                        saveMealButtonText = stringResource(R.string.save_custom_meal),
                        macroTitle = stringResource(R.string.macros),
                        aminoTitle = stringResource(R.string.amino_acids),
                        mineralTitle = stringResource(R.string.minerals),
                        vitaminTitle = stringResource(R.string.vitamins)
                    )
                }
                slidingComposable<FoodGraph.Add> {
                    val promptFieldPlaceholders = listOf(
                        stringResource(R.string.describe_your_food),
                        stringResource(R.string.try_a_plate_of_pasta_with_tomato_sauce),
                        stringResource(R.string.describe_what_you_ate),
                    )
                    val placeHolder = remember { promptFieldPlaceholders.random() }
                    val args = it.toRoute<FoodGraph.Add>()
                    AddScreen(
                        navController = navController,
                        date = args.date,
                        meal = args.meal,
                        inputFieldPlaceHolder = placeHolder,
                        scanButtonText = stringResource(R.string.scan_barcode),
                        generatingIndicatorText = stringResource(R.string.generating),
                        proteinLabel = stringResource(R.string.protein),
                        carbsLabel = stringResource(R.string.carbs),
                        fatsLabel = stringResource(R.string.fats),
                        recentlyAddedText = stringResource(R.string.recently_added),
                    )
                }
                slidingComposable<FoodGraph.Scan> {
                    val args = it.toRoute<FoodGraph.Scan>()
                    ScanScreen(
                        navController = navController,
                        date = args.date,
                        meal = args.meal,
                        scanFailureMessage = stringResource(R.string.product_not_found),
                        scanHintText = stringResource(R.string.scan_product_barcode),
                        allowButtonText = stringResource(R.string.allow),
                        cancelButtonText = stringResource(R.string.cancel),
                        permissionDialogTitle = stringResource(R.string.permission_required),
                        permissionDialogText = stringResource(R.string.this_app_requires_camera_permission_to_scan_barcodes),
                        permissionDeniedText = stringResource(R.string.camera_permission_denied_text),
                        openSettingsButtonText = stringResource(R.string.open_settings),
                        failureDialogText = stringResource(R.string.retry_dialog_text),
                        retryButtonText = stringResource(R.string.retry),
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
                        errorTitle = stringResource(R.string.error),
                        errorMessage = stringResource(R.string.something_went_wrong),
                        aminoTitle = stringResource(R.string.amino_acids_for_100_g),
                        macroTitle = stringResource(R.string.macros_for_100_g),
                        mineralTitle = stringResource(R.string.minerals_for_100_g),
                        vitaminTitle = stringResource(R.string.vitamins_for_100_g),
                    )
                }
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
    data class Add(
        val meal: Int,
        val date: Int,
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

