package com.khoa.viewpagerbottomsheet;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;

public class OnTouchListenerRecyclerView implements View.OnTouchListener {

    private static RecyclerView recyclerView;
    private View bottomSheet;
    private View headerView;

    private Handler handler;
    private float maxY;
    private float minY;
    private final float a = 0.8f;
    private final int b = 9;
    private SHEET_STATUS status;

    private Runnable runnable;

    private int prevFingerPosition; // vị trí chạm lần trước
    private int fingerPosition; // vị trí trạm hiện tại


    private int bottomSheetYPosition = 0;    // vị trí hiện tại của bottom sheet
    private float bottomSheetHeight = 0;      // chiều cao của bottom sheet
    private boolean scrollEnough = false;   // biến kiểm tra xem scroll đã đủ để đóng hoặc mở sheet chưa

    private boolean isScrolled = false;  // biến kiểm tra xem bottomSheet có bị cuộn không

    private boolean scrollToBottom = false; // biến kiểm tra recyclerview cuộn đến bottom
    private boolean scrollToTop = false; // biến kiểm tra recyclerview cuộn đến top

    private boolean resultOnTouch; // kết quả trả về của hàm onTouch

    public OnTouchListenerRecyclerView(View bottomSheet, float maxY, float minY) {
        this.bottomSheet = bottomSheet;
        this.handler = new Handler();
        this.maxY = maxY;
        this.minY = minY;

        this.headerView = bottomSheet.findViewById(R.id.header_bottomsheet);

        // true: không xử lý cuộn, false: xử lý cuộn
        resultOnTouch = false;

        // lấy status hiện tại
//        status = (Math.round(bottomSheet.getY()) > minY - 20 && Math.round(bottomSheet.getY()) < (minY + 20)) ? SHEET_STATUS.OPENED : SHEET_STATUS.CLOSED;

        headerViewListener();
    }

    private void headerViewListener() {
        if (headerView != null) {
            headerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        // Khi vừa đặt tay vào màn hình
                        case MotionEvent.ACTION_DOWN:
                            // lấy status hiện tại
                            status = (Math.round(bottomSheet.getY()) > minY - 20 && Math.round(bottomSheet.getY()) < (minY + 20)) ? SHEET_STATUS.OPENED : SHEET_STATUS.CLOSED;
                            Log.e("Loi", "header : " + status.name() + " : " + bottomSheet.getY());
                            // lấy chiều cao của bottom sheet
                            bottomSheetHeight = bottomSheet.getHeight();

                            // gán vị trí chạm
                            fingerPosition = (int) event.getRawY();

                            // lấy vị trí Y trước khi cuộn của sheet
                            bottomSheetYPosition = (int) bottomSheet.getY();

                            // hủy hành động cuộn về khi chạm
                            if (runnable != null) {
                                handler.removeCallbacks(runnable);
                            }
                            break;
                        // Khi di chuyển (scroll)
                        case MotionEvent.ACTION_MOVE:

                            // gán tọa độ cũ
                            prevFingerPosition = fingerPosition;

                            // Lấy tọa độ khi chạm
                            fingerPosition = (int) event.getRawY();

                            int currentYPosition = (int) bottomSheet.getY();

                            // tính khoảng cách giữa vị trí lúc bắt đầu trạm và vị trí hiện tại của sheet
                            float m = Math.abs(bottomSheetYPosition - currentYPosition);

                            // kiểm tra xem scroll đã đủ để close/open chưa
                            scrollEnough = m > bottomSheetHeight / b;

                            // tính khoảng cách giữa vị trí của ngón tay hiện tại với vị trí lần chạm trước đó
                            int distance = fingerPosition - prevFingerPosition;

                            // thay đổi vị trí của bottom sheet
                            bottomSheet.setY(bottomSheet.getY() + distance);

                            break;
                        // Khi bỏ tay ra khỏi màn hình
                        case MotionEvent.ACTION_UP:
                            int currentY = (int) bottomSheet.getY();
                            boolean scrollUp = (currentY - bottomSheetYPosition) < 0;
                            if((currentY - bottomSheetYPosition) != 0) {
                                if(status == SHEET_STATUS.OPENED){
                                    if(scrollEnough && !scrollUp){
                                        closeBottomSheet();
                                    } else {
                                        openBottomSheet();
                                    }
                                } else if(status == SHEET_STATUS.CLOSED){
                                    if(scrollEnough && scrollUp){
                                        openBottomSheet();
                                    } else {
                                        closeBottomSheet();
                                    }
                                }
                            }

                            scrollEnough = false;

                            break;

                        // Khi huỷ touch
                        case MotionEvent.ACTION_CANCEL:
                            Log.e("Loi", "cancel");
                            break;
                    }

                    return true;
                }
            });
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // Lấy tọa độ khi chạm
        fingerPosition = (int) event.getRawY();
        recyclerView = (RecyclerView) v;

        switch (event.getAction()) {

            // Khi vừa đặt tay vào màn hình
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            // Khi di chuyển (scroll)
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;

            // Khi bỏ tay ra khỏi màn hình
            case MotionEvent.ACTION_UP:
                actionUp(event);
                break;

            // Khi huỷ touch
            case MotionEvent.ACTION_CANCEL:
                actionMove(event);
                actionUp(event);
                break;
        }

        prevFingerPosition = fingerPosition;
        return resultOnTouch;
    }

    private void actionDown(MotionEvent event) {
        // lấy status hiện tại
        status = (Math.round(bottomSheet.getY()) > minY - 20 && Math.round(bottomSheet.getY()) < (minY + 20)) ? SHEET_STATUS.OPENED : SHEET_STATUS.CLOSED;
        Log.e("Loi", "recyclerview: " + status.name() + " : " + bottomSheet.getY());

        // lấy chiều cao của bottom sheet
        this.bottomSheetHeight = bottomSheet.getHeight();

        // gán vị trí chạm lần trước
        prevFingerPosition = fingerPosition;

        // lấy vị trí Y trước khi cuộn của sheet
        bottomSheetYPosition = (int) bottomSheet.getY();

        // hủy hành động cuộn về khi chạm
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }

        // xác định xem scrollView đang đã cuộn đến top hay bottom chưa
        scrollToBottom = !recyclerView.canScrollVertically(1); // up
        scrollToTop = !recyclerView.canScrollVertically(-1); // down
    }

    private void actionMove(MotionEvent event) {
        // Lấy tọa độ fingerPosition của bottom sheet hiện tại
        int currentYPosition = (int) bottomSheet.getY();

        // tính khoảng cách giữa vị trí lúc bắt đầu trạm và vị trí hiện tại của sheet
        float m = Math.abs(bottomSheetYPosition - currentYPosition);

        // kiểm tra xem scroll đã đủ để close/open chưa
        scrollEnough = m > bottomSheetHeight / b;

        // tính khoảng cách giữa vị trí của ngón tay hiện tại với vị trí lần chạm trước đó
        int distance = fingerPosition - prevFingerPosition;


        // khi sheet đang mở
        if (status == SHEET_STATUS.OPENED) {
            // cuộn sheet khi scroll ở Top và đang cuộn xuống
            if (scrollToTop && distance >= 0) {
                resultOnTouch = true;
                bottomSheet.setY((bottomSheet.getY() + distance));
                isScrolled = true;
            }
            // đang cuộn xuống thì cuộn lên
            else if (isScrolled) {
                bottomSheet.setY((bottomSheet.getY() + distance));
            }
            // cuộn lên
            else {
//                Log.e("Loi", "loi");
                resultOnTouch = false;
                scrollToTop = false;
            }
        } else if (status == SHEET_STATUS.CLOSED) {
            // không scroll recyclerview
            resultOnTouch = true;
            if (Math.round(bottomSheet.getY()) <= Math.round(maxY) && distance < 0) {
                bottomSheet.setY(bottomSheet.getY() + distance);
                isScrolled = true;
            }
            // đang cuộn lên thì cuộn xuống
            else if (isScrolled) {
                bottomSheet.setY(bottomSheet.getY() + distance);
            }
        }
    }

    private void actionUp(MotionEvent event) {
        Log.e("Loi", String.valueOf(bottomSheet.getY()));
        // Xử lý nếu người dùng đã cuộn sheet
        if (isScrolled) {
//            Log.e("Loi", "from:" + status.name());
            if (status == SHEET_STATUS.CLOSED) {
                if (scrollEnough) {
                    openBottomSheet();
//                    Log.e("Loi", "1");
                } else {
                    closeBottomSheet();
//                    Log.e("Loi", "2");
                }
            } else if (status == SHEET_STATUS.OPENED) {
                if (scrollToTop && scrollEnough) {
                    closeBottomSheet();
//                    Log.e("Loi", "3");
                } else if (scrollToTop && !scrollEnough) {
                    openBottomSheet();
//                    Log.e("Loi", "4");
                }
            }
        }

//        Log.e("Loi", "to: " + status.name());
        isScrolled = false;
        scrollEnough = false;
        scrollToBottom = false;
        scrollToTop = false;
        resultOnTouch = false;
    }

    public void closeBottomSheet() {
        status = SHEET_STATUS.CLOSED;
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (Math.round(bottomSheet.getY()) != Math.round(maxY)) {
//                    bottomSheet.setY(maxY + (bottomSheet.getY() - maxY) * a);
//                    handler.post(runnable);
//                }
//            }
//        };
//        handler.post(runnable);
        bottomSheet.animate()
                .y(maxY)
                .setDuration(300)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

    public void openBottomSheet() {
        status = SHEET_STATUS.OPENED;
        bottomSheet.animate()
                .y(minY)
                .setDuration(300)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (Math.round(bottomSheet.getY()) != Math.round(minY)) {
//                    bottomSheet.setY(minY + (bottomSheet.getY() - minY) * a);
//                    handler.post(runnable);
//                }
//            }
//        };
//        handler.post(runnable);
    }

    public SHEET_STATUS getStatus(){
        return status;
    }
}
