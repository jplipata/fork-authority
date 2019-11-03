package com.lipata.forkauthority.ui

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import com.lipata.forkauthority.api.yelp3.entities.Business

/**
 * Created by jlipata on 1/3/18.
 */
interface BusinessListParentView {
    fun getContext(): Context
    fun getRecyclerViewLayoutManager(): RecyclerView.LayoutManager

    /**
     * As per Yelp documentation: Rating for this business (value ranges from 1, 1.5, ... 4.5, 5).
     */
    fun getRatingDrawable(rating: String): Drawable

    fun notifyUserTooSoon(businessName: String)
    fun notifyUserBusinessLiked(businessName: String)
    fun notifyUserBusinessDismissed(position: Int, business: Business)
    fun notifyUserBusinessDontLiked(businessName: String)
    fun notifyNotAllowedOnDontLike();
    fun launchBusinessUrl(url: String)
}