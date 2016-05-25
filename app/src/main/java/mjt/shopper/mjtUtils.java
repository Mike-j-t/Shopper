package mjt.shopper;

import android.util.Log;
import java.util.Date;

/**
 * Created by Mike092015 on 2/03/2016.
 */

//Emsg (Extended Message for returning more than just a boolean for validation, rather:
// boolean (true = error condition)
// integer (return code)
// String (Message)
class Emsg {
    private boolean error_indicator;
    private int error_number;
    private String error_message;

    public Emsg() {
        this.error_indicator = true;
        this.error_number = 0;
        this.error_message = "";
    }
    public Emsg(boolean error_indicator, int error_number, String error_message) {
        this.error_indicator = error_indicator;
        this.error_number = error_number;
        this.error_message = error_message;
    }
    public boolean getErrorIndicator() {
        return this.error_indicator;
    }
    public int getErrorNumber() {
        return this.error_number;
    }
    public String getErrorMessage() {
        return  this.error_message;
    }
    public void setErrorIndicator(boolean error_indicator) {
        this.error_indicator = error_indicator;
    }
    public void  setErrorNumber(int error_number) {
        this.error_number = error_number;
    }
    public void setErrorMessage(String error_message) {
        this.error_message = error_message;
    }
    public void setAll(boolean error_indicator, int error_number, String error_message) {
        this.error_indicator = error_indicator;
        this.error_number = error_number;
        this.error_message = error_message;
    }
}

class mjtUtils {
    public final static int LOG_ERRORMSG = Constants.LOGTYPE_ERROR;
    public final static int LOG_WARNINGMSG = Constants.LOGTYPE_WARNING;
    public final static int LOG_DEBUGMSG = Constants.LOGTYPE_DEBUG;
    public final static int LOG_INFORMATIONMSG = Constants.LOGTYPE_INFORMATIONAL;
    public static void logMsg(int msgtype, String msg, String calleractivity, String callermethod, boolean debugmode) {
        if(!debugmode) {
            return;
        }
        String fullmessage = (new Date()).toString() + " Activity=" +  calleractivity +
                " Method=" + callermethod + " MSG=" +msg;
        switch(msgtype) {
            case LOG_ERRORMSG: {
                Log.e(Constants.LOG, fullmessage);
                break;
            }
            case LOG_WARNINGMSG: {
                Log.w(Constants.LOG, fullmessage);
                break;
            }
            case LOG_DEBUGMSG: {
                Log.d(Constants.LOG, fullmessage);
                break;
            }
            case LOG_INFORMATIONMSG: {
                Log.i(Constants.LOG, fullmessage);
                break;
            }
            default: {
                Log.d(Constants.LOG, fullmessage);
            }
        }
    }

    public static Emsg validateInteger(String integertocheck) {
        Emsg retmsg = new Emsg(false,0,"");
        try {
            int givenint = Integer.parseInt(integertocheck);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            retmsg.setAll(true, 1, "Invalid Integer - Must be nnn.");
        }
        return retmsg;
    }

    public static Emsg validateMonetary(String monetarytocheck) {
        Emsg retmsg = new Emsg(false, 0, "");
        int partcount = 0;
        String wholeamnt = "";
        String fractionamnt = "";

        String amnts[] = monetarytocheck.split(".");
        if(amnts.length > 1) {
            retmsg.setAll(true, 1, "Invalid Monetary Number - More than 1 decimal point.");
            return retmsg;
        }

        try {
            Float monetaryfloat = Float.parseFloat(monetarytocheck);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            retmsg.setAll(true,2,"Invalid Monetary Number - Should be nnn.nn or nnn");
            return retmsg;
        }
        return retmsg;
    }

    public static Emsg validateDate(String datetocheck) {
        Emsg retmsg = new Emsg(false, 0, "");
        // Valid Length Check ie must be a minimum of 8 characters in Length and a maximum of 10
        if(datetocheck.length() < 8 | datetocheck.length() > 10)  {
            retmsg.setAll(true, 1, "Invalid Length (must be 8-10) it was " + datetocheck.length());
            return retmsg;
        }
        String day = "";
        String month = "";
        String year = "";
        int dayasint = 0;
        int monthasint = 0;
        int yearasint = 0;

        int partcount = 0;
        String dateparts[] = datetocheck.split("/");
        for(String cpart: dateparts) {
            partcount++;
            switch (partcount) {
                case 1:
                    day = cpart;
                    break;
                case 2:
                    month = cpart;
                    break;
                case 3:
                    year = cpart;
                    break;
            }
        }
        if(partcount != 3) {
            retmsg.setAll(true, 2, "Invalid Format - Must have 3 parts seperated by 2 /'s (dd/MM/yyyy).");
            return retmsg;
        }
        if(day.length() < 1 | day.length() > 2) {
            retmsg.setAll(true, 3, "Invalid Day - Must be 1 or 2 numerics.");
            return retmsg;
        }
        if(month.length() < 1 | month.length() > 2 ) {
            retmsg.setAll(true, 4, "Invalid Month - Must be 1 or 2 numerics.");
            return retmsg;
        }
        if(year.length() != 4) {
            retmsg.setAll(true, 5, "Invalid Year - Must be 4 numerics.");
            return retmsg;
        }
        try {
            dayasint = Integer.parseInt(day);
        } catch (NumberFormatException e) {
            retmsg.setAll(true, 6, "Invalid Day - Non-numeric(s).");
            return retmsg;
        }
        try {
            monthasint = Integer.parseInt(month);
        } catch (NumberFormatException e) {
            retmsg.setAll(true, 7, "Invalid Month - Non-numeric(s).");
            return retmsg;
        }
        try {
            yearasint = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            retmsg.setAll(true, 8, "Invalid Year - Non-numeric(s).");
            return retmsg;
        }


        int leapyear = 0;
        if(yearasint % 4 == 0) {
            if(yearasint % 100 == 0) {
                if(yearasint % 400 == 0) {
                    leapyear = 1;
                }

            } else {
                leapyear = 1;
            }
        }
        // Definine the Days in Each month adjusting for leap years
        int[] daysinmonth = {31,28+leapyear,31,30,31,30,31,31,30,31,30,31};

        if(monthasint < 1 | monthasint > 12) {
            retmsg.setAll(true, 10, "Invalid Month - Must be 1-12.");
            return retmsg;
        }

        if(dayasint < 1 | dayasint > daysinmonth[(monthasint - 1)] ) {
            retmsg.setAll(true, 9, "Invalid Day - Must be 1-" + daysinmonth[(monthasint - 1)]);
            return retmsg;
        }

        if(yearasint < 1970) {
            retmsg.setAll(true, 11, "Invalid Year - The Year must be 1970 or later.");
            return retmsg;
        }
        return retmsg;
    }
}