package com.s8113459.assignment2.data

import com.google.gson.JsonParser
import com.s8113459.assignment2.data.remote.ApiService
import com.s8113459.assignment2.data.repository.AuthRepositoryImpl
import com.s8113459.assignment2.data.repository.DashboardRepositoryImpl
import com.s8113459.assignment2.domain.model.AppResult
import com.s8113459.assignment2.domain.model.DataError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Tests the repositories together with the real Retrofit/Gson stack against a local MockWebServer,
 * so request paths, JSON parsing and HTTP error mapping are all verified.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var api: ApiService
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun authRepository() = AuthRepositoryImpl(api, location = "sydney", ioDispatcher = dispatcher)
    private fun dashboardRepository() = DashboardRepositoryImpl(api, ioDispatcher = dispatcher)

    // ---------------------------------------------------------------- auth

    @Test
    fun `login posts credentials to the location auth endpoint and returns the keypass`() = runTest(dispatcher) {
        server.enqueue(MockResponse().setBody("""{"keypass":"art"}"""))

        val result = authRepository().login("12345678", "John")

        assertEquals(AppResult.Success("art"), result)
        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/sydney/auth", request.path)
        val body = JsonParser.parseString(request.body.readUtf8()).asJsonObject
        assertEquals("12345678", body["username"].asString)
        assertEquals("John", body["password"].asString)
    }

    @Test
    fun `login 400 maps to invalid credentials`() = runTest(dispatcher) {
        server.enqueue(MockResponse().setResponseCode(400))

        assertEquals(AppResult.Failure(DataError.InvalidCredentials), authRepository().login("12345678", "x"))
    }

    @Test
    fun `login 404 maps to not found`() = runTest(dispatcher) {
        server.enqueue(MockResponse().setResponseCode(404))

        assertEquals(AppResult.Failure(DataError.NotFound), authRepository().login("1", "x"))
    }

    @Test
    fun `login 500 maps to server error`() = runTest(dispatcher) {
        server.enqueue(MockResponse().setResponseCode(503))

        assertEquals(AppResult.Failure(DataError.Server(503)), authRepository().login("12345678", "John"))
    }

    @Test
    fun `login with blank keypass is treated as unknown error`() = runTest(dispatcher) {
        server.enqueue(MockResponse().setBody("""{"keypass":""}"""))

        assertEquals(AppResult.Failure(DataError.Unknown), authRepository().login("12345678", "John"))
    }

    @Test
    fun `dropped connection maps to network error`() = runTest(dispatcher) {
        // OkHttp may silently retry a failed connection, so queue several drops
        repeat(5) { server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)) }

        assertEquals(AppResult.Failure(DataError.Network), authRepository().login("12345678", "John"))
    }

    // ---------------------------------------------------------------- dashboard

    @Test
    fun `dashboard requests the keypass path and maps entities`() = runTest(dispatcher) {
        server.enqueue(
            MockResponse().setBody(
                """
                {"entities":[
                  {"artistName":"Vincent van Gogh","title":"The Starry Night","year":1889,"description":"Night sky"},
                  {"artistName":"Leonardo da Vinci","title":"Mona Lisa","year":1503,"description":"Portrait"}
                 ],
                 "entityTotal":2}
                """.trimIndent(),
            ),
        )

        val result = dashboardRepository().getDashboard("art")

        assertEquals("/dashboard/art", server.takeRequest().path)
        assertTrue(result is AppResult.Success)
        val dashboard = (result as AppResult.Success).data
        assertEquals(2, dashboard.entityTotal)
        assertEquals("Vincent van Gogh", dashboard.entities[0].title)
        assertEquals("Night sky", dashboard.entities[0].description)
        assertEquals(3, dashboard.entities[0].fields.size) // description is not a field
    }

    @Test
    fun `real mythology payload from the API is mapped correctly`() = runTest(dispatcher) {
        // Shape captured from GET /dashboard/mythology (trimmed to two entities)
        server.enqueue(
            MockResponse().setBody(
                """
                {"entities":[
                  {"name":"Zeus","culture":"Greek","domain":"Sky and Thunder","symbol":"Thunderbolt",
                   "parentage":"Cronus and Rhea","romanEquivalent":"Jupiter",
                   "description":"King of the Olympian gods and ruler of Mount Olympus."},
                  {"name":"Odin","culture":"Norse","domain":"Wisdom, Poetry, War, Death","symbol":"Spear Gungnir",
                   "parentage":"Borr and Bestla","romanEquivalent":"None",
                   "description":"The Allfather of the Norse gods."}
                 ],
                 "entityTotal":7}
                """.trimIndent(),
            ),
        )

        val dashboard = (dashboardRepository().getDashboard("mythology") as AppResult.Success).data

        assertEquals(7, dashboard.entityTotal)
        val zeus = dashboard.entities.first()
        assertEquals("Zeus", zeus.title)
        assertEquals(
            listOf("name", "culture", "domain", "symbol", "parentage", "romanEquivalent"),
            zeus.fields.map { it.key },
        )
        assertEquals("Jupiter", zeus.fields.last().value)
        assertEquals("King of the Olympian gods and ruler of Mount Olympus.", zeus.description)
        assertEquals(5, zeus.summaryFields.size) // dashboard card: everything except title and description
    }

    @Test
    fun `dashboard 404 maps to not found`() = runTest(dispatcher) {
        server.enqueue(MockResponse().setResponseCode(404))

        assertEquals(AppResult.Failure(DataError.NotFound), dashboardRepository().getDashboard("unknown"))
    }

    @Test
    fun `unexpected json shape maps to unknown error`() = runTest(dispatcher) {
        server.enqueue(MockResponse().setBody("""{"entities":"not a list","entityTotal":1}"""))

        assertEquals(AppResult.Failure(DataError.Unknown), dashboardRepository().getDashboard("art"))
    }
}
