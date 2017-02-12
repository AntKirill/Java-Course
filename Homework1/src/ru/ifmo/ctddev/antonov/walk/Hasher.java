package ru.ifmo.ctddev.antonov.walk;

class Hasher {
    private final int FNV_32_PRIME = 0x01000193;
    private int hval = 0x811c9dc5;

    int FNV(final byte[] buf, int len) {
        for (int i = 0; i < len; i++) {
            hval = (hval * FNV_32_PRIME) ^ (buf[i] & 0xff);
        }
        return hval;
    }

    int getInitialValue() {
        return 0x811c9dc5;
    }
}
