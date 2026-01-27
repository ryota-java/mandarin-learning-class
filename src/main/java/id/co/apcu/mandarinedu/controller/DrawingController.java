package id.co.apcu.mandarinedu.controller;

import id.co.apcu.mandarinedu.model.ClassroomState;
import id.co.apcu.mandarinedu.repository.ClassroomStateRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DrawingController {

    private final ClassroomStateRepository stateRepository;

    public DrawingController(ClassroomStateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    //stateless board
    @MessageMapping("/draw/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public String handleDrawing(@DestinationVariable String roomId, String payload) {
        return payload;
    }

    //stateful pdf
    @MessageMapping("/state/{roomId}/update")
    @SendTo("/topic/state/{roomId}")
    public ClassroomState updateStates(@DestinationVariable String roomId, ClassroomState newState){
        newState.setRoomId(roomId);
        return stateRepository.save(newState);
    }

    //auto fetch initial state
    @MessageMapping("/state/{roomId}/get")
    @SendTo("/topic/state/{roomId}")
    public ClassroomState getState(@DestinationVariable String roomId){
        return stateRepository.findById(roomId).orElseGet(()-> {
            ClassroomState defaultState = new ClassroomState();
            defaultState.setRoomId(roomId);
            return defaultState;
        });
    }
}

