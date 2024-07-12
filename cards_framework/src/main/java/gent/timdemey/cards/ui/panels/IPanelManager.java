package gent.timdemey.cards.ui.panels;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.readonlymodel.ReadOnlyEntityBase;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;

public interface IPanelManager
{
    public void preload();

    public boolean isPanelCreated();
    public JSLayeredPane createPanel();
    public JSLayeredPane getPanel();
    public void destroyPanel();

    public void onShown();
    public void onHidden();
    // public boolean isVisible();

    public void addComponentCreators(List<Runnable> compCreators);
    public void addRescaleRequests(List<? super RescaleRequest> requests);

    public void updateComponent(JComponent comp);

    public void positionComponents();
    public void positionComponent(JComponent jcomp);

    /**
     * Gets the component managed by the given panel manager at the given position
     * with the highest Z-order.
     * 
     * @param p
     * @return
     */
    JComponent getComponentById(UUID compId);
    JComponent getComponentByEntityId(UUID entityId);
    JComponent getComponent(ReadOnlyEntityBase<?> entity);
    public JComponent getComponentAt(Point p);
    public List<JComponent> getComponentsAt(Point p);
    public List<JComponent> getComponentsIn(Rectangle rect);
    public <T extends JComponent> List<T> getComponentsAt(Point p, Class<T> clazz);
    public List<JComponent> getComponents();

    public void startAnimate(JComponent scaleComp);
    public void stopAnimate(JComponent scaleComp);




   
}
