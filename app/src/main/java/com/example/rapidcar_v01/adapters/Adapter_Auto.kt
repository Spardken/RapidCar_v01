import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rapidcar_v01.R
import com.example.rapidcar_v01.databinding.ItemAutoBinding
import com.example.rapidcar_v01.modelo.DataAuto
import com.example.rapidcar_v01.view.homeButton.DetalleAutoActivity

class Adapter_Auto(private var data: List<DataAuto>, private val context: Context) :
    RecyclerView.Adapter<Adapter_Auto.ViewHolder>() {


    // Variable global para almacenar el ID del registro seleccionado
    companion object {
        private var selectedAutoId: Int = -1

        fun getSelectedAutoId(): Int {
            return selectedAutoId
        }
    }


    inner class ViewHolder(private val binding: ItemAutoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonDetalleAuto.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = data[position]
                    selectedAutoId = item.idAuto // Actualizar selectedAutoId con el ID del auto seleccionado
                    val intent = Intent(context, DetalleAutoActivity::class.java)
                    intent.putExtra("data_auto_id", item.idAuto) // Pasar el ID del auto
                    context.startActivity(intent)
                }
            }
        }

        fun bind(item: DataAuto) {
            binding.apply {
                // Log de la cadena Base64 antes de decodificarla (para debugging)
                Log.d("Adapter_Auto", "Cadena Base64 antes de decodificar: ${item.img1}")

                // Decodificar la cadena Base64 en un Bitmap
                val decodedBytes: ByteArray = Base64.decode(item.img1, Base64.DEFAULT)
                val bitmap: Bitmap =
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                // Cargar la imagen con Glide utilizando el Bitmap decodificado
                Glide.with(autoImageView.context)
                    .load(bitmap)
                    .placeholder(R.drawable.ic_picture_avatar)
                    .error(R.drawable.baseline_error_24)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("Adapter_Auto", "Error al cargar la imagen con Glide", e)
                            e?.logRootCauses("GlideException")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            // Imagen cargada exitosamente
                            return false
                        }
                    })
                    .into(autoImageView)

                modeloTextView.text = "Modelo: ${item.modelo}"
                kilometrajeTextView.text = "Km: ${item.kilometraje}"
                marcaTextView.text = "Marca: ${item.marca}"
                precioTextView.text = "Precio: ${item.precio}"
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAutoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = data[position]
        holder.bind(currentItem)
    }

    // Método para actualizar los datos del adaptador
    fun updateData(newData: List<DataAuto>) {
        data = newData
        notifyDataSetChanged()
    }

    // Método para obtener el ID del registro seleccionado
    fun getSelectedAutoId(): Int {
        return selectedAutoId
    }

    fun getAutoById(autoId: Int): DataAuto? {
        return data.firstOrNull { it.idAuto == autoId }
    }
}
