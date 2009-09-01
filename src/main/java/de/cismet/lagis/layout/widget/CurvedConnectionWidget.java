/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.layout.widget;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 * This class provides Bezier Curve interpolated Connection Widgets which can
 * be used in a NetBeans Visual scene.
 *
 * If there are controlpoints defined for this Widget, the paintWidget method will
 * produce a bezier approximated curve of the path defined by the controlpoints.
 *
 * If there are no controlpoints the curve produced will follow an "S" like shape
 * if the option "SETSCURVE_CTRLPTS" is set.
 * The peak of the S curves is then defined by an offset from the centerline.
 * 
 * @author mbrill
 */
public class CurvedConnectionWidget extends ConnectionWidget {

    /**
     * With this Option, a connection line without controlpoints will be
     * drawn as an "s" like shape. Therefore the algorithm inserts 2 additional
     * controlpoints.
     */
    public static final int SET_SCURVE_CTRLPTS = 0;

    private boolean drawSCurve = false;
    private int sCurveCoordinateOffset = 8;

    public CurvedConnectionWidget(Scene scene) {
        super(scene);

        paintWidget();
    }

    public CurvedConnectionWidget(Scene scene, int option) {
        super(scene);

        switch (option) {
            case SET_SCURVE_CTRLPTS:
                drawSCurve = true;
        }

        paintWidget();
    }

    /**
     * This method overrides paintWidget from ConnectionWidget.
     * Instead of drawing straigt lines
     */
    @Override
    protected void paintWidget() {

        Anchor source = getSourceAnchor();
        Anchor target = getTargetAnchor();

        List<Point> controlPoints = this.getControlPoints();
        int noOfControlPoints = controlPoints.size();

        if (noOfControlPoints > 1) {
            Point prev = controlPoints.get(0);
            Point current = controlPoints.get(1);

            if (drawSCurve && noOfControlPoints == 2) {
                ArrayList<Point> sCurveControl = new ArrayList();

                Point ctrl1;
                Point ctrl2;
                Point ctrl3;

                if (prev.x > current.x) {
                    ctrl2 = new Point(((prev.x + current.x) / 2) + sCurveCoordinateOffset,
                            ((prev.y + current.y) / 2) + sCurveCoordinateOffset);

                    ctrl1 = new Point((prev.x + ctrl2.x) / 2,
                            (prev.y + ctrl2.y) / 2);

                    ctrl3 = new Point(((current.x + ctrl2.x) / 2) - sCurveCoordinateOffset,
                            ((current.y + ctrl2.y) / 2) - sCurveCoordinateOffset);


                    sCurveControl.add(prev);
                    sCurveControl.add(ctrl1);
                    sCurveControl.add(ctrl2);
                    sCurveControl.add(ctrl3);
                    sCurveControl.add(current);

                    setControlPoints(sCurveControl, false);

                } else if (prev.x < current.x) {

                    ctrl2 = new Point(((prev.x + current.x) / 2) - sCurveCoordinateOffset,
                            ((prev.y + current.y) / 2) + sCurveCoordinateOffset);

                    ctrl1 = new Point((prev.x + ctrl2.x) / 2,
                            (prev.y + ctrl2.y) / 2);

                    ctrl3 = new Point(((current.x + ctrl2.x) / 2) + sCurveCoordinateOffset,
                            ((current.y + ctrl2.y) / 2) - sCurveCoordinateOffset);

                    sCurveControl.add(prev);
                    sCurveControl.add(ctrl1);
                    sCurveControl.add(ctrl2);
                    sCurveControl.add(ctrl3);
                    sCurveControl.add(current);

                    setControlPoints(sCurveControl, false);
                }
            }

            Point mid = new Point((current.x + prev.x) / 2,
                    (current.y + prev.y) / 2);

            GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO);
            gp.moveTo(prev.x, prev.y);


            if (noOfControlPoints > 2) {

                for (int i = 2; i < noOfControlPoints; i++) {
                    float x1 = (mid.x + current.x) / 2;
                    float y1 = (mid.y + current.y) / 2;

                    prev = current;

                    current = controlPoints.get(i);

                    mid = new Point((current.x + prev.x) / 2,
                            (current.y + prev.y) / 2);
                    float x2 = ((prev.x + mid.x) / 2);
                    float y2 = ((prev.y + mid.y) / 2);

                    gp.curveTo(x1, y1, x2, y2, mid.x, mid.y);
                }
            } else {

                float x1 = (mid.x + current.x) / 2;
                float y1 = (mid.y + current.y) / 2;

                float x2 = ((prev.x + mid.x) / 2);
                float y2 = ((prev.y + mid.y) / 2);

                gp.lineTo(current.x, current.y);

            }

            prev = controlPoints.get(noOfControlPoints - 1);
            current = getLastControlPoint();
            mid = new Point((current.x + prev.x) / 2, (current.y + prev.y) / 2);

            float x1 = (mid.x + current.x) / 2;
            float y1 = (mid.y + current.y) / 2;

            float x2 = ((prev.x + mid.x) / 2);
            float y2 = ((prev.y + mid.y) / 2);


            gp.curveTo(x1, y1, x2, y2, mid.x, mid.y);

            Graphics2D g2d = getScene().getGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Color oldColor = g2d.getColor();
            Stroke oldStroke = g2d.getStroke();

            g2d.setColor(this.getForeground());
            g2d.setStroke(this.getStroke());

            g2d.draw(gp);

            // align and draw the source Anchor shape. See org.netbeans.api.visual.widget.ConnectionWidget;
            // TODO source Anchor
            // align and draw the target Anchor shape. See org.netbeans.api.visual.widget.ConnectionWidget;

            Point lastControlPoint = getLastControlPoint();
            boolean isTargetCutDistance = getTargetAnchorShape().
                    getCutDistance() != 0.0;
            double lastControlPointRotation =
                    lastControlPoint != null && (getTargetAnchorShape().
                    isLineOriented() || isTargetCutDistance) ? getTargetAnchorShapeRotation() : 0.0;

            if (lastControlPoint != null) {
                AffineTransform previousTransform = g2d.getTransform();
                g2d.translate(lastControlPoint.x, lastControlPoint.y);
                if (getTargetAnchorShape().isLineOriented()) {
                    g2d.rotate(lastControlPointRotation);
                }
                getTargetAnchorShape().paint(g2d, false);
                g2d.setTransform(previousTransform);
            }

            g2d.setColor(oldColor);
            g2d.setStroke(oldStroke);

        }
    }
}
