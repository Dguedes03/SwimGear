package com.swimgear.app.ui.legacy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.swimgear.app.R
import com.swimgear.app.data.EquipamentoMock
import com.swimgear.app.ui.components.icone

class DetalhesXmlActivity : AppCompatActivity() {
    private lateinit var textStatus: TextView
    private lateinit var buttonPronto: Button
    private var pronto = false
    private var equipamentoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhes_xml)
        findViewById<View>(R.id.rootXmlDetail).aplicarInsets()
        equipamentoId = intent.getStringExtra(EXTRA_ID)
        val item = EquipamentoMock.equipamentos.find { it.id == equipamentoId }
        if (item == null) {
            Toast.makeText(this, R.string.item_nao_encontrado, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        pronto = savedInstanceState?.getBoolean(EXTRA_PRONTO)
            ?: intent.getBooleanExtra(EXTRA_PRONTO, item.prontoParaTreino)
        findViewById<TextView>(R.id.textNome).text = item.nome
        findViewById<TextView>(R.id.textMarca).text = item.marca ?: getString(R.string.marca_nao_informada)
        findViewById<TextView>(R.id.textDescricao).text = item.descricao ?: getString(R.string.sem_descricao)
        findViewById<TextView>(R.id.textCategoria).text = item.categoria.titulo
        findViewById<ImageView>(R.id.imageEquipamento).setImageResource(item.categoria.icone())
        textStatus = findViewById(R.id.textStatus)
        buttonPronto = findViewById(R.id.buttonPronto)
        findViewById<Button>(R.id.buttonVoltar).setOnClickListener { finish() }
        buttonPronto.setOnClickListener { pronto = !pronto; atualizarStatus() }
        atualizarStatus()
    }
    private fun atualizarStatus() {
        textStatus.setText(if (pronto) R.string.pronto else R.string.pendente)
        buttonPronto.setText(if (pronto) R.string.remover_mochila else R.string.marcar_pronto)
        setResult(RESULT_OK, Intent().putExtra(EXTRA_ID, equipamentoId).putExtra(EXTRA_PRONTO, pronto))
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(EXTRA_PRONTO, pronto)
        super.onSaveInstanceState(outState)
    }
    companion object {
        const val EXTRA_ID = "com.swimgear.app.EQUIPAMENTO_ID"
        const val EXTRA_PRONTO = "com.swimgear.app.EQUIPAMENTO_PRONTO"
    }
}
