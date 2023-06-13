package com.lttrung.notepro.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.databinding.FeatureItemBinding
import com.lttrung.notepro.ui.entities.Feature

class FeatureAdapter (private val listener: FeatureListener) : ListAdapter<Feature, FeatureAdapter.FeatureViewHolder>(object :
    DiffUtil.ItemCallback<Feature>() {
    override fun areItemsTheSame(oldItem: Feature, newItem: Feature): Boolean {
        return oldItem.id.name == newItem.id.name
    }

    override fun areContentsTheSame(oldItem: Feature, newItem: Feature): Boolean {
        return oldItem == newItem
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val binding = FeatureItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeatureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class FeatureViewHolder(val binding: FeatureItemBinding) : ViewHolder(binding.root) {
        fun bind(feature: Feature, listener: FeatureListener) {
            binding.icon.setOnClickListener {
                listener.onClick(feature)
            }
            binding.icon.setImageResource(feature.icon)
        }
    }

    interface FeatureListener {
        fun onClick(item: Feature)
    }
}