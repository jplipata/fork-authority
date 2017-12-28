package com.lipata.forkauthority.data

import com.lipata.forkauthority.api.yelp3.entities.Business
import com.lipata.forkauthority.ui.BusinessListBaseItem
import com.lipata.forkauthority.ui.BusinessListHeader
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

/**
 * Created by jlipata on 1/1/18.
 */
class CombinedListTest {

    val LIKED_BUSINESS = "Liked Business"
    val UNSORTED_BUSINESS = "Unsorted Business"
    val DONT_LIKE_BUSINESS = "Don't Like Business"

    /**
     * Test case with 4 full sublists
     */
    @Test
    fun get_fullList() {
        val listProxy = CombinedList()

        // Make sublists
        val likedBusiness = Business()
        likedBusiness.name = "Liked Business"

        val tooSoonBusiness = Business()
        tooSoonBusiness.name = "Too Soon Business"

        val unsortedBusiness = Business()
        unsortedBusiness.name = "Unsorted Business"

        val dontLikeBusiness = Business()
        dontLikeBusiness.name = "Don't Like Business"

        val likesList = mutableListOf<BusinessListBaseItem>(
                BusinessListHeader(Categories.LIKES),
                likedBusiness
        )

        val tooSoonList = mutableListOf<BusinessListBaseItem>(
                BusinessListHeader(Categories.LIKES_TOO_SOON),
                tooSoonBusiness
        )

        val unsortedList = mutableListOf<BusinessListBaseItem>(
                BusinessListHeader(Categories.UNSORTED),
                unsortedBusiness
        )

        val dontLikeList = mutableListOf<BusinessListBaseItem>(
                BusinessListHeader(Categories.DONT_LIKE),
                dontLikeBusiness
        )

        listProxy.setSublists(
                listOf(likesList, tooSoonList, unsortedList, mutableListOf(),dontLikeList))

        assertThat((listProxy.get(1) as Business).getName(), `is`(equalTo(likedBusiness.getName())))
        assertThat((listProxy.get(3) as Business).getName(), `is`(equalTo(tooSoonBusiness.getName())))
        assertThat((listProxy.get(5) as Business).getName(), `is`(equalTo(unsortedBusiness.getName())))
        assertThat((listProxy.get(7) as Business).getName(), `is`(equalTo(dontLikeBusiness.getName())))
    }

    /**
     * Tests case with no Too Soon items
     */
    @Test
    fun get_noTooSoon() {
        val listProxy = initList_WithoutTooSoon()
        assertThat((listProxy.get(1) as Business).getName(), `is`(equalTo(LIKED_BUSINESS)))
        assertThat((listProxy.get(3) as Business).getName(), `is`(equalTo(UNSORTED_BUSINESS)))
        assertThat((listProxy.get(5) as Business).getName(), `is`(equalTo(DONT_LIKE_BUSINESS)))
    }

    @Test
    fun add() {
        val listProxy = initList_WithoutTooSoon()
        listProxy.add(2, BusinessListHeader(Categories.LIKES_TOO_SOON))
        assertThat((listProxy.get(2) as BusinessListHeader).key, `is`(equalTo(Categories.LIKES_TOO_SOON)))
    }

    /**
     * Creates a ListProxy without a TooSoon sublist
     */
    private fun initList_WithoutTooSoon(): CombinedList {
        val listProxy = CombinedList()

        // Make sublists
        val likedBusiness = Business()
        likedBusiness.name = LIKED_BUSINESS

        val unsortedBusiness = Business()
        unsortedBusiness.name = UNSORTED_BUSINESS

        val dontLikeBusiness = Business()
        dontLikeBusiness.name = DONT_LIKE_BUSINESS

        val likesList = mutableListOf<BusinessListBaseItem>(
                BusinessListHeader(Categories.LIKES),
                likedBusiness
        )

        val tooSoonList = mutableListOf<BusinessListBaseItem>()

        val unsortedList = mutableListOf<BusinessListBaseItem>(
                BusinessListHeader(Categories.UNSORTED),
                unsortedBusiness
        )

        val dontLikeList = mutableListOf<BusinessListBaseItem>(
                BusinessListHeader(Categories.DONT_LIKE),
                dontLikeBusiness
        )

        listProxy.setSublists(
                listOf<List<BusinessListBaseItem>>(likesList, tooSoonList, unsortedList, mutableListOf(), dontLikeList))

        return listProxy
    }

}