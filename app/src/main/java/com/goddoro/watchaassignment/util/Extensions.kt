package com.goddoro.watchaassignment.util

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.goddoro.watchaassignment.BuildConfig
import com.goddoro.watchaassignment.data.MusicItem
import com.goddoro.watchaassignment.data.database.FavoriteItem


/**
 * created By DORO 2021/07/09
 */

fun HashMap<String, out Any?>.filterValueNotNull(): HashMap<String, Any> {
    return this.filterNot { it.value == null } as HashMap<String, Any>
}

@BindingAdapter("srcUrl", "placeholder", requireAll = false)
fun ImageView.loadUrlAsync(url: String?, placeholder: Drawable? = null) {
    if (url == null) {
        Glide.with(this).load(placeholder).into(this)
    } else {
        Glide.with(this).load(url)
            .apply {
                if (placeholder != null)
                    (placeholder)
            }
            .into(this)
    }
}

fun MusicItem.toFavoriteItem( index : Int ) : FavoriteItem {
    return FavoriteItem(
        trackId = this.trackId,
        artistName = this.artistName,
        trackName = this.trackName,
        artworkUrl60 = this.artworkUrl60 ?: "",
        artworkUrl100 = this.artworkUrl100 ?: "",
        collectionName = this.collectionName,
        index = index
    )
}


open class Once<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

fun <T> LiveData<Once<T>>.observeOnce(lifecycle: LifecycleOwner, listener: (T) -> Unit) {
    this.observe(lifecycle, Observer {
        it?.getContentIfNotHandled()?.let {
            listener(it)
        }
    })
}

fun ObservableBoolean.toggle(){
    set(!this.get())
}

@BindingAdapter("android:visibility")
fun View.setVisibility ( isVisible : Boolean) {
    if ( isVisible) this.visibility = View.VISIBLE
    else this.visibility = View.GONE
}


fun debugE(tag: String, message: Any?) {
    if (BuildConfig.DEBUG)
        Log.e(tag, "🧩" + message.toString() + "🧩")
}

fun debugE(message: Any?) {
    debugE("DEBUG", message)
}