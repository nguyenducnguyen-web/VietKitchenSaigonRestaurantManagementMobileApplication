package com.example.ungdungdatdoanonlinenhvietkitchensg.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.entity.LienHe

class LienHeAdapter(
    private var listLienHe: MutableList<LienHe>,
    private val onItemClick: (LienHe) -> Unit
) : RecyclerView.Adapter<LienHeAdapter.LienHeViewHolder>() {

    class LienHeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chuDe: TextView = itemView.findViewById(R.id.tv_title)
        val noiDung: TextView = itemView.findViewById(R.id.tv_content)
        val hinhAnh: TextView = itemView.findViewById(R.id.tv_image)
        val txt: TextView = itemView.findViewById(R.id.tv_filetxt)
        val excel: TextView = itemView.findViewById(R.id.tv_fileexcel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LienHeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return LienHeViewHolder(view)
    }

    override fun onBindViewHolder(holder: LienHeViewHolder, position: Int) {
        val lienHe = listLienHe[position]
        holder.chuDe.text = lienHe.chuDe
        holder.noiDung.text = lienHe.noiDung
        holder.hinhAnh.text = getFileName(lienHe.hinhAnh)
        holder.txt.text = getFileName(lienHe.filetxt)
        holder.excel.text = getFileName(lienHe.fileExcel)

        holder.itemView.setOnClickListener {
            onItemClick(lienHe)
        }
    }

    override fun getItemCount(): Int = listLienHe.size

    fun updateData(newList: List<LienHe>) {
        listLienHe.clear()
        listLienHe.addAll(newList)
        notifyDataSetChanged()
    }

    private fun getFileName(uriString: String?): String {
        if (uriString.isNullOrEmpty()) return "Không có file"
        return Uri.parse(uriString).lastPathSegment?.substringAfterLast("/") ?: "Không xác định"
    }
}