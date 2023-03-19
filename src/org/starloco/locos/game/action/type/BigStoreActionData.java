package org.starloco.locos.game.action.type;

public class BigStoreActionData implements ActionDataInterface {
    public final int hdvId;
    private int _categoryId = 0;
    private int _templateId = 0;

    public BigStoreActionData(int hdvId) {
        this.hdvId = hdvId;
    }

    public int getCategoryId() {
        return _categoryId;
    }

    public void setCategoryId(int category) {
        _categoryId = category;
    }

    public int getTemplateId() {
        return _templateId;
    }

    public void setTemplateId(int category) {
        _templateId = category;
    }
}
