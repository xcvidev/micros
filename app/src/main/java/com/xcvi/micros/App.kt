package com.xcvi.micros

import android.app.Application
import androidx.room.Room
import com.xcvi.micros.data.AppDatabase
import com.xcvi.micros.data.food.FoodRepository
import com.xcvi.micros.data.food.source.FoodApi
import com.xcvi.micros.data.food.source.FoodDao
import com.xcvi.micros.data.weight.WeightRepository
import com.xcvi.micros.data.weight.source.WeightDao
import com.xcvi.micros.ui.destinations.food.f3.AddViewModel
import com.xcvi.micros.ui.destinations.food.f1.FoodViewModel
import com.xcvi.micros.ui.destinations.food.f5.DetailsViewModel
import com.xcvi.micros.ui.destinations.food.f4.ScanViewModel
import com.xcvi.micros.ui.destinations.stats.StatsViewModel
import com.xcvi.micros.ui.destinations.weight.WeightViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(viewModelModule,repositoryModule, dbModule, apiModule)
        }
    }

    companion object {

        val viewModelModule = module {
            viewModel { FoodViewModel(get()) }
            //viewModel { MealViewModel(get()) }  // now sharing with FoodViewModel
            viewModel { AddViewModel(get()) }
            viewModel { ScanViewModel(get()) }
            viewModel { DetailsViewModel(get()) }
            viewModel { WeightViewModel(get()) }
            viewModel { StatsViewModel(get(), get()) }
        }

        val repositoryModule = module {
            single { FoodRepository(api = get<FoodApi>(), dao = get<FoodDao>()) }
            single { WeightRepository(dao = get<WeightDao>()) }
        }

        val apiModule = module {
            val scanClient = HttpClient(Android) {
                install(HttpTimeout) {
                    connectTimeoutMillis = 5000
                }

                install(JsonFeature) {
                    serializer = KotlinxSerializer(
                        Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }


            val key = BuildConfig.OPENAI_API_KEY

            val openAiClient = HttpClient(Android) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 5000
                }

                install(JsonFeature) {
                    serializer = KotlinxSerializer(
                        Json {
                            ignoreUnknownKeys = true
                            prettyPrint = true
                            isLenient = true
                        }
                    )
                }

                defaultRequest {
                    header("Authorization", "Bearer $key")
                    contentType(ContentType.Application.Json)
                }
            }

            single {
                FoodApi(
                    aiClient = openAiClient,
                    scanClient = scanClient
                )
            }

        }

        val dbModule = module {
            single {
                Room.databaseBuilder(
                    androidContext(),
                    AppDatabase::class.java,
                    "app_db"
                ).build()
            }
            single { get<AppDatabase>().weightDao() }
            single { get<AppDatabase>().foodDao() }

            /*
            single {
                Room.databaseBuilder(
                    androidContext(),
                    ProductDatabase::class.java,
                    "product_db"
                ).createFromAsset("products_with_tags.db").build()
            }
            single { get<ProductDatabase>().productDao() }
            */
        }
    }
}