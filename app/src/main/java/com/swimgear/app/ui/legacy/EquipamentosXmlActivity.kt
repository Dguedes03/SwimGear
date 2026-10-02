package com.swimgear.app.ui.legacy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.swimgear.app.data.EquipamentoMock
import com.swimgear.app.model.Equipamento

/** Parcial independente: duas Activities, XML, findViewById, mocks e Intent explícita. */
class EquipamentosXmlActivity : AppCompatActivity() {
    private lateinit var recycler: androidx.recyclerview.widget.RecyclerView
    private var itens: List<Equipamento> = EquipamentoMock.equipamentos
    private lateinit var adapter: EquipamentoAdapter
    private val detalhes = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val id = result.data?.getStringExtra(DetalhesXmlActivity.EXTRA_ID)
            val pronto = result.data?.getBooleanExtra(DetalhesXmlActivity.EXTRA_PRONTO, false) ?: false
            itens = itens.map { if (it.id == id) it.copy(prontoParaTreino = pronto) else it }
            adapter.submitList(itens)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(com.swimgear.app.R.layout.activity_equipamentos_xml)
        findViewById<View>(com.swimgear.app.R.id.rootXmlList).aplicarInsets()
        adapter = EquipamentoAdapter { item ->
            detalhes.launch(Intent(this, DetalhesXmlActivity::class.java).apply {
                putExtra(DetalhesXmlActivity.EXTRA_ID, item.id)
                putExtra(DetalhesXmlActivity.EXTRA_PRONTO, item.prontoParaTreino)
            })
        }
        savedInstanceState?.getBooleanArray("prontos")?.let { estados ->
            itens = itens.mapIndexed { index, item -> item.copy(prontoParaTreino = estados.getOrElse(index) { item.prontoParaTreino }) }
        }
        findViewById<Button>(com.swimgear.app.R.id.buttonVoltar).setOnClickListener { finish() }
        recycler = findViewById(com.swimgear.app.R.id.recyclerEquipamentos)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        adapter.submitList(itens)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBooleanArray("prontos", itens.map { it.prontoParaTreino }.toBooleanArray())
        super.onSaveInstanceState(outState)
    }
}
