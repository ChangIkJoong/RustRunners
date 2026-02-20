package main.controller.inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import main.controller.facades.IGameActions;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private final IGameActions actions;

    public MouseInputs(IGameActions actions) {
        this.actions = actions;
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        actions.mouseMoved(e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        actions.mousePressed(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        actions.mouseReleased(e.getX(), e.getY());
    }

}
