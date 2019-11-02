package com.lipata.forkauthority.ui;

import android.graphics.Canvas;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

public class ListItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private BusinessListAdapter mBusinessListAdapter;

    public ListItemTouchHelper(BusinessListAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mBusinessListAdapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Only do this for a Business, not for a Header
        if (viewHolder.getItemViewType() == ListItemTypes.BUSINESS) {
            int position = viewHolder.getAdapterPosition();
            mBusinessListAdapter.dismiss(position);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // Only do this for a Business, not for a Header
        if (viewHolder.getItemViewType() == ListItemTypes.BUSINESS) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
