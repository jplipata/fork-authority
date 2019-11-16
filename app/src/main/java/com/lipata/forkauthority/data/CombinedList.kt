package com.lipata.forkauthority.data

import com.lipata.forkauthority.businesslist.BusinessListBaseItem

/**
 * Created by jlipata on 12/31/17.
 */
class CombinedList {
    lateinit var list: List<BusinessListBaseItem>

    // TODO This is a "hard coded" implementation, there should be a more flexible way of doing this
    private lateinit var likesList: MutableList<BusinessListBaseItem>
    private lateinit var likedButTooSoon: MutableList<BusinessListBaseItem>
    private lateinit var unsortedList: MutableList<BusinessListBaseItem>
    private lateinit var unsortedTooSoonList: MutableList<BusinessListBaseItem>
    private lateinit var dontLikeList: MutableList<BusinessListBaseItem>

    fun get(index: Int): BusinessListBaseItem {
        val subLocation = mapSubLocation(index)
        return subLocation.subList[subLocation.subIndex]
    }

    fun set(index: Int, item: BusinessListBaseItem) {
        val subLocation = mapSubLocation(index)
        subLocation.subList[subLocation.subIndex] = item
    }

    fun remove(index: Int) {
        val subLocation = mapSubLocation(index)
        subLocation.subList.removeAt(subLocation.subIndex)
    }

    fun add(index: Int, item: BusinessListBaseItem) {
        val subLocation = mapSubLocation(index)
        subLocation.subList.add(subLocation.subIndex, item)
    }

    fun size(): Int {
        val size = (likesList.size
                + likedButTooSoon.size
                + unsortedList.size
                + unsortedTooSoonList.size
                + dontLikeList.size)
        return size
    }

    /**
     * @param lists You must provide all sublists even if any are empty and the order must correspond
     */
    fun setSublists(lists: List<List<BusinessListBaseItem>>) {
        likesList = lists[0] as MutableList<BusinessListBaseItem>
        likedButTooSoon = lists[1] as MutableList<BusinessListBaseItem>
        unsortedList = lists[2] as MutableList<BusinessListBaseItem>
        unsortedTooSoonList = lists[3] as MutableList<BusinessListBaseItem>
        dontLikeList = lists[4] as MutableList<BusinessListBaseItem>
    }

    fun getSublist(key: String): List<BusinessListBaseItem> {
        return when {
            key == Categories.LIKES -> likesList
            key == Categories.LIKES_TOO_SOON -> likedButTooSoon
            key == Categories.UNSORTED -> unsortedList
            key == Categories.UNSORTED_TOO_SOON -> unsortedTooSoonList
            key == Categories.DONT_LIKE -> dontLikeList
            else -> throw Exception("Could not map sublist")
        }
    }

    /**
     * @return Returns the master index of a sublist + subindex
     */
    fun getCombinedIndex(key: String, index: Int): Int {
        return when {
            key == Categories.LIKES -> index
            key == Categories.LIKES_TOO_SOON -> index + likesList.size
            key == Categories.UNSORTED -> index + (likesList.size + likedButTooSoon.size)
            key == Categories.UNSORTED_TOO_SOON -> index + (likesList.size + likedButTooSoon.size + unsortedList.size)
            key == Categories.DONT_LIKE -> index + (likesList.size + likedButTooSoon.size + unsortedList.size + unsortedTooSoonList.size)
            else -> throw Exception("Look up error")
        }
    }

    fun getStartOfSublist(key: String): Int {
        return when {
            key == Categories.LIKES -> 0
            key == Categories.LIKES_TOO_SOON -> likesList.size
            key == Categories.UNSORTED -> likesList.size + likedButTooSoon.size
            key == Categories.UNSORTED_TOO_SOON -> likesList.size + likedButTooSoon.size + unsortedList.size
            key == Categories.DONT_LIKE -> likesList.size + likedButTooSoon.size + unsortedList.size + unsortedTooSoonList.size
            else -> throw Exception("Could not map sublist")
        }
    }

    // TODO There's an issue this implementation when more than one list starts at 0 (e.g. Likes and Too Soon are both empty)
    private fun mapSubLocation(index: Int): SubLocation {
        // TODO This is very ugly, there's definitely a smarter way to do this
        return when {
            index < (likesList.size) -> SubLocation(likesList, index)

            index >= (likesList.size)
                    && index < (likesList.size + likedButTooSoon.size) -> SubLocation(likedButTooSoon, (index - likesList.size))

            index >= (likesList.size + likedButTooSoon.size)
                    && index < (likesList.size + likedButTooSoon.size + unsortedList.size) -> SubLocation(unsortedList, (index - (likesList.size + likedButTooSoon.size)))

            index >= (likesList.size + likedButTooSoon.size + unsortedList.size)
                    && index < (likesList.size + likedButTooSoon.size + unsortedList.size + unsortedTooSoonList.size) -> SubLocation(unsortedTooSoonList, (index - (likesList.size + likedButTooSoon.size + unsortedList.size)))

            index >= (likesList.size + likedButTooSoon.size + unsortedList.size + unsortedTooSoonList.size) ->
                SubLocation(dontLikeList, (index - (likesList.size + likedButTooSoon.size + unsortedList.size + unsortedTooSoonList.size)))
            else -> throw Exception("Could not map index to sublist")
        }
    }

    class SubLocation(val subList: MutableList<BusinessListBaseItem>, val subIndex: Int)
}