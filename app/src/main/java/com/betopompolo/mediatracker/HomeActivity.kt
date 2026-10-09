package com.betopompolo.mediatracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.betopompolo.mediatracker.databinding.ActivityHomeBinding
import com.betopompolo.mediatracker.databinding.HomeListItemLayoutBinding

/** Tela 1: lista de livros. Ao tocar em um item, abre [BookDetailActivity] via Intent explícita. */
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listView.layoutManager = LinearLayoutManager(this)
        binding.listView.adapter = HomeListAdapter(MockBooks.all) { book ->
            val intent = Intent(this, BookDetailActivity::class.java)
            intent.putExtra(BookDetailActivity.BOOK_ID_KEY, book.id)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

class HomeListAdapter(
    private val books: List<Book>,
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: HomeListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            HomeListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]
        val context = holder.itemBinding.root.context

        holder.itemBinding.cover.setImageResource(book.coverRes ?: R.drawable.book_cover_placeholder)
        holder.itemBinding.title.text = book.title
        holder.itemBinding.author.text = book.author
        holder.itemBinding.pageCount.text =
            context.getString(R.string.home_list_item_page_count, book.totalPages)
        holder.itemBinding.badge.badgeText.text = context.getString(R.string.book)

        holder.itemBinding.root.setOnClickListener { onBookClick(book) }
    }

    override fun getItemCount(): Int = books.size
}
