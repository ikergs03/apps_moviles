package com.example.mylibrary.ui.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mylibrary.R
import com.example.mylibrary.data.model.ItemStatus
import com.example.mylibrary.data.model.ItemType
import com.example.mylibrary.data.model.LibraryItem
import com.example.mylibrary.databinding.ItemLibraryBinding

class LibraryAdapter(
	private val onItemClick: (LibraryItem) -> Unit,
	private val onItemLongClick: (LibraryItem) -> Unit
) : RecyclerView.Adapter<LibraryAdapter.LibraryHolder>() {

	private var data: List<LibraryItem> = emptyList()

	fun updateData(newData: List<LibraryItem>) {
		data = newData
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = ItemLibraryBinding.inflate(inflater, parent, false)
		return LibraryHolder(binding)
	}

	override fun getItemCount(): Int = data.size

	override fun onBindViewHolder(holder: LibraryHolder, position: Int) {
		holder.bind(data[position])
	}

	inner class LibraryHolder(
		private val binding: ItemLibraryBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(item: LibraryItem) {
			binding.tvTitle.text = item.title
			binding.tvAuthor.text = item.author.ifBlank { item.year }
			binding.tvStatus.text = when (item.status) {
				ItemStatus.PENDING -> "Pendiente"
				ItemStatus.IN_PROGRESS -> "En progreso"
				ItemStatus.COMPLETED -> "Completado"
				ItemStatus.ABANDONED -> "Abandonado"
			}
			binding.tvType.text = when (item.type) {
				ItemType.BOOK -> "📚 Libro"
				ItemType.MOVIE -> "🎬 Película"
				ItemType.VIDEOGAME -> "🎮 Videojuego"
			}

			if (item.coverUrl.isNotBlank()) {
				Glide.with(binding.root.context)
					.load(item.coverUrl)
					.placeholder(R.drawable.ic_placeholder)
					.error(R.drawable.ic_placeholder)
					.centerCrop()
					.into(binding.ivCover)
			} else {
				binding.ivCover.setImageResource(R.drawable.ic_placeholder)
			}

			binding.root.setOnClickListener { onItemClick(item) }
			binding.root.setOnLongClickListener {
				onItemLongClick(item)
				true
			}
		}
	}
}
