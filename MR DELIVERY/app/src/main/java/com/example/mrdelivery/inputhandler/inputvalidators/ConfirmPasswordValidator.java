package com.example.mrdelivery.inputhandler.inputvalidators;

import com.example.mrdelivery.inputhandler.RegexChecks;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ConfirmPasswordValidator extends TextValidator {

    public PassHelper inputPassHelper;
    private TextInputLayout inputPass;

    private class PassHelper extends PasswordValidator{

        PassHelper(TextInputLayout passInput) {
            super(passInput);
        }

        @Override
        public void validate(String input){
            if(Objects.requireNonNull(ConfirmPasswordValidator.this.textInput.getEditText()).getText().toString().equals(input) && RegexChecks.isValidPassword(input))
            {
                ConfirmPasswordValidator.this.textInput.setError(null);
            }
            else if(!ConfirmPasswordValidator.this.textInput.getEditText().getText().toString().equals(input))
            {
                ConfirmPasswordValidator.this.textInput.setError(RegexChecks.INVALID_CONFIRM_PASSWORD);
            }
            else
            {
                ConfirmPasswordValidator.this.textInput.setError(RegexChecks.INVALID_PASSWORD);
            }
        }
    }

    public ConfirmPasswordValidator(TextInputLayout confPassText, TextInputLayout inputPass)
    {
        super(confPassText);

        this.inputPass = inputPass;
        this.inputPassHelper = new PassHelper(this.inputPass);
    }

    @Override
    public void validate(String input) {
        if(!(Objects.requireNonNull(Objects.requireNonNull(inputPass.getEditText()).getText()).toString().equals(input)))
        {
            textInput.setError(RegexChecks.INVALID_CONFIRM_PASSWORD);
        }
        else if(!RegexChecks.isValidPassword(input))
        {
            textInput.setError(RegexChecks.INVALID_PASSWORD);
        }
        else
        {
            textInput.setError(null);
        }
    }
}
