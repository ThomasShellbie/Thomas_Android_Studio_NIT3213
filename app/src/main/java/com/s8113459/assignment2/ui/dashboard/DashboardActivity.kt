package com.s8113459.assignment2.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.s8113459.assignment2.R
import com.s8113459.assignment2.databinding.ActivityDashboardBinding
import com.s8113459.assignment2.ui.common.dashboardMessage
import com.s8113459.assignment2.ui.common.humanizeKey
import com.s8113459.assignment2.ui.details.DetailsActivity
import com.s8113459.assignment2.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/** Screen 2 — list of entities for the keypass, displayed in a RecyclerView. */
@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()

    private val adapter = EntityAdapter { entity ->
        startActivity(DetailsActivity.newIntent(this, entity))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = getString(R.string.dashboard_title)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) { logout(); true } else false
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        binding.btnRetry.setOnClickListener { viewModel.refresh() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: DashboardUiState) {
        binding.apply {
            progress.visibility = if (state is DashboardUiState.Loading) View.VISIBLE else View.GONE
            errorView.visibility = if (state is DashboardUiState.Error) View.VISIBLE else View.GONE
            swipeRefresh.visibility = if (state is DashboardUiState.Success) View.VISIBLE else View.GONE

            when (state) {
                DashboardUiState.Loading -> {
                    toolbar.subtitle = getString(R.string.topic_subtitle_loading, humanizeKey(viewModel.keypass))
                }
                is DashboardUiState.Success -> {
                    swipeRefresh.isRefreshing = state.isRefreshing
                    adapter.submitList(state.entities)
                    toolbar.subtitle = resources.getQuantityString(
                        R.plurals.topic_subtitle, state.entityTotal, humanizeKey(viewModel.keypass), state.entityTotal,
                    )
                    tvEmpty.visibility = if (state.entities.isEmpty()) View.VISIBLE else View.GONE
                }
                is DashboardUiState.Error -> {
                    swipeRefresh.isRefreshing = false
                    tvError.setText(state.error.dashboardMessage())
                }
            }
        }
    }

    private fun logout() {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
        )
    }

    companion object {
        fun newIntent(context: Context, keypass: String): Intent =
            Intent(context, DashboardActivity::class.java)
                .putExtra(DashboardViewModel.KEY_KEYPASS, keypass)
    }
}
