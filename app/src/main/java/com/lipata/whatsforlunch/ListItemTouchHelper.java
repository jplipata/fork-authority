package com.lipata.whatsforlunch;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by jlipata on 2/23/16.
 */
public class ListItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private static final String LOG_TAG = ListItemTouchHelper.class.getSimpleName();
    private BusinessListAdapter mBusinessListAdapter;

    public ListItemTouchHelper(BusinessListAdapter adapter){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mBusinessListAdapter=adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mBusinessListAdapter.dismiss(position);
    }
}
