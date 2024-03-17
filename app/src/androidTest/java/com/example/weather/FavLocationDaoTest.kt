import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weather.DB.FavLocationDB
import com.example.weather.DB.FavLocationDao
import com.example.weather.Pojo.FavoriteLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class FavLocationDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: FavLocationDB
    private lateinit var dao: FavLocationDao
    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FavLocationDB::class.java
        ).allowMainThreadQueries().build()

        dao = db.getLocationDao()
    }

    @After
    fun shutdown() {
        db.close()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun insertAndRetrieveFavLocation() = testScope.runBlockingTest {
        // Given
        val location = FavoriteLocation(
            1,
            43.77,
            35.89,
            "Test Address (EN)",
            "Test Address (AR)"
        )

        // When
        dao.insertFavLocation(location)

        // Then
        val locations = dao.getAllFavLocation().first()
        assertThat("Inserted and retrieved location should match", locations.size, `is`(1))
        assertThat("Inserted and retrieved location should match", locations[0], `is`(location))
    }

    @Test
    fun deleteFavLocation() = testScope.runBlockingTest {
        // Given
        val location = FavoriteLocation(
            1,
            43.77,
            35.89,
            "Test Address (EN)",
            "Test Address (AR)"
        )
        dao.insertFavLocation(location)

        // When
        dao.deletLocation(location)

        // Then
        val locations = dao.getAllFavLocation().first()
        assertThat("After deletion, no locations should be retrieved", locations.size, `is`(0))
    }
}
