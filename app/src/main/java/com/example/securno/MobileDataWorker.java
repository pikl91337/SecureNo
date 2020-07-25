package com.example.securno;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * класс для работы с мобильными данными
 */
public class MobileDataWorker {

    private Context _Context;

    public MobileDataWorker(){
        _Context = GlobalApplication.getAppContext();
    }

    /**
     * метод для получения текущего состояния мобильных данных (0,1,2,3,4)
     * @return
     */
    public int GetMobileDataState() {
        try {
            TelephonyManager telephonyService = (TelephonyManager) _Context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = Objects.requireNonNull(telephonyService).getClass().getDeclaredMethod("getDataEnabled");
//            return (boolean) (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
            return telephonyService.getDataState();
        } catch (Exception ex) {
            Log.e("MainActivity", "Error getting mobile data state", ex);
        }
        return 0;
    }
}
