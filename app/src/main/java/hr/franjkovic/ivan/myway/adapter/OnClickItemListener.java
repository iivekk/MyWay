package hr.franjkovic.ivan.myway.adapter;

import android.view.View;

public interface OnClickItemListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
