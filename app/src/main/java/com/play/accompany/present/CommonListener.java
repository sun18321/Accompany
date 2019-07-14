package com.play.accompany.present;

public interface CommonListener {

    interface StringListener{
        void onListener(String data);
    }

    interface IntListener{
        void onListener(int i);
    }

    interface BooleanListener{
        void onListener(boolean b);
    }

    interface NullListener{
        void onListener();
    }
}
