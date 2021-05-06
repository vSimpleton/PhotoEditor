package com.vsimpleton.photoeditor

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    private var lists = listOf<String>()
    var onItemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            holder.ivStroke.visibility = if (position == 1) View.VISIBLE else View.GONE
            holder.ivColor.setBackgroundResource(R.drawable.ic_pick_color)
            holder.ivColor.backgroundTintList =
                if (position == 0) null else ColorStateList.valueOf(Color.parseColor(lists[position]))
            setOnClickListener {
                onItemClickListener?.invoke(position)
            }
        }
    }

    override fun getItemCount() = lists.size

    fun setData(list: List<String>) {
        lists = list
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivColor: ImageView = itemView.findViewById(R.id.ivColor)
        val ivStroke: ImageView = itemView.findViewById(R.id.ivStroke)
    }

}