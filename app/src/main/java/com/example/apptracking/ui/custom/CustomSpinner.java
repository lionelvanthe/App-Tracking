package com.example.apptracking.ui.custom;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import java.lang.reflect.Field;

public class CustomSpinner extends AppCompatSpinner {

    private android.widget.ListPopupWindow popupWindow;

    public interface OnSpinnerEventsListener {
        void onPopupWindowOpened(Spinner spinner);
        void onPopupWindowClosed(Spinner spinner);
    }

    private OnSpinnerEventsListener mListener;
    private boolean mOpenInitiated = false;

    public CustomSpinner(Context context) {
        super(context);
    }

    public CustomSpinner(@NonNull Context context, int mode) {
        super(context, mode);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }

    @Override
    public boolean performClick() {
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onPopupWindowOpened(this);
        }
        return super.performClick();
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            popupWindow = (android.widget.ListPopupWindow) popup.get(this);
            popupWindow.setHeight(50);
            Log.d("Thenv", "setAdapter: "  +popupWindow.getHeight());
        } catch (Throwable e) {
            Log.d("Thenv", "setAdapter: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        if (hasBeenOpened() && hasFocus) {
            performClosedEvent();
        }
    }

    /**
     * Register the listener which will listen for events.
     */
    public void setSpinnerEventsListener(
            OnSpinnerEventsListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
    }

    /**
     * Propagate the closed Spinner event to the listener from outside if needed.
     */
    public void performClosedEvent() {
        mOpenInitiated = false;
        if (mListener != null) {
            mListener.onPopupWindowClosed(this);
        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    public boolean hasBeenOpened() {
        return mOpenInitiated;
    }

}
