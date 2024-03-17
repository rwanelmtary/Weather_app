import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.Pojo.FavoriteLocation
import com.example.weather.databinding.FavouritecellBinding
import com.example.weather.facvourite.FavouriteFragmentDirections
import com.example.weather.facvourite.OnClick

class FavAdapter(
    private var fav: List<FavoriteLocation>,
    val context: Context,
    var listener: OnClick
) :
    RecyclerView.Adapter<FavAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavAdapter.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = FavouritecellBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavAdapter.ViewHolder, position: Int) {
        val currentFav = fav[position]
        holder.binding.favcountry.text = currentFav.address_en

        holder.binding.delete.setOnClickListener {
            listener.onClick(currentFav)
        }
        holder.itemView.setOnClickListener {
            val action = FavouriteFragmentDirections.actionFavouriteFragment2ToFavDetailsFragment3(currentFav)
            holder.itemView.findNavController().navigate(action)
        }


    }

    override fun getItemCount(): Int = fav.size

    fun updateData(newProducts: List<FavoriteLocation>) {
        fav = newProducts
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: FavouritecellBinding) : RecyclerView.ViewHolder(binding.root)
}
