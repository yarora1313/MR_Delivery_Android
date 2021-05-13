package com.example.mrdelivery.inputhandler.inputvalidators;

import com.example.mrdelivery.inputhandler.RegexChecks;
import com.google.android.material.textfield.TextInputLayout;

public class PhoneNumValidator extends TextValidator {
    public PhoneNumValidator(TextInputLayout textInput) {
        super(textInput);
    }

    @Override
    public void validate(String input) {
        if(!RegexChecks.isValidPhoneNum(input))
        {
            textInput.setError(RegexChecks.INVALID_PHONE);
        }
        else
        {
            textInput.setError(null);
        }
    }
}
