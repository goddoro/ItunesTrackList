package com.goddoro.watchaassignment.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.goddoro.watchaassignment.data.MusicItem
import com.goddoro.watchaassignment.databinding.ItemSearchBinding
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.koin.core.KoinComponent
import androidx.databinding.library.baseAdapters.BR
import com.goddoro.watchaassignment.util.CommonConst.ITEM_OFFSET
import com.goddoro.watchaassignment.util.setGreenText

class SearchListAdapter: RecyclerView.Adapter<SearchListAdapter.SearchViewHolder>() {

    private val onClickStar : PublishSubject<Pair<Int,MusicItem>> = PublishSubject.create()
    val clickStar : Observable<Pair<Int,MusicItem>> = onClickStar

    private val onNeedMore : PublishSubject<Unit> = PublishSubject.create()
    val needMoreEvent : Observable<Unit> = onNeedMore


    private val diff = object : DiffUtil.ItemCallback<MusicItem>() {
        override fun areItemsTheSame(oldItem: MusicItem, newItem: MusicItem): Boolean {
            return oldItem.trackId == newItem.trackId
        }

        override fun areContentsTheSame(oldItem: MusicItem, newItem: MusicItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diff)

    fun submitItems(items: List<MusicItem>?) {
        differ.submitList(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBinding.inflate(inflater, parent, false)

        return SearchViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) = holder.bind(differ.currentList[position])

    inner class SearchViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root),
        KoinComponent {
        init {

            binding.imgStar.setOnClickListener {
                onClickStar.onNext(Pair(layoutPosition,differ.currentList[layoutPosition]))
            }

        }

        fun bind(item: MusicItem) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()

            binding.txtIndex.text = ( layoutPosition + 1 ).toString()

            binding.txtArtistName.setGreenText()
            binding.txtCollectionName.setGreenText()
            binding.txtTrackName.setGreenText()

            /**
             * 아이템이 10개밖에 남지 않았을 때 needMoreEvent발생,
             * 추가로 불러오는 아이템이 10개 이하일경우를 대비해 마지막 아이템만 남았을 경우에도 한 번 호출
             */

            if ( ( differ.currentList.size - 10 ) == layoutPosition || differ.currentList.size - 1 == layoutPosition  ) {
                onNeedMore.onNext(Unit)
            }


        }
    }

}

@BindingAdapter("app:recyclerview_search_items")
fun RecyclerView.setSearchItemList(items: List<MusicItem>?) {
    (adapter as? SearchListAdapter)?.run {
        this.submitItems(items)
    }
}
