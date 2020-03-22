package com.lipata.forkauthority.businesslist

class BusinessListHeader(val key: String) : BusinessListBaseItem() {

    override fun getViewType(): Int {
        return ListItemTypes.HEADER
    }
}