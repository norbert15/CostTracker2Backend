package hu.bnorbi.costtracker.util;

import org.apache.commons.lang3.StringUtils;

public abstract class Validators {

    public Validators() {
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isValidMonthDate(String month) {
        if (month.length() != 7 || !month.contains("-")) {
            return false;
        }

        String[] data = month.split("-");

        return data.length == 2
                && data[0].length() == 4
                && data[1].length() == 2
                && StringUtils.isNumeric(data[0])
                && StringUtils.isNumeric(data[1]);
    }
}
