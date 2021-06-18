package gent.timdemey.cards.ui.components.ext;

public interface IHasComponent<T extends IComponent>
{
    T getComponent();
    void setComponent(T scomp);
}
