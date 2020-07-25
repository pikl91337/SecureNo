package com.example.securno;

import android.content.Context;

public class FakeGpsThread extends Thread {

    Context _Context;

    public FakeGpsThread(Context context){
        _Context = context;
    }
    @Override
    public void run() {
        super.run();
        GpsFaker gpsFaker = new GpsFaker();
        gpsFaker.FakeGps(100,50,_Context);
    }
}
