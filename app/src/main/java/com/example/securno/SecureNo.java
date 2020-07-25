/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.securno;

import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.widget.Toast;

import java.util.List;

/**
 * This activity provides a comprehensive UI for exploring and operating the DevicePolicyManager
 * api.  It consists of two primary modules:
 *
 * 1:  A device policy controller, implemented here as a series of preference fragments.  Each
 *     one contains code to monitor and control a particular subset of device policies.
 *
 * 2:  A DeviceAdminReceiver, to receive updates from the DevicePolicyManager when certain aspects
 *     of the device security status have changed.
 */
public class SecureNo extends PreferenceActivity {

    // Miscellaneous utilities and definitions
    private static final String TAG = "DeviceAdminSample";

    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;


    // The following keys are used to find each preference item
    private static final String KEY_ENABLE_ADMIN = "key_enable_admin";
    private static final String KEY_DISABLE_CAMERA = "key_disable_camera";
    private static final String KEY_DISABLE_MICROPHONE = "key_disable_microphone";
    private static final String KEY_DISABLE_MICROPHONE_ROOT = "key_disable_microphone_module";
    private static final String KEY_DISABLE_MICROPHONE_SETPROP = "key_disable_microphone_setprop";
    private static final String KEY_SWITCH_GPS = "key_switch_gps";
    private static final String KEY_DISABLE_GPS_ROOT = "key_disable_gps_root";
    private static final String KEY_FAKE_GPS = "key_fake_gps";
    private static final String KEY_DISABLE_WIFI = "key_disable_wifi";
    private static final String KEY_DISABLE_NETWORK = "key_disable_network";
    private static final String KEY_DISABLE_CELL = "key_disable_cell_root";
    private static final String KEY_DISABLE_INTERNET_ACCESS = "key_disable_internet_access";
    private static final String KEY_ENABLE_TELEGRAM_ACCESS = "key_enable_telegram_access";
    private static final String KEY_ENABLE_YOUTUBE_ACCESS = "key_enable_youtube_access";
    private static final String KEY_ENABLE_VK_ACCESS = "key_enable_vk_access";
    private static final String KEY_ENABLE_CHROME_ACCESS = "key_enable_chrome_access";
    private static final String KEY_ENABLE_WHATSAPP_ACCESS = "key_enable_whatsapp_access";
    private static final String KEY_ENABLE_VYSOR_ACCESS = "key_enable_vysor_access";

    // Interaction with the DevicePolicyManager
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdminSample;

    private static Context _MainContext;

    /**
     * Точка входа в программу
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _MainContext = this;

        // Prepare to work with the DPM
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(this, DeviceAdminSampleReceiver.class);
    }

    /**
     * We override this method to provide PreferenceActivity with the top-level preference headers.
     * Построение заголовков.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.device_admin_headers, target);
    }

    /**
     * Helper to determine if we are an active admin
     */
    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return GeneralFragment.class.getName().equals(fragmentName)
        || RootFragment.class.getName().equals(fragmentName)
        || NetworkFragment.class.getName().equals(fragmentName);
    }

    /**
     * Common fragment code for DevicePolicyManager access.  Provides two shared elements:
     *
     *   1.  Provides instance variables to access activity/context, DevicePolicyManager, etc.
     *   2.  Provides support for the "set password" button(s) shared by multiple fragments.
     */
    public static class AdminSampleFragment extends PreferenceFragment
            implements OnPreferenceChangeListener, OnPreferenceClickListener{

        // Useful instance variables
        protected SecureNo mActivity;
        protected DevicePolicyManager mDPM;
        protected ComponentName mDeviceAdminSample;
        protected boolean mAdminActive;


        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Retrieve the useful instance variables
            mActivity = (SecureNo) getActivity();
            mDPM = mActivity.mDPM;
            mDeviceAdminSample = mActivity.mDeviceAdminSample;
            mAdminActive = mActivity.isActiveAdmin();

        }

        @Override
        public void onResume() {
            super.onResume();
            mAdminActive = mActivity.isActiveAdmin();
            reloadSummaries();
        }

        /**
         * Called automatically at every onResume.  Should also call explicitly any time a
         * policy changes that may affect other policy values.
         */
        protected void reloadSummaries() {
        }

        protected void postReloadSummaries() {
            getView().post(new Runnable() {
                @Override
                public void run() {
                    reloadSummaries();
                }
            });
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            return false;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return false;
        }

    }

    /**
     * PreferenceFragment for "general" preferences.
     */
    public static class GeneralFragment extends AdminSampleFragment
            implements OnPreferenceChangeListener, OnPreferenceClickListener {

        private Context _Context = GlobalApplication.getAppContext();
        private AudioManager _AudioManager = (AudioManager) _Context.getSystemService(Context.AUDIO_SERVICE);
        private LocationManager _LocationManager = (LocationManager) _Context.getSystemService(Context.LOCATION_SERVICE);

        // UI elements
        private CheckBoxPreference mEnableCheckbox;
        private CheckBoxPreference mDisableCameraCheckbox;
        private CheckBoxPreference mDisableMicrophoneCheckbox;
        private Preference mSwitchGps;
        private CheckBoxPreference mFakeGps;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.device_admin_general);
            mEnableCheckbox = (CheckBoxPreference) findPreference(KEY_ENABLE_ADMIN);
            mEnableCheckbox.setOnPreferenceChangeListener(this);

            mDisableCameraCheckbox = (CheckBoxPreference) findPreference(KEY_DISABLE_CAMERA);
            mDisableCameraCheckbox.setOnPreferenceChangeListener(this);

            mDisableMicrophoneCheckbox = (CheckBoxPreference) findPreference(KEY_DISABLE_MICROPHONE);
            mDisableMicrophoneCheckbox.setOnPreferenceChangeListener(this);

            mSwitchGps = findPreference(KEY_SWITCH_GPS);
            mSwitchGps.setOnPreferenceClickListener(this);

            mFakeGps = (CheckBoxPreference) findPreference(KEY_FAKE_GPS);
            mFakeGps.setOnPreferenceChangeListener(this);

        }

        // At onResume time, reload UI with current values as required
        @Override
        public void onResume() {
            super.onResume();
            mEnableCheckbox.setChecked(mAdminActive);
            enableDeviceCapabilitiesArea(mAdminActive);

            if (mAdminActive) {
                mDPM.setCameraDisabled(mDeviceAdminSample, mDisableCameraCheckbox.isChecked());
                reloadSummaries();
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (super.onPreferenceChange(preference, newValue)) {
                return true;
            }
            if (preference == mEnableCheckbox) {
                boolean value = (Boolean) newValue;
                if (value != mAdminActive) {
                    if (value) {
                        // Launch the activity to have the user enable our admin.
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                mActivity.getString(R.string.add_admin_extra_app_text));
                        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                        // return false - don't update checkbox until we're really active
                        return false;
                    } else {
                        mDPM.removeActiveAdmin(mDeviceAdminSample);
                        enableDeviceCapabilitiesArea(false);
                        mAdminActive = false;
                    }
                }
            } else if (preference == mDisableCameraCheckbox) {
                boolean value = (Boolean) newValue;
                mDPM.setCameraDisabled(mDeviceAdminSample, value);
                // Delay update because the change is only applied after exiting this method.

                postReloadSummaries();
            } else if (preference == mDisableMicrophoneCheckbox){
                boolean value = (Boolean) newValue;
                _AudioManager.setMicrophoneMute(value);
                postReloadSummaries();
            } else if (preference == mFakeGps){

                boolean value = (Boolean) newValue;

                GpsFaker gpsFaker = new GpsFaker();

                if (value){

                    gpsFaker.FakeGps(40,74,_MainContext);
                    mFakeGps.setSummary("Gps enabled. Check Google maps");
                    postReloadSummaries();
                }
                else{
                    gpsFaker.RemoveTestProvider();

                    mFakeGps.setSummary("Gps faker disabled");
                    postReloadSummaries();
                }
            }
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (mSwitchGps != null && preference == mSwitchGps) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

                // костыль :)))
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                postReloadSummaries();
                return true;
            }
            return false;
        }

        @Override
        protected void reloadSummaries() {
            super.reloadSummaries();
            String cameraSummary = getString(mDPM.getCameraDisabled(mDeviceAdminSample)
                    ? R.string.camera_disabled : R.string.camera_enabled);
            mDisableCameraCheckbox.setSummary(cameraSummary);

            String microphoneSummary = getString(_AudioManager.isMicrophoneMute()
                    ? R.string.microphone_disabled : R.string.microphone_enabled);
            mDisableMicrophoneCheckbox.setSummary(microphoneSummary);

            String gpsSwitcherSummary = getString(_LocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            ? R.string.gps_enabled : R.string.gps_disabled);
            mSwitchGps.setSummary(gpsSwitcherSummary);

        }

        /** Updates the device capabilities area (dis/enabling) as the admin is (de)activated */
        private void enableDeviceCapabilitiesArea(boolean enabled) {
            mDisableCameraCheckbox.setEnabled(enabled);
            mDisableMicrophoneCheckbox.setEnabled(enabled);
            mSwitchGps.setEnabled(enabled);
            mFakeGps.setEnabled(enabled);
        }
    }

    /**
     * PreferenceFragment for "root" preferences.
     */
    public static class RootFragment extends AdminSampleFragment
            implements OnPreferenceChangeListener{

        // UI elements
        private CheckBoxPreference mDisableMicrophoneCheckbox;
        private CheckBoxPreference mDisableMicrophoneSetpropCheckbox;
        private CheckBoxPreference mDisableGpsCheckbox;
        private CheckBoxPreference mDisableCellCheckbox;
        private CheckBoxPreference mDisableInternetAccess;
        private CheckBoxPreference mEnableTelegramAccess;
        private CheckBoxPreference mEnableYoutubeAccess;
        private CheckBoxPreference mEnableVkAccess;
        private CheckBoxPreference mEnableChromeAccess;
        private CheckBoxPreference mEnableWhatsappAccess;
        private CheckBoxPreference mEnableVysorAccess;

        private Context _Context = GlobalApplication.getAppContext();
        private TerminalWorker terminalWorker = new TerminalWorker();
        private AlertDialog.Builder dlgAlert = new AlertDialog.Builder(_Context);
        private ModuleWorker moduleWorker = new ModuleWorker(terminalWorker,dlgAlert);
        private LocationManager _LocationManager = (LocationManager) _Context.getSystemService(Context.LOCATION_SERVICE);
        private MobileDataWorker _MobileDataWorker = new MobileDataWorker();

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            addPreferencesFromResource(R.xml.device_admin_root);

            mDisableMicrophoneCheckbox = (CheckBoxPreference) findPreference(KEY_DISABLE_MICROPHONE_ROOT);
            mDisableMicrophoneCheckbox.setOnPreferenceChangeListener(this);

            mDisableMicrophoneSetpropCheckbox = (CheckBoxPreference) findPreference(KEY_DISABLE_MICROPHONE_SETPROP);
            mDisableMicrophoneSetpropCheckbox.setOnPreferenceChangeListener(this);

            mDisableGpsCheckbox = (CheckBoxPreference) findPreference(KEY_DISABLE_GPS_ROOT);
            mDisableGpsCheckbox.setOnPreferenceChangeListener(this);

            mDisableCellCheckbox = (CheckBoxPreference) findPreference(KEY_DISABLE_CELL);
            mDisableCellCheckbox.setOnPreferenceChangeListener(this);

            mDisableInternetAccess = (CheckBoxPreference) findPreference(KEY_DISABLE_INTERNET_ACCESS);
            mDisableInternetAccess.setOnPreferenceChangeListener(this);

            mEnableTelegramAccess = (CheckBoxPreference) findPreference(KEY_ENABLE_TELEGRAM_ACCESS);
            mEnableTelegramAccess.setOnPreferenceChangeListener(this);

            mEnableYoutubeAccess = (CheckBoxPreference) findPreference(KEY_ENABLE_YOUTUBE_ACCESS);
            mEnableYoutubeAccess.setOnPreferenceChangeListener(this);

            mEnableVkAccess = (CheckBoxPreference) findPreference(KEY_ENABLE_VK_ACCESS);
            mEnableVkAccess.setOnPreferenceChangeListener(this);

            mEnableChromeAccess = (CheckBoxPreference) findPreference(KEY_ENABLE_CHROME_ACCESS);
            mEnableChromeAccess.setOnPreferenceChangeListener(this);

            mEnableWhatsappAccess = (CheckBoxPreference) findPreference(KEY_ENABLE_WHATSAPP_ACCESS);
            mEnableWhatsappAccess.setOnPreferenceChangeListener(this);

            mEnableVysorAccess = (CheckBoxPreference) findPreference(KEY_ENABLE_VYSOR_ACCESS);
            mEnableVysorAccess.setOnPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            if (!mDisableInternetAccess.isChecked()){
                enableNetworkAccessArea(false);
                mDisableInternetAccess.setSummary(R.string.internet_enabled);
            } else {
                mDisableInternetAccess.setSummary(R.string.internet_disabled);
            }
            reloadSummaries();
        }

        private void enableNetworkAccessArea(boolean enabled) {
            mEnableTelegramAccess.setEnabled(enabled);
            mEnableYoutubeAccess.setEnabled(enabled);
            mEnableWhatsappAccess.setEnabled(enabled);
            mEnableChromeAccess.setEnabled(enabled);
            mEnableVkAccess.setEnabled(enabled);
            mEnableVysorAccess.setEnabled(enabled);
        }

        @Override
        protected void reloadSummaries() {
            super.reloadSummaries();
            String gpsDisablerSummary = getString(_LocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    ? R.string.gps_enabled : R.string.gps_disabled);
            mDisableGpsCheckbox.setSummary(gpsDisablerSummary);

            int test = _MobileDataWorker.GetMobileDataState();
            if (test == 0){
                mDisableCellCheckbox.setSummary("Mobile data disabled");
            }
            if (test == 2){
                mDisableCellCheckbox.setSummary("Mobile data enabled");
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (super.onPreferenceChange(preference, newValue)) {
                return true;
            }
             if (preference == mDisableMicrophoneCheckbox){
                boolean value = (Boolean) newValue;
                 moduleWorker.SwitchModule("insmod /lib/modules/`uname -r`/kernel/sound/pci/snd-intel8x0.ko","Microphone",value);
            } else if (preference == mDisableInternetAccess){
                 boolean value = (Boolean) newValue;
                 if (value){
                     terminalWorker.RunCommandAsRoot("iptables -P OUTPUT DROP");
//                     terminalWorker.RunCommandAsRoot("iptables -P INPUT DROP");
                     enableNetworkAccessArea(true);
                     mDisableInternetAccess.setSummary(R.string.internet_disabled);
                     reloadSummaries();
                 } else {
                     terminalWorker.RunCommandAsRoot("iptables -P OUTPUT ACCEPT");
//                     terminalWorker.RunCommandAsRoot("iptables -P INPUT ACCEPT");
                     enableNetworkAccessArea(false);
                     mDisableInternetAccess.setSummary(R.string.internet_enabled);
                     reloadSummaries();
                 }
             }
             else if (preference == mEnableTelegramAccess){
                 boolean value = (Boolean) newValue;
                 if (value){

                     // разрешаем телеграму доступ в интернет (10097 - uid)
                     terminalWorker.RunCommandAsRoot(String.format("iptables -A OUTPUT -m owner --uid-owner %s -j ACCEPT","10097"));
//                     terminalWorker.RunCommandAsRoot(String.format("iptables -A INPUT -P -m owner --uid-owner 10097 -j ACCEPT"));
                     mEnableTelegramAccess.setSummary(R.string.access_provided);
                 } else {
                     // запрещаем телеграму доступ в интернет
                     terminalWorker.RunCommandAsRoot(String.format("iptables -D OUTPUT -m owner --uid-owner %s -j ACCEPT","10097"));
                     mEnableTelegramAccess.setSummary("");
                 }
             }
             else if (preference == mEnableYoutubeAccess){
                 boolean value = (Boolean) newValue;
                 if (value){

                     // разрешаем ютубу доступ в интернет (10087 - uid)
                     terminalWorker.RunCommandAsRoot(String.format("iptables -A OUTPUT -m owner --uid-owner %s -j ACCEPT","10087"));
//                     terminalWorker.RunCommandAsRoot(String.format("iptables -A INPUT -P -m owner --uid-owner 10097 -j ACCEPT"));
                     mEnableYoutubeAccess.setSummary(R.string.access_provided);
                 } else {
                     // запрещаем ютубу доступ в интернет
                     terminalWorker.RunCommandAsRoot(String.format("iptables -D OUTPUT -m owner --uid-owner %s -j ACCEPT","10087"));
                     mEnableYoutubeAccess.setSummary("");
                 }
             }
             else if (preference == mEnableVkAccess){
                 boolean value = (Boolean) newValue;
                 if (value){

                     // разрешаем вк доступ в интернет (10094 - uid)
                     terminalWorker.RunCommandAsRoot(String.format("iptables -A OUTPUT -m owner --uid-owner %s -j ACCEPT","10094"));
//                     terminalWorker.RunCommandAsRoot(String.format("iptables -A INPUT -P -m owner --uid-owner 10097 -j ACCEPT"));
                     mEnableVkAccess.setSummary(R.string.access_provided);
                 } else {
                     // запрещаем вк доступ в интернет
                     terminalWorker.RunCommandAsRoot(String.format("iptables -D OUTPUT -m owner --uid-owner %s -j ACCEPT","10094"));
                     mEnableVkAccess.setSummary("");
                 }
             }
             else if (preference == mEnableChromeAccess){
                 boolean value = (Boolean) newValue;
                 if (value){

                     // разрешаем хрому доступ в интернет (10052 - uid)
                     terminalWorker.RunCommandAsRoot(String.format("iptables -A OUTPUT -m owner --uid-owner %s -j ACCEPT","10052"));
//                     terminalWorker.RunCommandAsRoot(String.format("iptables -A INPUT -P -m owner --uid-owner 10097 -j ACCEPT"));
                     mEnableChromeAccess.setSummary(R.string.access_provided);
                 } else {
                     // запрещаем хрому доступ в интернет
                     terminalWorker.RunCommandAsRoot(String.format("iptables -D OUTPUT -m owner --uid-owner %s -j ACCEPT","10052"));
                     mEnableChromeAccess.setSummary("");
                 }
             }
             else if (preference == mEnableWhatsappAccess){
                 boolean value = (Boolean) newValue;
                 if (value){

                     // разрешаем вотсаппу доступ в интернет (10079 - uid)
                     terminalWorker.RunCommandAsRoot(String.format("iptables -A OUTPUT -m owner --uid-owner %s -j ACCEPT","10079"));
//                     terminalWorker.RunCommandAsRoot(String.format("iptables -A INPUT -P -m owner --uid-owner 10097 -j ACCEPT"));
                     mEnableWhatsappAccess.setSummary(R.string.access_provided);
                 } else {
                     // запрещаем вотсаппу доступ в интернет
                     terminalWorker.RunCommandAsRoot(String.format("iptables -D OUTPUT -m owner --uid-owner %s -j ACCEPT","10079"));
                     mEnableWhatsappAccess.setSummary("");
                 }

             }
             else if (preference == mEnableVysorAccess){
                 boolean value = (Boolean) newValue;
                 if (value){

                     terminalWorker.RunCommandAsRoot(String.format("iptables -A OUTPUT -m owner --uid-owner %s -j ACCEPT","10151"));

                     mEnableVysorAccess.setSummary(R.string.access_provided);
                 } else {
                     terminalWorker.RunCommandAsRoot(String.format("iptables -D OUTPUT -m owner --uid-owner %s -j ACCEPT","10151"));
                     mEnableVysorAccess.setSummary("");
                 }
             }
             else if (preference == mDisableGpsCheckbox){
                 boolean value = (Boolean) newValue;
                 if (value){
                     terminalWorker.RunCommandAsRoot("settings put secure location_providers_allowed -gps");
                     reloadSummaries();
                 } else {
                     terminalWorker.RunCommandAsRoot("settings put secure location_providers_allowed +gps");
                     reloadSummaries();
                 }
             } else if (preference == mDisableCellCheckbox){
                 boolean value = (Boolean) newValue;
                 if (value){
                     terminalWorker.RunCommandAsRoot("svc data disable");
                     try {
                         Thread.sleep(3000);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                     reloadSummaries();

                 } else {
                     terminalWorker.RunCommandAsRoot("svc data enable");
                     try {
                         Thread.sleep(3000);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                     reloadSummaries();
                 }
             } else if (preference == mDisableMicrophoneSetpropCheckbox){
                 boolean value = (Boolean) newValue;
                 if (value){
                     terminalWorker.RunCommandAsRoot("setprop ro.audio.silent 1");
                 } else {
                     terminalWorker.RunCommandAsRoot("setprop ro.audio.silent 0");
                 }
             }
            return true;
        }
    }

    /**
     * PreferenceFragment for "network" preferences.
     */
    public static class NetworkFragment extends AdminSampleFragment
            implements OnPreferenceChangeListener, OnPreferenceClickListener{

        // UI elements
        private CheckBoxPreference mDisableWifi;
        private Preference mDisableNetwork;

        private Context _Context = GlobalApplication.getAppContext();
        private WifiManager _WifiManager = (WifiManager) _Context.getSystemService(Context.WIFI_SERVICE);
        private MobileDataWorker _MobileDataWorker = new MobileDataWorker();

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            addPreferencesFromResource(R.xml.device_admin_network);

            mDisableWifi = (CheckBoxPreference) findPreference(KEY_DISABLE_WIFI);
            mDisableWifi.setOnPreferenceChangeListener(this);

            mDisableNetwork = findPreference(KEY_DISABLE_NETWORK);
            mDisableNetwork.setOnPreferenceClickListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        protected void reloadSummaries() {
            super.reloadSummaries();
            String wifiDisableSummary = getString(_WifiManager.isWifiEnabled()
                    ? R.string.wifi_enabled : R.string.wifi_disabled);
            mDisableWifi.setSummary(wifiDisableSummary);

            int test = _MobileDataWorker.GetMobileDataState();
            if (test == 0){
                mDisableNetwork.setSummary("Mobile data disabled");
            }
            if (test == 2){
                mDisableNetwork.setSummary("Mobile data enabled");
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (super.onPreferenceChange(preference, newValue)) {
                return true;
            }
            if (preference == mDisableWifi) {
                boolean value = (Boolean) newValue;
                _WifiManager.setWifiEnabled(!value);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                postReloadSummaries();
            }
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (mDisableNetwork != null && preference == mDisableNetwork) {
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                startActivity(intent);

                // костыль :)))
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                postReloadSummaries();
                return true;
            }
            return false;
        }

        protected void postReloadSummaries() {
            getView().post(new Runnable() {
                @Override
                public void run() {
                    reloadSummaries();
                }
            });
        }
    }


    /**
     * Sample implementation of a DeviceAdminReceiver.  Your controller must provide one,
     * although you may or may not implement all of the methods shown here.
     *
     * All callbacks are on the UI thread and your implementations should not engage in any
     * blocking operations, including disk I/O.
     */
    public static class DeviceAdminSampleReceiver extends DeviceAdminReceiver {
        void showToast(Context context, String msg) {
            String status = context.getString(R.string.admin_receiver_status, msg);
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED) {
                abortBroadcast();
            }
            super.onReceive(context, intent);
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_enabled));
        }

        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            return context.getString(R.string.admin_receiver_status_disable_warning);
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_disabled));
        }

    }
}
