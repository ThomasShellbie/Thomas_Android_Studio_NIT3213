package com.s8113459.assignment2.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.s8113459.assignment2.databinding.ItemEntityBinding
import com.s8113459.assignment2.domain.model.Entity
import com.s8113459.assignment2.ui.common.humanizeKey

/**
 * RecyclerView adapter for the dashboard. Each row shows a summary of the entity
 * (all properties except the description, as required by the spec).
 */
class EntityAdapter(
    private val onEntityClick: (Entity) -> Unit,
) : ListAdapter<Entity, EntityAdapter.EntityViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val binding = ItemEntityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntityViewHolder(binding, onEntityClick)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EntityViewHolder(
        private val binding: ItemEntityBinding,
        private val onEntityClick: (Entity) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entity: Entity) = with(binding) {
            val title = entity.title
            tvInitial.text = title.firstOrNull { it.isLetterOrDigit() }?.uppercase() ?: "?"
            tvTitle.text = title
            tvTitleLabel.text = entity.fields.firstOrNull()?.key?.let(::humanizeKey).orEmpty()

            val summary = entity.summaryFields.joinToString("\n") { "${humanizeKey(it.key)}: ${it.value}" }
            tvSummary.text = summary
            tvSummary.visibility = if (summary.isBlank()) View.GONE else View.VISIBLE

            root.setOnClickListener { onEntityClick(entity) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Entity>() {
        override fun areItemsTheSame(oldItem: Entity, newItem: Entity): Boolean =
            oldItem.fields == newItem.fields
        override fun areContentsTheSame(oldItem: Entity, newItem: Entity): Boolean =
            oldItem == newItem
    }
}
