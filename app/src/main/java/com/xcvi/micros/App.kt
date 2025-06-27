package com.xcvi.micros

import android.app.Application
import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.ui.destination.food.add.AddViewModel
import com.xcvi.micros.ui.destination.food.dashboard.FoodViewModel
import com.xcvi.micros.ui.destination.food.details.DetailsViewModel
import com.xcvi.micros.ui.destination.food.meal.MealViewModel
import com.xcvi.micros.ui.destination.food.scan.ScanViewModel
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
        }

        val repositoryModule = module {
            single { FoodRepository() }
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