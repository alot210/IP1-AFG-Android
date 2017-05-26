package de.hsd.manguli.fractalsapp;

/**
 * Created by Thomas on 26.05.2017.
 */

public class Mandelbrot extends Algorithm {

    //Konstruktor
    public Mandelbrot(int width, int heigth, int maxIteration, Complex translate, Complex scale) {

        super.setWidth(width);
        super.setHeigth(heigth);
        super.setMaxIteration(maxIteration);
        //Nullpunkt der Mandelbrotmenge
        super.translate = translate;
        super.scale = scale;
    }
}
