package com.example.mrdelivery.inputhandler.inputvalidators;

import com.example.mrdelivery.inputhandler.RegexChecks;
import com.google.android.material.textfield.TextInputLayout;

public class NameValidator extends TextValidator {
    public NameValidator(TextInputLayout textInput) {
        super(textInput);
    }

    @Override
    public void validate(String input) {
        if(!RegexChecks.isValidName(input))
        {
            textInput.setError(RegexChecks.INVALID_NAME);
        }
        else
        {
            textInput.setError(null);
        }
    }
}
