package com.example.securno;

import android.app.AlertDialog;
import android.content.DialogInterface;


/**
 * Класс для включения и выключения микрофона
 */
public class ModuleWorker {

    /**
     * Выполнение терминальных команд
     */
    TerminalWorker _CommandRunner;

    /**
     * Месседж бокс
     */
    AlertDialog.Builder _DlgAlert;

    /**
     * Базовый конструктор
     * @param termWorker Выполнение терминальных команд
     * @param dlgAlert Месседж бокс
     */
    public ModuleWorker(TerminalWorker termWorker, AlertDialog.Builder dlgAlert){
        _CommandRunner = termWorker;
        _DlgAlert = dlgAlert;
    }


    /**
     * Переключение модуля с одного состояния на другое и обратно (ON и OFF)
     * @param cmd Команда для терминала
     * @param moduleName Название модуля (негласное), который будет переключен (вкл выкл)
     * @param whatAction Вкл (TRUE) Выкл (FALSE)
     */
    public void SwitchModule(String cmd, String moduleName, boolean whatAction){

        String action = whatAction ? "ON" : "OFF";
        try {
//            _CommandRunner.RunCommand("insmod /lib/modules/`uname -r`/kernel/sound/pci/snd-intel8x0.ko");
            _CommandRunner.RunCommandAsRoot(cmd);
        }
        catch (Exception e){
            _DlgAlert.setTitle("Error");
            _DlgAlert.setMessage(e.toString());
        }
        finally {
            _DlgAlert.setMessage("Module " + moduleName + " turned " + action);
            _DlgAlert.setTitle(moduleName + " setting");
        }

        MessageBoxSetting();
    }

    /**
     * Настройка месседж бокса
     */
    private void MessageBoxSetting(){
        _DlgAlert.setPositiveButton("OK", null);
        _DlgAlert.setCancelable(true);

        _DlgAlert.create().show(); // НЕ РОБЕРТ
        _DlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });
    }
}
