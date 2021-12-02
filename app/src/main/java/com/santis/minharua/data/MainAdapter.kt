package com.santis.minharua.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.santis.minharua.MinhaRua
import com.santis.minharua.R
import com.santis.minharua.data.model.CategoriaIncidentes
import com.santis.minharua.data.model.Incidente
import com.santis.minharua.ui.AddIncidenteActivity
import com.santis.minharua.util.Image
import com.santis.minharua.util.ViewAnimation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainAdapter(val incidenteList: MutableList<CategoriaIncidentes>) : RecyclerView.Adapter<MainAdapter.IncidenteViewHolder>() {

    var listenerShare: (View) -> Unit = {}
    override fun getItemCount(): Int {
        return incidenteList.size
    }

    override fun onBindViewHolder(holder: IncidenteViewHolder, position: Int) {
        with(holder) {
            // Cabeçalho
            imgCatCor.setColorFilter(ContextCompat.getColor(imgCatCor.context, incidenteList[position].categoria.corCat), android.graphics.PorterDuff.Mode.SRC_IN)
            imgCatIcon.setImageResource(incidenteList[position].categoria.iconeCat)
            imgIcone.setImageURI(Uri.parse(incidenteList[position].incidentes[0].imagemInc))
            lblTitulo.text = incidenteList[position].incidentes[0].tituloInc
            cmdExpandir.setImageResource(R.drawable.ic_down)
            cmdExpandir.tag = "off"

            // CardView
            cvCartao.visibility = View.GONE
            lblTitulo2.text = incidenteList[position].incidentes[0].tituloInc
            imgImagem.setImageURI(Uri.parse(incidenteList[position].incidentes[0].imagemInc))
            /*fadeOut(imgImagem)*/
            lblDescricao.text = incidenteList[position].incidentes[0].descricaoInc
            lblEndereco.text = "${MinhaRua.cep?.logradouro} ${MinhaRua.cep?.endereco}"
            lblBairro.text = MinhaRua.cep?.bairro

            lblCategoria.text = incidenteList[position].categoria.nomeCat

            // Botão Expandir
            cmdExpandir.setOnClickListener(
                View.OnClickListener {

                    if (cmdExpandir.tag == "off") {
                        // lblResumo.visibility = View.INVISIBLE
                        lblTitulo.animate().alpha(0.25f)
                        cmdExpandir.setImageResource(R.drawable.ic_up)
                        toggleLayoutExpand(true, it, cvCartao)
                        cmdExpandir.tag = "on"
                        /*fadeIn(imgImagem)*/
                    } else {
                        // lblResumo.visibility = View.VISIBLE
                        lblTitulo.animate().alpha(1.0f)
                        /*cvCartao.visibility = View.GONE*/
                        cmdExpandir.setImageResource(R.drawable.ic_down)
                        cmdExpandir.tag = "off"
                        toggleLayoutExpand(false, it, cvCartao)
                        /*fadeOut(imgImagem)*/
                    }
                }
            )

            // Botão Compartilhar
            cmdCompartilhar.setOnClickListener {
                listenerShare = { incidente ->
                    Image.share(it.context, incidente)
                }

                listenerShare(cvCartao)
            }

            // Botão Apagar
            cmdApagar.setOnClickListener {
                val builder = AlertDialog.Builder(contexto)
                builder.setTitle("Confirma Exclusão do Incidente ?")
                builder.setPositiveButton(
                    "Excluir"
                ) { dialogInterface, i ->
                    GlobalScope.launch {
                        val db = MinhaRuaDatabase.abrirBanco(contexto)
                        val incidente: Incidente = incidenteList[position].incidentes.first()
                        db.incidenteDao().excluirIncidente(incidente)
                        incidenteList.removeAt(position)
                    }
                    notifyDataSetChanged()
                    Toast.makeText(contexto, "Incidente Excluido", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("Cancelar", null)
                builder.show()
            }

            // Botao Editar
            cmdEditar.setOnClickListener {
                val intent = Intent(contexto, AddIncidenteActivity::class.java)
                val inci = incidenteList[position]
                intent.putExtra("edicao", true)
                intent.putExtra("catinc", inci)
                contexto.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IncidenteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_incidente, parent, false)
        return IncidenteViewHolder(view)
    }

    private fun toggleLayoutExpand(show: Boolean, view: View, lyt_expand: View): Boolean {
        if (show) {
            ViewAnimation.expand(lyt_expand)
        } else {
            ViewAnimation.collapse(lyt_expand)
        }
        return show
    }

    inner class IncidenteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cmdApagar = view.findViewById<ImageView>(R.id.cmd_apagar)
        val cmdCompartilhar = view.findViewById<ImageView>(R.id.cmd_compartilhar)
        val cmdEditar = view.findViewById<ImageView>(R.id.cmd_editar)
        val cmdExpandir = view.findViewById<ImageView>(R.id.cmd_expandir)
        val contexto: Context = view.findViewById<ConstraintLayout>(R.id.root_item).context
        val cvCartao = view.findViewById<CardView>(R.id.cv_cartao)
        val imgCatCor = view.findViewById<ImageView>(R.id.img_cat_cor)
        val imgCatIcon = view.findViewById<ImageView>(R.id.img_cat_icon)
        val imgIcone = view.findViewById<ImageView>(R.id.img_icone)
        val imgImagem = view.findViewById<ImageView>(R.id.img_imagem)
        val lblBairro = view.findViewById<TextView>(R.id.lbl_bairro)
        val lblCategoria = view.findViewById<TextView>(R.id.lbl_categoria)
        val lblDescricao = view.findViewById<TextView>(R.id.lbl_descricao)
        val lblEndereco = view.findViewById<TextView>(R.id.lbl_endereco)
        val lblTitulo = view.findViewById<TextView>(R.id.lbl_titulo)
        val lblTitulo2 = view.findViewById<TextView>(R.id.lbl_titulo_2)
    }
}
