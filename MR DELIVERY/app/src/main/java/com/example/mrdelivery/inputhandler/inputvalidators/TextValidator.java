package com.example.mrdelivery.inputhandler.inputvalidators;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public abstract class TextValidator implements TextWatcher {
    protected final TextInputLayout textInput;

    TextValidator(TextInputLayout textInput)
    {
        this.textInput = textInput;
    }

    public abstract void validate(String input);

    @Override
    final public void afterTextChanged(Editable s)
    {
        String input = Objects.requireNonNull(Objects.requireNonNull(textInput.getEditText()).getText()).toString();
        validate(input);
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
