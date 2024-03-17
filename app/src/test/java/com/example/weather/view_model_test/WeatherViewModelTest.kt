import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.Model.WeatherViewModel
import com.example.weather.Pojo.FavoriteLocation
import com.example.weather.Pojo.Repo
import com.example.weather.Pojo.WeatherResponse
import com.example.weather.Utility.Utility
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FakeRepo : Repo {
    var fakeWeatherData: WeatherResponse? = null
    val fakeFavoriteLocations = mutableListOf<FavoriteLocation>()

    override suspend fun getWeather(latitude: Double, longitude: Double, language: String?, unit: String?): Flow<WeatherResponse> {
        return flow {
            emit(fakeWeatherData ?: WeatherResponse(/* Initialize with default test data */))
        }
    }

    override suspend fun getFavourites(): Flow<List<FavoriteLocation>> {
        return flow {
            emit(fakeFavoriteLocations)
        }
    }

    override suspend fun insertToFav(favoriteLocation: FavoriteLocation) {
        fakeFavoriteLocations.add(favoriteLocation)
    }

    override suspend fun deletFromFav(favoriteLocation: FavoriteLocation) {
        fakeFavoriteLocations.remove(favoriteLocation)
    }
}

@RunWith(AndroidJUnit4::class)
class WeatherViewModelTest {

    private lateinit var fakeRepo: FakeRepo
    private lateinit var viewModel: WeatherViewModel
    private lateinit var context: Context


    @get:Rule
    val myRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        fakeRepo = FakeRepo()
        viewModel = WeatherViewModel(fakeRepo, context)
    }

    @Test
    fun testGetDayWeather() {
        // Given
        val latitude = 43.77
        val longitude = 35.89
        val fakeWeatherData = WeatherResponse()
        fakeRepo.fakeWeatherData = fakeWeatherData

        // When
        viewModel.getDayWeather(latitude, longitude,null,null)

        // Then
        val weatherState = viewModel.forcast.value
        assertThat(weatherState, `is`(Utility.Loading))
    }

    @Test
    fun testInsertInDB() {
        // Given
        val location = FavoriteLocation(
            id = 1,
            latitude = 43.77,
            longitude = 35.89,
            address_en = "Test Address (EN)",
            address_ar = "Test Address (AR)"
        )

        // When
        viewModel.insertInDB(location)

        // Then
        val locations = runBlocking { fakeRepo.getFavourites().toList().first() }
        assertThat(locations.contains(location), `is`(true))
    }

    @Test
    fun testDeleteFromDB() {
        // Given
        val location = FavoriteLocation(
            id = 1,
            latitude = 43.77,
            longitude = 35.89,
            address_en = "Test Address (EN)",
            address_ar = "Test Address (AR)"
        )
        fakeRepo.fakeFavoriteLocations.add(location)

        // When
        viewModel.deletFromDB(location)

        // Then
        val locations = runBlocking { fakeRepo.getFavourites().toList().first() }
        assertThat(locations.contains(location), `is`(false))
    }
}
