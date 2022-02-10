package io.castled.forms.dtos;

import io.castled.forms.FormFieldType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordProps extends DisplayFieldProps {

    public PasswordProps(String title, String description) {
        super(FormFieldType.PASSWORD, title, description);
    }
}
