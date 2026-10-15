package com.s8113459.assignment2.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.s8113459.assignment2.databinding.ActivityDetailsBinding
import com.s8113459.assignment2.databinding.ItemDetailFieldBinding
import com.s8113459.assignment2.domain.model.Entity
import com.s8113459.assignment2.ui.common.humanizeKey
import dagger.hilt.android.AndroidEntryPoint

/** Screen 3 — every property of the selected entity, including the description. */
@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.tvHeaderTitle.text = viewModel.title
        binding.tvHeaderLabel.text = viewModel.fields.firstOrNull()?.key?.let(::humanizeKey).orEmpty()
        binding.tvInitial.text = viewModel.title.firstOrNull { it.isLetterOrDigit() }?.uppercase() ?: "?"

        // One label/value row per property
        binding.fieldsContainer.removeAllViews()
        viewModel.fields.forEachIndexed { index, field ->
            val row = ItemDetailFieldBinding.inflate(layoutInflater, binding.fieldsContainer, false)
            row.tvLabel.text = humanizeKey(field.key)
            row.tvValue.text = field.value.ifBlank { "—" }
            row.divider.visibility = if (index == viewModel.fields.lastIndex) View.GONE else View.VISIBLE
            binding.fieldsContainer.addView(row.root)
        }

        val description = viewModel.description
        binding.cardDescription.visibility = if (description.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.tvDescription.text = description
    }

    companion object {
        fun newIntent(context: Context, entity: Entity): Intent =
            Intent(context, DetailsActivity::class.java)
                .putExtra(DetailsViewModel.KEY_ENTITY, entity)
    }
}
