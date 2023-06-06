public class Checker {
    public enum Type {
        RED, BLACK
    }

    private Type type;
    private boolean isKing;

    public Checker(Type type) {
        this.type = type;
        this.isKing = false;
    }

    public Type getType() {
        return type;
    }

    public boolean isKing() {
        return isKing;
    }

    public void makeKing() {
        this.isKing = true;
    }
}

