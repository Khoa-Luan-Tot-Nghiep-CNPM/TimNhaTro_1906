package com.doan.timnhatro.callback;

import com.doan.timnhatro.model.DirectionsResult;

public interface OnDirectionListener {
    void onSuccess(DirectionsResult directionsResult);
    void onError();
}
