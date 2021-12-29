package common.enums;

public enum CommandTypeEnum {
    DISCONNECT("/disconnect"),
    CHANGE_PASSWORD("/changepass"),
    CHANGE_USERNAME("/changename"),
    WHISPER("/whisper"),
    SHOW_CONTACTS("/showcontacts");

    private final String value;

    CommandTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CommandTypeEnum fromValueOrNull(String value) {
        for (CommandTypeEnum commandTypeEnum : values()) {
            if (commandTypeEnum.value.equals(value)) return commandTypeEnum;
        }

        return null;
    }
}
