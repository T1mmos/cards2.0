package gent.timdemey.cards.ui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.AGameEventAdapter;
import gent.timdemey.cards.entities.IContextProvider;
import gent.timdemey.cards.entities.IGameOperations;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.multiplayer.ConnectInfo;
import gent.timdemey.cards.multiplayer.HelloClientInfo;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogContent;
import net.miginfocom.swing.MigLayout;

public class JoinMultiplayerGameDialogContent extends DialogContent<Void, ConnectInfo> 
{
    private class ServersTableModel extends AbstractTableModel
    {
        @Override
        public String getColumnName(int column) 
        {
            if (column == 0)
            {
                return Loc.get("columntitle_servername");                        
            }
            else if (column == 1)
            {
                return Loc.get("columntitle_ipaddress");
            }
            else 
            {
                throw new UnsupportedOperationException("Table only has 2 columns");
            }
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            HelloClientInfo info = serverInfos.get(rowIndex);
            if (columnIndex == 0)
            {
                return info.srvname;
            }
            else if (columnIndex == 1)
            {
                return info.address.getHostAddress() + ":" + info.tcpport;
            }
            else 
            {
                throw new UnsupportedOperationException("Table only has 2 columns");
            }
        }
        
        @Override
        public int getRowCount() {
            return serverInfos.size();
        }
        
        @Override
        public int getColumnCount() {
            return 2;
        }
    }
    
    private class ServerInfoListener extends AGameEventAdapter
    {
        @Override
        public void onHelloClient() {
            serverInfos.clear();
            serverInfos.addAll(Services.get(IContextProvider.class).getThreadContext().getCardGameState().getServers());
            tableModel.fireTableDataChanged();
        }
    }
    
    private class RefreshListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Services.get(IGameOperations.class).helloServerStop();
            serverInfos.clear();
            tableModel.fireTableDataChanged();            
            Services.get(IGameOperations.class).helloServerStart();
        }
    }
    
    private class SelectionListener implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            checkOk();
        }        
    }
    
    private final JScrollPane scroll_server;
    private final JTable table_servers;
    private final List<HelloClientInfo> serverInfos; // local copy
    private final ServersTableModel tableModel;
    private final JButton button_refresh;
    private final ServerInfoListener serverInfoListener;
    private final ActionListener refreshListener;
    private final SelectionListener selectionListener; 
    private final JTextField tf_name;
    
    public JoinMultiplayerGameDialogContent()
    {
        this.scroll_server = new JScrollPane();
        this.tableModel = new ServersTableModel();
        this.table_servers = new JTable(tableModel);
        this.button_refresh = new JButton(Loc.get("button_refresh"));
        this.serverInfoListener = new ServerInfoListener();
        this.serverInfos = new ArrayList<>();      
        this.refreshListener = new RefreshListener();
        this.selectionListener = new SelectionListener(); 
        this.tf_name = new JTextField(20);
    }
    
    @Override
    protected JPanel createContent(Void parameter) {
        JPanel panel = new JPanel(new MigLayout("insets 0"));        
        JLabel lb_srvname  = new JLabel(Loc.get("label_serversOnNetwork"));
        JLabel lb_playerName = new JLabel(Loc.get("label_enterPlayerName"));
        
        scroll_server.setViewportView(table_servers);
                      
        panel.add(lb_srvname, "pushx, growx");
        panel.add(button_refresh, "wrap");
        panel.add(scroll_server, "span, hmin 150, push, grow, span, wrap");
        panel.add(lb_playerName, "span, split 2");
        panel.add(tf_name, "pushx, wrap");
                        
        return panel;
    }

    @Override
    protected boolean isOk() {
        int row = table_servers.getSelectedRow();        
        return row != -1;
    }
    
    @Override
    protected ConnectInfo onClose(DialogButtonType dbType) {
        Services.get(IGameOperations.class).helloServerStop();
        Services.get(IGameOperations.class).removeGameEventListener(serverInfoListener);    
        button_refresh.removeActionListener(refreshListener);
        table_servers.getSelectionModel().removeListSelectionListener(selectionListener);        
        
        if (dbType == DialogButtonType.Ok)
        {
            int row = table_servers.getSelectedRow();        
            HelloClientInfo srvInfo = row != -1 ? serverInfos.get(row) : null;
            return new ConnectInfo(srvInfo, tf_name.getText());
        }
        else
        {
            return null;
        }
    }

    @Override
    protected void onShow() 
    {   
        Services.get(IGameOperations.class).addGameEventListener(serverInfoListener);
        button_refresh.addActionListener(refreshListener);
        table_servers.getSelectionModel().addListSelectionListener(selectionListener);
        
        Services.get(IGameOperations.class).helloServerStart();
    }
}
