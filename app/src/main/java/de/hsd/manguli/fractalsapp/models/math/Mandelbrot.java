package de.hsd.manguli.fractalsapp.models.math;

import de.hsd.manguli.fractalsapp.models.math.Algorithm;
import de.hsd.manguli.fractalsapp.models.math.Complex;

/**
 * Mandelbrot Klasse
 */

public class Mandelbrot extends Algorithm {


    //Konstruktor
    public Mandelbrot(int width, int heigth, int maxIteration, Complex translate, Complex scale) {

        super.setWidth(width);
        super.setHeigth(heigth);
        super.setMaxIteration((maxIteration/3)*3);
        //Nullpunkt der Mandelbrotmenge
        super.translate = translate;
        super.scale = scale;
    }
}
