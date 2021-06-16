package gent.timdemey.cards.ui.components.ext;

public interface IHasMouse
{
    public void onMouseDragged();
    public void onMouseMoved();

    public void onMouseWheelMoved();

    public void onMouseClicked();
    public void onMousePressed();
    public void onMouseReleased();
    public void onMouseEntered();
    public void onMouseExited();
}
