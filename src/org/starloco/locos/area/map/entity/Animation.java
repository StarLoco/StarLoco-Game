package org.starloco.locos.area.map.entity;

public class Animation {

    private int id;
    private int animationId;
    private String animationName;
    private int animationArea;
    private int animationAction;
    private int animationSize;

    public Animation(int id, int animId, String name, int area, int action, int size) {
        this.id = id;
        this.animationId = animId;
        this.animationName = name;
        this.animationArea = area;
        this.animationAction = action;
        this.animationSize = size;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return animationName;
    }

    public int getArea() {
        return animationArea;
    }

    public int getAction() {
        return animationAction;
    }

    public int getSize() {
        return animationSize;
    }

    private int getAnimationId() {
        return animationId;
    }

    public String prepareToGA() {
        return String.valueOf(this.getAnimationId()) + "," + this.getArea() + "," + this.getAction() + "," + this.getSize();
    }
}