package se.iths.javatwentytwo.labthree.labthree.model.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rectangle extends Shape {

    public Rectangle(Point point, Color color, double size) {
        super.setPoint(point);
        super.setColor(color);
        super.setSize(size);
    }

    @Override
    public String svgFormat() {
        return "<rect x=\"" + centerPoint().getPosX() + "\" y=\"" + centerPoint().getPosY() + "\" width=\"" + getSize() +
                "\" height=\"" + getSize() + "\" fill=\"#" + getColor().toString().substring(2) + "\" />";
    }

    @Override
    public boolean pointInsideShape(Point point) {
        boolean xInside = point.getPosX() >= centerPoint().getPosX() && point.getPosX() <= centerPoint().getPosX() + getSize();
        boolean yInside = point.getPosY() >= centerPoint().getPosY() && point.getPosY() <= centerPoint().getPosY() + getSize();
        return xInside && yInside;
    }

    @Override
    public void draw(GraphicsContext context) {
        context.setFill(getColor());
        context.fillRect(centerPoint().getPosX(), centerPoint().getPosY(), getSize(), getSize());
    }
}
