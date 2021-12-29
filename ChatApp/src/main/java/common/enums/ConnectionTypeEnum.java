package common.enums;

public enum ConnectionTypeEnum {
    LOGIN("LOGIN"),
    REGISTER("REGISTER"),
    CANCEL("CANCEL");

    private final String value;

    ConnectionTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ConnectionTypeEnum fromValueOrNull(String value) {
        for (ConnectionTypeEnum connectionTypeEnum : values()) {
            if (connectionTypeEnum.getValue().equals(value)) return connectionTypeEnum;
        }

        return null;
    }
}
