package com.pclink.attendance.system.TabRequest;

public class itemCardViewVacationList {
    private  String VacationTxt;


    private String VacId;
    public itemCardViewVacationList(String vacationTxt ,String vacId ) {

        VacationTxt = vacationTxt;
        VacId=vacId;
    }



    public String getVacId()
    {
        return VacId;
    }

    public void setVacId(String vacId)
    {
        VacId = vacId;
    }




    public String getVacationTxt()
    {
        return VacationTxt;
    }

    public void setVacationTxt(String vacationTxt)
    {
        VacationTxt = vacationTxt;
    }


}