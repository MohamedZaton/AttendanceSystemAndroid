package com.pclink.attendance.system.Wrapper;


import com.pclink.attendance.system.Models.LogCheckdataModel;

import java.util.Collections;
import java.util.List;

public class LogCheckdataWrapper {
    private List<LogCheckdataModel> checkdata;

    public List<LogCheckdataModel> getCheckdata() {
        Collections.reverse(checkdata);

        return checkdata;
    }

    public void setCheckdata(List<LogCheckdataModel> checkdata) {
        this.checkdata = checkdata;
    }
}
