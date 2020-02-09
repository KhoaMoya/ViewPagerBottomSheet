package com.khoa.viewpagerbottomsheet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.khoa.viewpagerbottomsheet.databinding.ExampleFragmentBinding;

public class ExampleFragment extends Fragment {

    private View bottomSheet;
    private ExampleFragmentBinding mBinding;
    private OnTouchListenerRecyclerView onTouchListenerRecyclerView;

    public ExampleFragment(View bottomSheet) {
        this.bottomSheet = bottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.example_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            mBinding.recyclerView.setAdapter(new RecyclerViewAdapter());
            bottomSheet.post(new Runnable() {
                @Override
                public void run() {
                    final int minY = (int) bottomSheet.getY();
                    onTouchListenerRecyclerView = new OnTouchListenerRecyclerView(bottomSheet, 1000, minY);

                    mBinding.recyclerView.setOnTouchListener(onTouchListenerRecyclerView);

//                    onTouchListenerRecyclerView.closeBottomSheet();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
