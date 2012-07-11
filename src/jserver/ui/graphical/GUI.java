
package jserver.ui.graphical;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Jimmy
 */
public class GUI extends JFrame{

    private JButton exitBtn = new JButton("Exit");

    public GUI(){
        super("Server Controller.");
        this.setIconImage( new ImageIcon("icon.jpg").getImage() );
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setSize( 300, 200 );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.setLayout( new GridLayout(3,3) );
        add( new JLabel("The Graphical interface is currently in construction.") );
        add( exitBtn );
    }
}
