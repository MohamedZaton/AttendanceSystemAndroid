package com.pclink.attendance.system.Wrapper;


import com.pclink.attendance.system.Models.LogVacationsModel;

import java.util.Collections;
import java.util.List;

public class LogVacationWrapper {
    private List<LogVacationsModel> vacations;

    public List<LogVacationsModel> getVacations() {

        Collections.reverse(vacations);

        return vacations;
    }

    public void setVacations(List<LogVacationsModel> vacations) {
        this.vacations = vacations;
    }
}
