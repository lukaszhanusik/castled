package io.castled.forms.dtos;

import io.castled.forms.FormFieldType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RadioBoxProps extends MultiOptionProps {

    public RadioBoxProps(List<FormFieldOption> selectOptions, String title, String description) {
        super(FormFieldType.RADIO_BOX, selectOptions, title, description);
    }

    public RadioBoxProps(String optionsReference, String title, String description) {
        super(FormFieldType.RADIO_BOX, optionsReference, title, description);

    }
}