package com.aaronep.andy;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by aaronep on 3/22/18.
 */


@IgnoreExtraProperties
public class Total {



    public String fb_timName;
    public String a_calcDurationPost;
    public Double a_scoreHRRoundLast;
    public Double a_scoreHRTotal;
    public Double a_speedLast;
    public Double a_speedTotal;
    public Integer fb_Date;
    public Integer fb_DateNow;
    public Integer fb_maxHRTotal;
    public Double fb_scoreHRRoundLast;
    public Double fb_scoreHRTotal;

    public Double fb_timAvgCADtotal;
    public Double fb_timAvgSPDtotal;
    public Double fb_timDistanceTraveled;
    public String fb_timGroup;
    public Double fb_timLastSPD;
    public String fb_timTeam;




    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Total() {
    }

    public Total(String fb_timName, Double a_scoreHRTotal) {

        this.fb_timName = fb_timName;
        this.a_scoreHRTotal = a_scoreHRTotal;

        this.a_calcDurationPost = "1";
        this.a_scoreHRRoundLast = 1.0;
        this.a_speedLast = 1.0;
        this.a_speedTotal = 1.0;
        this.fb_Date = 1;
        this.fb_DateNow = 1;
        this.fb_maxHRTotal = 1;
        this.fb_scoreHRRoundLast = 1.0;
        this.fb_scoreHRTotal = 1.0;
        this.fb_timAvgCADtotal = 1.0;
        this.fb_timAvgSPDtotal = 1.0;
        this.fb_timDistanceTraveled = 1.0;
        this.fb_timGroup = "group";
        this.fb_timLastSPD = 1.0;
        this.fb_timTeam = "team";
    }

}

//{
//        "a_calcDurationPost" : "00:40:05",
//        "a_scoreHRRoundLast" : 65.4,
//        "a_scoreHRTotal" : 65.4,
//        "a_speedLast" : 0,
//        "a_speedTotal" : 0,
//        "fb_Date" : "20180321",
//        "fb_DateNow" : 1521640371651,
//        "fb_maxHRTotal" : "185",
//        "fb_scoreHRRoundLast" : 65.4,
//        "fb_scoreHRTotal" : 65.4,
//        "fb_timAvgCADtotal" : 0,
//        "fb_timAvgSPDtotal" : 0,
//        "fb_timDistanceTraveled" : 0,
//        "fb_timGroup" : "M",
//        "fb_timLastSPD" : 0,
//        "fb_timName" : "OG",
//        "fb_timTeam" : "Solo"
//        }