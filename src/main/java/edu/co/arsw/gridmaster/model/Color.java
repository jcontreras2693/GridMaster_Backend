package edu.co.arsw.gridmaster.model;

import edu.co.arsw.gridmaster.model.exceptions.NoMoreColorsException;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Color {

    @JsonProperty
    private ConcurrentLinkedQueue<int[]> colors;

    public Color() {
        colors = new ConcurrentLinkedQueue<>();
        colors.add(new int[]{255, 0, 0});   // Rojo
        colors.add(new int[]{0, 255, 0});   // Verde
        colors.add(new int[]{0, 0, 255});   // Azul
        colors.add(new int[]{255, 255, 0}); // Amarillo
        colors.add(new int[]{0, 255, 255}); // Cian
        colors.add(new int[]{255, 0, 255}); // Magenta
        colors.add(new int[]{192, 192, 192}); // Gris claro
        colors.add(new int[]{128, 128, 128}); // Gris oscuro
        colors.add(new int[]{255, 165, 0}); // Naranja
        colors.add(new int[]{75, 0, 130});   // Índigo
        colors.add(new int[]{255, 20, 147}); // Deep Pink
        colors.add(new int[]{0, 128, 0});    // Verde oscuro
        colors.add(new int[]{128, 0, 128});  // Púrpura
        colors.add(new int[]{0, 0, 128});    // Azul marino
        colors.add(new int[]{128, 128, 0});  // Verde oliva
        colors.add(new int[]{255, 255, 255}); // Blanco
        colors.add(new int[]{0, 0, 0});      // Negro
        colors.add(new int[]{240, 248, 255}); // Alice Blue
        colors.add(new int[]{255, 192, 203}); // Rosa
        colors.add(new int[]{255, 105, 180}); // Hot Pink
    }

    @JsonGetter("color")
    public int[] getColor() throws NoMoreColorsException {
        if (colors.isEmpty()) {
            // Handle empty state more gracefully
            return new int[]{0, 0, 0};  // Default color (black) if no colors are left
        }
        return colors.poll();
    }

    @JsonSetter("color")
    public void setColor(int[] color) {
        colors.add(color);
    }
}
