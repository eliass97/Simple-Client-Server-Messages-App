package common.enums;

public enum ConnectionValidationEnum {
    ACCEPT("ACCEPT"),
    REJECT("REJECT");

    private final String value;

    ConnectionValidationEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ConnectionValidationEnum fromValueOrNull(String value) {
        for (ConnectionValidationEnum connectionValidationEnum : values()) {
            if (connectionValidationEnum.value.equals(value)) return connectionValidationEnum;
        }

        return null;
    }
}
