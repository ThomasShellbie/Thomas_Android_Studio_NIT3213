package com.s8113459.assignment2.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import com.s8113459.assignment2.domain.model.AppResult
import com.s8113459.assignment2.domain.model.Dashboard
import com.s8113459.assignment2.domain.model.DataError
import com.s8113459.assignment2.domain.repository.DashboardRepository
import com.s8113459.assignment2.util.MainDispatcherRule
import com.s8113459.assignment2.util.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: DashboardRepository = mockk()

    private fun createViewModel(keypass: String? = "art") = DashboardViewModel(
        savedStateHandle = SavedStateHandle(
            if (keypass != null) mapOf(DashboardViewModel.KEY_KEYPASS to keypass) else emptyMap(),
        ),
        dashboardRepository = repository,
    )

    @Test
    fun `loads entities for the keypass on creation`() = runTest {
        coEvery { repository.getDashboard("art") } returns AppResult.Success(TestData.dashboard)

        val viewModel = createViewModel()

        assertEquals(
            DashboardUiState.Success(entities = TestData.dashboard.entities, entityTotal = 2),
            viewModel.uiState.value,
        )
        coVerify(exactly = 1) { repository.getDashboard("art") }
    }

    @Test
    fun `shows loading until data arrives`() = runTest {
        val pending = CompletableDeferred<AppResult<Dashboard>>()
        coEvery { repository.getDashboard(any()) } coAnswers { pending.await() }

        val viewModel = createViewModel()
        assertEquals(DashboardUiState.Loading, viewModel.uiState.value)

        pending.complete(AppResult.Success(TestData.dashboard))
        assertTrue(viewModel.uiState.value is DashboardUiState.Success)
    }

    @Test
    fun `repository failure is exposed as error state`() = runTest {
        coEvery { repository.getDashboard(any()) } returns AppResult.Failure(DataError.Network)

        val viewModel = createViewModel()

        assertEquals(DashboardUiState.Error(DataError.Network), viewModel.uiState.value)
    }

    @Test
    fun `refresh after an error loads the data again`() = runTest {
        coEvery { repository.getDashboard(any()) } returnsMany listOf(
            AppResult.Failure(DataError.Network),
            AppResult.Success(TestData.dashboard),
        )
        val viewModel = createViewModel()

        viewModel.refresh()

        assertTrue(viewModel.uiState.value is DashboardUiState.Success)
        coVerify(exactly = 2) { repository.getDashboard("art") }
    }

    @Test
    fun `pull to refresh keeps the list visible while reloading`() = runTest {
        val pending = CompletableDeferred<AppResult<Dashboard>>()
        var calls = 0
        coEvery { repository.getDashboard(any()) } coAnswers {
            calls++
            if (calls == 1) AppResult.Success(TestData.dashboard) else pending.await()
        }
        val viewModel = createViewModel()

        viewModel.refresh()

        val refreshing = viewModel.uiState.value as DashboardUiState.Success
        assertTrue(refreshing.isRefreshing)
        assertEquals(TestData.dashboard.entities, refreshing.entities)

        pending.complete(AppResult.Success(TestData.dashboard))
        assertEquals(false, (viewModel.uiState.value as DashboardUiState.Success).isRefreshing)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `missing keypass argument fails fast`() {
        createViewModel(keypass = null)
    }
}
