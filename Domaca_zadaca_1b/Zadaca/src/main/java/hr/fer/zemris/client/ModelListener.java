package hr.fer.zemris.client;

public interface ModelListener {
    void addModelListener(MyListener listener);

    void removeModelListener(MyListener listener);

    void notifyListeners(String text);
}
