package gent.timdemey.cards.services.scaling;

public interface IScalableComponentMouseListener
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
