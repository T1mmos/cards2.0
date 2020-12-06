package gent.timdemey.cards.ui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;

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
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.C_UDP_StartServiceRequester;
import gent.timdemey.cards.model.entities.commands.C_UDP_StopServiceRequester;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.readonlymodel.ReadOnlyUDPServer;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogContentCreator;
import gent.timdemey.cards.services.interfaces.IContextService;
import net.miginfocom.swing.MigLayout;

public class JoinMultiplayerGameDialogContent extends DialogContentCreator<Void, JoinMultiplayerGameData>
{
    private class ServersTableModel extends AbstractTableModel
    {
        @Override
        public String getColumnName(int column)
        {
            if (column == 0)
            {
                return Loc.get(LocKey.TableColumnTitle_servername);
            }
            else if (column == 1)
            {
                return Loc.get(LocKey.TableColumnTitle_ipaddress);
            }
            else if (column == 2)
            {
                return Loc.get(LocKey.TableColumnTitle_lobbyPlayerCounts);
            }
            else
            {
                throw new UnsupportedOperationException("Table only has 2 columns");
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            IContextService contextServ = Services.get(IContextService.class);
            ReadOnlyUDPServer udpServer = contextServ.getThreadContext().getReadOnlyState().getServers().get(rowIndex);
            Server server = udpServer.getServer();
            if (columnIndex == 0)
            {
                return server.serverName;
            }
            else if (columnIndex == 1)
            {
                return server.inetAddress.getHostAddress() + ":" + server.tcpport;
            }
            else if (columnIndex == 2)
            {
                return udpServer.getPlayerCount() + "/" + udpServer.getMaxPlayerCount();  
            }
            else
            {
                throw new UnsupportedOperationException("Table only has 3 columns");
            }
        }

        @Override
        public int getRowCount()
        {
            IContextService contextServ = Services.get(IContextService.class);
            int size = contextServ.getThreadContext().getReadOnlyState().getServers().size();
            return size;
        }

        @Override
        public int getColumnCount()
        {
            return 3;
        }
    }

    private class ServersStateListener implements IStateListener
    {
        @Override
        public void onChange(ReadOnlyChange change)
        {
            if (change.property == ReadOnlyState.Servers)
            {
                tableModel.fireTableDataChanged();
            }
        }
    }

    private class RefreshListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            stopRequester();
            tableModel.fireTableDataChanged();
            startRequester();
        }
    }

    private class SelectionListener implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            inData.verifyButtonFunc.accept(DialogButtonType.Ok);
        }
    }

    private final JScrollPane scroll_server;
    private final JTable table_servers;
    private final ServersTableModel tableModel;
    private final JButton button_refresh;
    private final ServersStateListener serversStateListener;
    private final ActionListener refreshListener;
    private final SelectionListener selectionListener;
    private final JTextField tf_name;

    public JoinMultiplayerGameDialogContent()
    {
        this.scroll_server = new JScrollPane();
        this.tableModel = new ServersTableModel();
        this.table_servers = new JTable(tableModel);
        this.button_refresh = new JButton(Loc.get(LocKey.Button_refresh));
        this.serversStateListener = new ServersStateListener();
        this.refreshListener = new RefreshListener();
        this.selectionListener = new SelectionListener();
        this.tf_name = new JTextField(20);
    }

    @Override
    public JPanel createContent()
    {
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        JLabel lb_srvname = new JLabel(Loc.get(LocKey.Label_serversfound));
        JLabel lb_playerName = new JLabel(Loc.get(LocKey.Label_playername));

        scroll_server.setViewportView(table_servers);

        panel.add(lb_srvname, "pushx, growx");
        panel.add(button_refresh, "wrap");
        panel.add(scroll_server, "span, hmin 150, push, grow, span, wrap");
        panel.add(lb_playerName, "span, split 2");
        panel.add(tf_name, "pushx, wrap");

        return panel;
    }

    @Override
    public boolean isButtonEnabled(DialogButtonType dbType)
    {
        if (dbType == DialogButtonType.Ok)
        {
            int row = table_servers.getSelectedRow();
            return row != -1;
        }
         
        return true;
    }
    
    private void stopRequester()
    {
        C_UDP_StopServiceRequester command = new C_UDP_StopServiceRequester();
        IContextService contextServ = Services.get(IContextService.class);
        contextServ.getThreadContext().schedule(command);
    }
    
    private void startRequester()
    {
        C_UDP_StartServiceRequester command = new C_UDP_StartServiceRequester();
        IContextService contextServ = Services.get(IContextService.class);
        contextServ.getThreadContext().schedule(command);
    }

    @Override
    public JoinMultiplayerGameData onClose(DialogButtonType dbType)
    {
        stopRequester();        
                
        Services.get(IContextService.class).getThreadContext().removeStateListener(serversStateListener);
        button_refresh.removeActionListener(refreshListener);
        table_servers.getSelectionModel().removeListSelectionListener(selectionListener);

        if (dbType == DialogButtonType.Ok)
        {
            int row = table_servers.getSelectedRow();
            ReadOnlyUDPServer server = Services.get(IContextService.class).getThreadContext().getReadOnlyState()
                    .getServers().get(row);
            String playerName = tf_name.getText();
            return new JoinMultiplayerGameData(server, playerName);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onShow()
    {
        Services.get(IContextService.class).getThreadContext().addStateListener(serversStateListener);
        
        button_refresh.addActionListener(refreshListener);
        table_servers.getSelectionModel().addListSelectionListener(selectionListener);

        startRequester();
    }

    @Override
    public EnumSet<DialogButtonType> getButtonTypes()
    {
        return SET_OK_CANCEL;
    }
}
