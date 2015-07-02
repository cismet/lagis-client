/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.panels;
import javafx.application.Platform;

import javafx.embed.swing.JFXPanel;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.cismet.tools.gui.FXWebViewPanel;
/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class Test {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private static void initAndShowGUI() {
        // This method is invoked on the EDT thread
        final JFrame frame = new JFrame("Swing and JavaFX");
        System.out.println("before");
        final JFXPanel fxPanel = new JFXPanel();
        System.out.println("after");
//        final FXWebViewPanel fxPanel = new FXWebViewPanel();
        frame.add(fxPanel);
        frame.setSize(300, 200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    initFX(fxPanel);
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fxPanel  DOCUMENT ME!
     */
    private static void initFX(final JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        final Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Scene createScene() {
        final Group root = new Group();
        final Scene scene = new Scene(root, Color.ALICEBLUE);
        final Text text = new Text();

        text.setX(40);
        text.setY(100);
        text.setFont(new Font(25));
        text.setText("Welcome JavaFX!");

        root.getChildren().add(text);

        return (scene);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    initAndShowGUI();
                }
            });
    }
}
