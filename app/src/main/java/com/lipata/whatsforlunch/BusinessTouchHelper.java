package com.lipata.whatsforlunch;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by jlipata on 2/23/16.
 */
public class BusinessTouchHelper extends ItemTouchHelper.SimpleCallback {
    private static final String LOG_TAG = BusinessTouchHelper.class.getSimpleName();
    private BusinessListFilter mBusinessListFilter;
    private BusinessListAdapter mBusinessListAdapter;

    public BusinessTouchHelper(BusinessListAdapter adapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mBusinessListAdapter=adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mBusinessListAdapter.remove(position);
    }
}
