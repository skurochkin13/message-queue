package com.testtask.message;

/**
 * Class of message for queue.
 *
 */
public class KeyValueMessage
{
    private int key;
    private String value;

    public KeyValueMessage(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key;
        result = prime * result + value.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null ||
                this.getClass() != obj.getClass()) {
            return false;
        }
        KeyValueMessage otherMessage = (KeyValueMessage) obj;
        return this.key == otherMessage.getKey() && this.value.equals(otherMessage.getValue());
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + key + "=" + value;
    }
}
