package com.pclink.attendance.system.Wrapper;


import com.pclink.attendance.system.Models.LogExcusesModel;

import java.util.Collections;
import java.util.List;

public class LogExcusesWrapper {
    private List<LogExcusesModel>excuses;

    public List<LogExcusesModel> getExcuses() {

        Collections.reverse(excuses);
        return excuses;
    }

    public void setExcuses(List<LogExcusesModel> excuses) {
        this.excuses = excuses;
    }
}
