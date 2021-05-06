package com.vsimpleton.photoeditor

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FontAdapter : RecyclerView.Adapter<FontAdapter.ViewHolder>() {

    private var lists = listOf<String>()
    var onItemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_font, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            holder.tvFont.typeface =
                Typeface.createFromAsset(context.assets, lists[position])
            setOnClickListener { onItemClickListener?.invoke(position) }
        }
    }

    override fun getItemCount() = lists.size

    fun setData(list: List<String>) {
        lists = list
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFont: TextView = itemView.findViewById(R.id.tvFont)
    }

}