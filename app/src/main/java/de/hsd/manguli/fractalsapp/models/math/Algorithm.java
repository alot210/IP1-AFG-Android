package de.hsd.manguli.fractalsapp.models.math;


import android.graphics.Color;

/**
 * Oberklasse mit Algorithmus fuer Julia und Mandelbrot
 */
public class Algorithm {

    //Default Konstruktor
    public Algorithm() {
    }

    //Fenstergröße
    private int width;
    private int height;
    //Default-Werte
    private int maxIteration = 150;
    private int color1 = Color.YELLOW, color2 = Color.CYAN, color3 = Color.MAGENTA, color4 = Color.BLUE;

    public int getMaxIteration() {
        return this.maxIteration;
    }

    public void setWidth(int _width) {
        this.width = _width;
    }

    public void setHeigth(int _height) {
        this.height = _height;
    }

    public void setMaxIteration(int _m) {
        this.maxIteration = _m;
    }

    public int getColor1() {
        return color1;
    }

    public void setColor1(int color1) {
        this.color1 = color1;
    }

    public int getColor2() {
        return color2;
    }

    public void setColor2(int color2) {
        this.color2 = color2;
    }

    public int getColor3() {
        return color3;
    }

    public void setColor3(int color3) {
        this.color3 = color3;
    }

    public int getColor4() {
        return color4;
    }

    public void setColor4(int color4) {
        this.color4 = color4;
    }

    //Mittelpunkt des Bildausschnitts berechnen:
    //spaeter beim Verschieben werden diese Werte dynamisch geändert
    //Die Mandelbrot-Menge wird im Fenster bei w/2 und (3/4)*h zentriert
    Complex translate;
    Complex scale;

    public void setTranslate(Complex translate) {
        this.translate.add(translate);
    }

    /**
     * Methode wird nur von Julia-Menge überschrieben
     * Methode überprüft für jede übergebene Komplexe Zahl Zugehörigkeit zur Menge
     * @param z0 Konstante aus Folge
     * @return Farbe für Pixel
     */
    public int isElemOfMand(Complex z0){

        Complex z = z0;

        for (int n = 0; n < maxIteration; n++) {   //n Zahl der Iterationen

            if (z.pythagoras() > 4.0) {  //|Zn| > 2    => |Zn| = sqrt(x^2+y^2), dann ist Zahl C ausserhalb der Menge
                //setColor(n)
                int color =  0;
                if (n < maxIteration / 3) {
                    color = interpolate(color2, color3, n);
                } else if (n < maxIteration *2/ 3) {
                    color = interpolate(color3, color4, n);
                } else{
                    color = interpolate(color4, color2, n);
                }
                return color;
            }
            //Berechnung der Folge z' = z*z + z0;
            z = z.mult(z).add(z0);
        }
        return color1;  //|Zn| < 2, Farbe ist schwarz
    }

    /**
     * Methode transformiert übergebenen Punkt (= Pixel auf Screen)
     * ruft isElemOfMand auf und gibt eine Farbe zurück
     * @param i Punkt(i, j) im Koordinatensystem
     * @param j
     * @return Farbe für Pixel
     */
    public int setColor(int i, int j) {

        //transalte und scale und mirror: a=i, b=j a' = i*sx * x - tx & b' = j*sy* y - ty

        //Int i, j als Complexe Zahl im Verhältnis zur Skalierung des Screens
        Complex c = new Complex((double) i / this.width, (double) j / this.height);
        //scale
        c = c.scale(scale.getReal(), scale.getImag());

        // mirror and translate
        c = c.translate(-translate.getReal(), -translate.getImag());

        //überprüfen ob Punkt in Menge liegt und dem entsprechend Farbe zurückgeben
        return isElemOfMand(c);
    }

    /**
     * Methode um Pixelarray mit Farbwerten befüllen
     * @param granulation Auflösung der aktuellen Bitmap
     * @return Pixel-Array, speichert Farben für Bitmap
     */
    public int[] fillPixels(int granulation){
        int[] pixels = new int[width*height];
        int loc;
        int c;
        //Farbpixel für jeweilige Auflösung berechnen
        for (int i = 0; i < width - granulation; i += granulation) {
            for (int j = 0; j < height - granulation; j += granulation) {
                //Koordinaten von 2D-Array zu 1D-Array umrechnen
                loc = i + j * width;
                //Farbe für Pixelblock berechnen
                c = setColor(i, j);
                pixels[loc] = c;
                int n = granulation * granulation - 1;

                //Pixelblock in jeweiliger Granulation ins Array schreiben
                while (n > 0) {
                    if (n % granulation == 0) {
                        loc = loc + width;
                        pixels[loc] = c;
                    }
                    pixels[loc + (n % granulation)] = c;
                    n--;
                }
            }
        }
        return pixels;
    }

    /**
     * Methode um zwischen Farben interpolieren
     * @param v1 erste Farbe
     * @param v2 zweite Farbe
     * @param i index von Farb-Array
     * @return interpolierte Farbe
     */
    public int interpolate(int v1, int v2, int i) {
        int r = Math.abs(Color.red(v1) + (i%(maxIteration/3)) * (Color.red(v2) - Color.red(v1)) / (maxIteration/3));
        int g = Math.abs(Color.green(v1) + (i%(maxIteration/3)) * (Color.green(v2) - Color.green(v1)) / (maxIteration/3));
        int b = Math.abs(Color.blue(v1) + (i%(maxIteration/3)) * (Color.blue(v2) - Color.blue(v1)) / (maxIteration/3));
        return Color.rgb(r, g, b);
    }
}


