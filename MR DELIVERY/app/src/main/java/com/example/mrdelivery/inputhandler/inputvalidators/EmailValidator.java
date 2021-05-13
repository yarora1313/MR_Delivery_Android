package com.example.mrdelivery.inputhandler.inputvalidators;

import com.example.mrdelivery.inputhandler.RegexChecks;
import com.google.android.material.textfield.TextInputLayout;

public class EmailValidator extends TextValidator {
    public EmailValidator(TextInputLayout textInput) {
        super(textInput);
    }

    @Override
    public void validate(String input) {
        if(!RegexChecks.isValidEmailID(input))
        {
            textInput.setError(RegexChecks.INVALID_EMAIL);
        }
        else
        {
            textInput.setError(null);
        }
    }
}
