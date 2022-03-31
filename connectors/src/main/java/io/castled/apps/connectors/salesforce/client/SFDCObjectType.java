package io.castled.apps.connectors.salesforce.client;

public enum SFDCObjectType {
    STRING("string"),
    DOUBLE("double"),
    DATETIME("dateTime"),
    DATE("date"),
    TIME("time"),
    BOOLEAN("boolean"),
    ID("ID"),
    INTEGER("int");

    private final String name;

    SFDCObjectType(String name) {
        this.name = name;
    }

    public static SFDCObjectType fromName(String name) {
        for (SFDCObjectType sfdcObjectType : SFDCObjectType.values()) {
            if (sfdcObjectType.name.equals(name)) {
                return sfdcObjectType;
            }
        }
        return null;
    }

}
