package com.vsimpleton.photoeditor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.blankj.utilcode.util.ConvertUtils
import com.vsimpleton.photoeditor.R

class EditAdapter : RecyclerView.Adapter<EditAdapter.ViewHolder>() {

    private var lists = listOf<String>()
    var onItemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<ImageView>(R.id.ivImg).load("file:///android_asset/${lists[position]}") {
                crossfade(true)
                transformations(CircleCropTransformation())
                size(ConvertUtils.dp2px(48f), ConvertUtils.dp2px(48f))
            }
            setOnClickListener {
                onItemClickListener?.invoke(position)
            }
        }
    }

    override fun getItemCount() = lists.size

    fun setData(lists: List<String>) {
        this.lists = lists
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}