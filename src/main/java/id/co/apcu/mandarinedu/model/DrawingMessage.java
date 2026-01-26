package id.co.apcu.mandarinedu.model;

import lombok.Data;

@Data
public class DrawingMessage {
    private String type;
    private double x;
    private double y;
    private String color;
    private String sender;
}

