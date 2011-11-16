/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.checkbox;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class IconCheckBox extends JPanel {

    //~ Instance fields --------------------------------------------------------

    private final JCheckBox checkBox;
    private final JLabel iconLabel;
    private final JLabel textLabel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new IconCheckBox object.
     */
    public IconCheckBox() {
        this.checkBox = new JCheckBox();
        this.iconLabel = new JLabel();
        this.textLabel = new JLabel();

        super.setLayout(new FlowLayout(FlowLayout.LEFT));
        super.add(this.checkBox);
        super.add(this.iconLabel);
        super.add(this.textLabel);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   icon  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public void setIcon(final Icon icon) {
        if (icon == null) {
            throw new NullPointerException();
        }

        this.iconLabel.setIcon(icon);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Icon getIcon() {
        if (this.iconLabel == null) {
            return null;
        }

        return this.iconLabel.getIcon();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  text  DOCUMENT ME!
     */
    public void setText(final String text) {
        this.textLabel.setText(text);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getText() {
        if (this.textLabel == null) {
            return null;
        }

        return this.textLabel.getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selected  DOCUMENT ME!
     */
    public void setSelected(final boolean selected) {
        this.checkBox.setSelected(selected);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSelected() {
        return this.checkBox.isSelected();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.checkBox.setEnabled(enabled);
        this.iconLabel.setEnabled(enabled);
        this.textLabel.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return this.checkBox.isEnabled();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addActionListener(final ActionListener listener) {
        this.checkBox.addActionListener(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final IconCheckBox box = new IconCheckBox();
        final ImageIcon icon = new ImageIcon(IconCheckBox.class.getResource(
                    "/de/cismet/lagis/ressource/icons/titlebar/flurstueck16.png"));
        box.setIcon(icon);
        box.setText("TEST");
        box.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    JOptionPane.showMessageDialog(box, "Hi");
                }
            });

        final JFrame frame = new JFrame();
        frame.add(box);

        frame.setSize(100, 100);
        frame.setVisible(true);
    }
}
