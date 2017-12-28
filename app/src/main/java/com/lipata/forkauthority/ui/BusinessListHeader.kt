package com.lipata.forkauthority.ui

class BusinessListHeader(val key: String) : BusinessListBaseItem() {

    override fun getViewType(): Int {
        return ListItemTypes.HEADER
    }
}