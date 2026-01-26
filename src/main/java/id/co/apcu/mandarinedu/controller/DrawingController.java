package id.co.apcu.mandarinedu.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DrawingController {

    @MessageMapping("/draw/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public String handleDrawing(@DestinationVariable String roomId, String payload){
        return payload;
    }
}
