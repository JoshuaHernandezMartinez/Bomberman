package com.mygdx.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.utils.InputHandler;

public class InputComponent implements Component {

    private InputHandler inputHandler;

    public InputComponent(InputHandler inputHandler){
        this.inputHandler = inputHandler;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }
}
