/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dicewars.states;

import dicewars.players.Player;
import dicewars.players.PlayerHuman;
import dicewars.players.PlayerAI;
import dicewars.Settings;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 *
 * @author ganter
 */
public class PlayerEntry extends JPanel {
    private final PlayerEntry THIS = this;
    private Player player;
    private String type;
    private SettingsState parent;

    public Player getPlayer() {
        return player;
    }
    

    /**
     * Creates new form PlayerEntry
     * @param panel
     * @param type
     */
    public PlayerEntry(SettingsState panel, String type) {
        int pNum = Settings.getNumOfPlayers();
        parent = panel;
        this.type = type;
        switch(type){
            case "Human":
                player = new PlayerHuman((pNum <= Settings.COLORS.length)? pNum : 0);
                break;
            case "AI":
                player = new PlayerAI((pNum <= Settings.COLORS.length)? pNum : 0);
                break;
        }
        initComponents();
    }
    
    public void refresh(){
        lblPlayerID.setText("Player" +player.getId());
        boxTeam.setModel(new DefaultComboBoxModel<String>(){
            @Override
            public void setSelectedItem(Object val) {
                int t = Integer.parseInt(((String) val).substring(5));
                player.setTeam(t);
            }

            @Override
            public Object getSelectedItem() {
                return "Team "+player.getTeam();
            }

            @Override
            public int getSize() {
                return Settings.getNumOfPlayers();
            }

            @Override
            public String getElementAt(int i) {
                return "Team "+(i+1);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        boxTeam = new JComboBox<>();
        boxColor = new JComboBox<>();
        boxPlayerType = new JComboBox<>();
        lblPlayerID = new JLabel();
        BtnDel = new JButton();

        boxTeam.setEditable(false);
        boxTeam.setModel(new DefaultComboBoxModel<String>(){
            @Override
            public void setSelectedItem(Object val) {
                int t = Integer.parseInt(((String) val).substring(5));
                player.setTeam(t);
            }

            @Override
            public Object getSelectedItem() {
                return "Team "+player.getTeam();
            }

            @Override
            public int getSize() {
                return Settings.getNumOfPlayers();
            }

            @Override
            public String getElementAt(int i) {
                return "Team "+(i+1);
            }
        });
        boxTeam.setSelectedIndex(player.getId());
        
        boxColor.setModel(new DefaultComboBoxModel<Integer>() {
            int selected;
            @Override
            public void setSelectedItem(Object anItem){
                selected=(int)anItem;
            }

            @Override
            public Object getSelectedItem() {
                return selected;
            }

            @Override
            public int getSize() {
                return Settings.COLORS.length;
            }

            @Override
            public Integer getElementAt(int i) {
                return i;
            }
        });
        boxColor.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                Color color = Settings.COLORS[(int)value];
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setText(" ");

                if (isSelected) {
                    color = color.brighter();
                }
                
                label.setBackground(color);
                label.setForeground(color);
                
                return label;
            }
        });
        boxColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.setColor((int)boxColor.getSelectedItem());
            }
        });
        boxColor.setSelectedItem(player.getColor());
        
        boxPlayerType.setModel(new DefaultComboBoxModel<>(new String[] {"Human", "AI"}));
        boxPlayerType.setSelectedItem(type);
        boxPlayerType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch((String) boxPlayerType.getSelectedItem()){
                    case "Human":
                        player.dispose();
                        player = new PlayerHuman(player.getColorID(), player.getTeam());
                        break;
                    case "AI":
                        player.dispose();
                        player = new PlayerAI(player.getColorID(), player.getTeam());
                        break;
                }
            }
        });

        BtnDel.setText("X");
        BtnDel.setMargin(new Insets(0,0,0,0));
        BtnDel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parent.removePlayer(THIS.player.getId());
                player = null;
                parent = null;
            }
        });

        refresh();
        
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BtnDel, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPlayerID)
                .addGap(18, 18, 18)
                .addComponent(boxPlayerType, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(boxColor, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(boxTeam, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(BtnDel, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPlayerID)
                        .addComponent(boxPlayerType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(boxColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(boxTeam, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>                                                            

    // Variables declaration - do not modify                     
    private JButton BtnDel;
    private JComboBox boxColor;
    private JComboBox boxPlayerType;
    private JComboBox boxTeam;
    private JLabel lblPlayerID;
    // End of variables declaration                   
}