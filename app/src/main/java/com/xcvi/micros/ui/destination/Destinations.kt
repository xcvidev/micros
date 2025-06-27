package com.xcvi.micros.ui.destination

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.xcvi.micros.ui.destination.food.add.AddScreen
import com.xcvi.micros.ui.destination.food.dashboard.FoodScreen
import com.xcvi.micros.ui.destination.food.details.DetailsScreen
import com.xcvi.micros.ui.destination.food.meal.MealScreen
import com.xcvi.micros.ui.destination.food.scan.ScanScreen
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
                startDestination = Food.label
            ) {
                //main graph
                composable(Food.label) {
                    FoodScreen(navController, scaffoldPadding.calculateBottomPadding())
                }
                composable(Weight.label) {
                    //WeightScreen(navController, scaffoldPadding)
                }
                composable(Stats.label) {
                    //StatsScreen(navController, scaffoldPadding)
                }

                //food graph
                slidingComposable<Food.Meal> {
                    val args = it.toRoute<Food.Meal>()
                    MealScreen(navController = navController, date = args.date, meal = args.meal)
                }
                slidingComposable<Food.Add> {
                    val args = it.toRoute<Food.Add>()
                    AddScreen(navHostController = navController, date = args.date, meal = args.meal)
                }
                slidingComposable<Food.Scan> {
                    val args = it.toRoute<Food.Scan>()
                    ScanScreen(navHostController =  navController, date = args.date, meal = args.meal)
                }
                slidingComposable<Food.Details> {
                    val args = it.toRoute<Food.Details>()
                    DetailsScreen(navHostController = navController, date = args.date, meal = args.meal, amount = args.amount)
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
        Food, Weight, Stats
    )
    val showBottomBar = currentDestination in bottomBarDestinations
    if (showBottomBar) {
        BottomAppBar {
            bottomBarDestinations.forEach { destination ->
                val selected = currentDestination == destination
                val label = when (destination) {
                    Food -> stringResource(R.string.bottombar_label_food)
                    Weight -> stringResource(R.string.bottombar_label_weight)
                    Stats -> stringResource(R.string.bottombar_label_stats)
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
                            Icon(destination.selectedIcon, contentDescription = destination.label)
                        } else {
                            Icon(destination.unselectedIcon, contentDescription = destination.label)
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
    val selectedIcon: ImageVector
    val unselectedIcon: ImageVector
}

@Serializable
data object Food : Destination {
    override val label = "food_label"
    override val selectedIcon = Icons.Filled.Fastfood
    override val unselectedIcon = Icons.Outlined.Fastfood

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
        val amount: Int
    )

}

@Serializable
data object Weight : Destination {
    override val label = "weight_label"
    override val selectedIcon = Icons.Filled.MonitorWeight
    override val unselectedIcon = Icons.Outlined.MonitorWeight
}

@Serializable
data object Stats : Destination {
    override val label = "stats_label"
    override val selectedIcon = Icons.Filled.QueryStats
    override val unselectedIcon = Icons.Outlined.QueryStats
}


fun getCurrentTypedDestination(entry: NavBackStackEntry?): Destination? {
    entry?.destination?.hierarchy?.forEach { destination ->
        when (destination.route) {
            Food.label -> return Food
            Weight.label-> return Weight
            Stats.label -> return Stats
        }
    }
    return null
}

