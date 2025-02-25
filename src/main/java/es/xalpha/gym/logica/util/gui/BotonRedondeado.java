package es.xalpha.gym.logica.util.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotonRedondeado extends JButton {

    private boolean over;
    private Color color;
    private Color colorOver;
    private Color colorClick;
    private Color colorBorder;

    public BotonRedondeado() {
        setColor(Color.white);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        colorOver = getColor();
        colorClick = getColor();
        colorBorder = getColor();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(colorClick);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (over) {
                    setBackground(colorOver);
                } else {
                    setBackground(color);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(colorOver);
                over = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(color);
                over = false;
            }
        });
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColorOver() {
        return colorOver;
    }

    public void setColorOver(Color colorOver) {
        this.colorOver = colorOver;
    }

    public Color getColorClick() {
        return colorClick;
    }

    public void setColorClick(Color colorClick) {
        this.colorClick = colorClick;
    }

    public Color getColorBorder() {
        return colorBorder;
    }

    public void setColorBorder(Color colorBorder) {
        this.colorBorder = colorBorder;
    }
}
