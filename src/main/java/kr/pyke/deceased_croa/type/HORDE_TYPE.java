package kr.pyke.deceased_croa.type;

public enum HORDE_TYPE {
    NORMAL("normal"),
    SPECIAL("special"),
    SERVER("server");

    final String id;

    HORDE_TYPE(String id) { this.id = id;}

    public static HORDE_TYPE fromID(String id) {
        for (HORDE_TYPE hordeType : HORDE_TYPE.values()) {
            if (hordeType.id.equals(id)) { return hordeType; }
        }

        return HORDE_TYPE.NORMAL;
    }
}
