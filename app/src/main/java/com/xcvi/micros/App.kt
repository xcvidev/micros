package com.xcvi.micros

import android.app.Application
import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.WeightRepository
import com.xcvi.micros.ui.destinations.food.add.AddViewModel
import com.xcvi.micros.ui.destinations.food.dashboard.FoodViewModel
import com.xcvi.micros.ui.destinations.food.details.DetailsViewModel
import com.xcvi.micros.ui.destinations.food.meal.MealViewModel
import com.xcvi.micros.ui.destinations.food.scan.ScanViewModel
import com.xcvi.micros.ui.destinations.stats.StatsViewModel
import com.xcvi.micros.ui.destinations.weight.WeightViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
        }
        loadKoinModules(
            listOf(
                viewModelModule,repositoryModule
                //dbModule, apiModule,
            )
        )
    }

    companion object {

        val viewModelModule = module {
            viewModel { FoodViewModel(get()) }
            viewModel { MealViewModel(get()) }
            viewModel { AddViewModel(get()) }
            viewModel { ScanViewModel(get()) }
            viewModel { DetailsViewModel(get()) }
            viewModel { WeightViewModel(get()) }
            viewModel { StatsViewModel(get(), get()) }
        }

        val repositoryModule = module {
            single { FoodRepository() }
            single { WeightRepository() }
        }

        val apiModule = module {
            /*
            single { AiAssistantApi() }
            single { ScanApi() }

             */
        }

        val dbModule = module {
            /*
            single {
                Room.databaseBuilder(
                    androidContext(),
                    ProductDatabase::class.java,
                    "product_db"
                ).createFromAsset("products_with_tags.db").build()
            }
            single { get<ProductDatabase>().productDao() }

            single {
                Room.databaseBuilder(
                    androidContext(),
                    AppDatabase::class.java,
                    "app_db"
                ).build()
            }
            single { get<AppDatabase>().weightDao() }
            single { get<AppDatabase>().foodDao() }

             */
        }
    }
}