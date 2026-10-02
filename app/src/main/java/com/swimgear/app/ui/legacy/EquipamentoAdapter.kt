package com.swimgear.app.ui.legacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swimgear.app.R
import com.swimgear.app.model.Equipamento
import com.swimgear.app.ui.components.icone

class EquipamentoAdapter(private val onClick: (Equipamento) -> Unit) :
    ListAdapter<Equipamento, EquipamentoAdapter.Holder>(object : DiffUtil.ItemCallback<Equipamento>() {
        override fun areItemsTheSame(oldItem: Equipamento, newItem: Equipamento) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Equipamento, newItem: Equipamento) = oldItem == newItem
    }) {
    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val nome: TextView = view.findViewById(R.id.textNome)
        private val marca: TextView = view.findViewById(R.id.textMarca)
        private val status: TextView = view.findViewById(R.id.textStatus)
        private val imagem: ImageView = view.findViewById(R.id.imageEquipamento)
        fun bind(item: Equipamento) {
            nome.text = item.nome
            marca.text = item.marca ?: itemView.context.getString(R.string.marca_nao_informada)
            status.setText(if (item.prontoParaTreino) R.string.pronto else R.string.pendente)
            imagem.setImageResource(item.categoria.icone())
            itemView.setOnClickListener { onClick(item) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_equipamento, parent, false))
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))
}
