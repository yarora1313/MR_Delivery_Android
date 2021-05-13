package com.example.mrdelivery.inputhandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexChecks {
    private static final String FULL_NAME_PATTERN = "^ *[a-zA-Z'. ]+ *$";
    private static final String EMAIL_PATTERN = "^ *([\\w\\-.+]*[\\w]+@[a-zA-Z0-9\\-][a-zA-Z0-9\\-.]*)\\.([a-zA-Z]{2,}) *$";
    private static final String PHONE_NUM_PATTERN = "^ *(\\+?\\d{1,3})?[789][\\d]{9} *$";
    private static final String PASSWORD_PATTERN = "^.{8,}$";

    private static final String INVALID_INPUT = "Please enter a valid ";
    public static final String INVALID_NAME = INVALID_INPUT + "Name.";
    public static final String INVALID_EMAIL = INVALID_INPUT + "Email-ID.";
    public static final String INVALID_PHONE = INVALID_INPUT + "Phone Number.";
    public static final String INVALID_PASSWORD = "Your password should contain at least 8 characters.";
    public static final String INVALID_CONFIRM_PASSWORD = "Passwords don't match.";

    public static boolean isValidName(String fullName)
    {
        return (Pattern.compile(FULL_NAME_PATTERN)).matcher(fullName).matches();
    }

    public static boolean isValidEmailID(String emailID)
    {
        return (Pattern.compile(EMAIL_PATTERN)).matcher(emailID).matches();
    }

    public static boolean isValidPhoneNum(String phoneNum)
    {
        return (Pattern.compile(PHONE_NUM_PATTERN)).matcher(phoneNum).matches();
    }

    public static boolean isValidPassword(String password)
    {
        return (Pattern.compile(PASSWORD_PATTERN)).matcher(password).matches();
    }

    public static boolean validateUserReg(String fullName, String emailID, String phoneNum, String password, String confPass)
    {
        return (isValidName(fullName) && isValidPassword(password) && isValidPhoneNum(phoneNum)
                && isValidEmailID(emailID) && password.equals(confPass));
    }

    public static boolean validateUserLogin(String emailID)
    {
        return isValidEmailID(emailID);
    }

    @Deprecated
    public static String getValidEMAILID(String emailID)
    {
        Pattern pat = Pattern.compile(EMAIL_PATTERN);
        Matcher match = pat.matcher(emailID);

        if(match.find() && isValidEmailID(emailID))
        {
            return (match.group(1) + "," + match.group(2));
        }
        else
        {
            throw new RuntimeException("REGEX ERROR");
        }
    }
}