package com.sun.spring.f.test;

/**
 *
 * @author Sunil
 */
public class Dog implements Animal {

    private Color color;
    private String voice;

    public void voice() {
        System.out.println(voice);
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public void color() {
        System.out.println(color.getColor());
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
}
