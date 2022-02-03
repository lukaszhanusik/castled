package io.castled.forms;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {

    String description() default "";

    String title() default "";

    String placeholder() default "";

    FormFieldSchema schema() default FormFieldSchema.STRING;

    FormFieldType type() default FormFieldType.TEXT_BOX;

    String group() default FormGroups.DEFAULT_GROUP;

    String loadingText() default "";

    GroupActivator groupActivator() default @GroupActivator();

    OptionsRef optionsRef() default @OptionsRef();

    boolean required() default true;


}
