package com.glitchcog.fontificator.emoji.loader.frankerfacez;

import java.util.Map;

/**
 * FrankerFaceZ Emote object returned by the FrankerFaceZ API
 */
public class FfzEmote
{
    private Object css;

    private int height;

    private boolean hidden;

    private int id;

    private Object margins;

    private String name;

    private Owner owner;

    private boolean _public;

    private Map<String, String> urls;

    private int width;

    public Object getCss()
    {
        return css;
    }

    public void setCss(Object css)
    {
        this.css = css;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Object getMargins()
    {
        return margins;
    }

    public void setMargins(Object margins)
    {
        this.margins = margins;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Owner getOwner()
    {
        return owner;
    }

    public void setOwner(Owner owner)
    {
        this.owner = owner;
    }

    public boolean isPublic()
    {
        return _public;
    }

    public void setPublic(boolean _public)
    {
        this._public = _public;
    }

    public Map<String, String> getUrls()
    {
        return urls;
    }

    public void setUrls(Map<String, String> urls)
    {
        this.urls = urls;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

}