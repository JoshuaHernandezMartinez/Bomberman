package com.mygdx.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.mygdx.components.AnimationComponent;
import com.mygdx.components.BombComponent;
import com.mygdx.components.CellComponent;
import com.mygdx.components.CreepComponent;
import com.mygdx.components.IdleComponent;
import com.mygdx.components.InputComponent;
import com.mygdx.components.OnlinePlayerComponent;
import com.mygdx.components.PhysicComponent;
import com.mygdx.components.PlayerComponent;
import com.mygdx.components.PowerUpComponent;
import com.mygdx.components.RegionComponent;
import com.mygdx.components.SpeedComponent;
import com.mygdx.components.StateComponent;
import com.mygdx.components.TimerComponent;

public class Mappers {
    public static final ComponentMapper<RegionComponent> region = ComponentMapper.getFor(RegionComponent.class);
    public static final ComponentMapper<AnimationComponent> animation = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<StateComponent> state = ComponentMapper.getFor(StateComponent.class);
    public static final ComponentMapper<PhysicComponent> physic = ComponentMapper.getFor(PhysicComponent.class);
    public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<CellComponent> cell = ComponentMapper.getFor(CellComponent.class);
    public static final ComponentMapper<SpeedComponent> speed = ComponentMapper.getFor(SpeedComponent.class);
    public static final ComponentMapper<IdleComponent> idle = ComponentMapper.getFor(IdleComponent.class);
    public static final ComponentMapper<TimerComponent> timer = ComponentMapper.getFor(TimerComponent.class);
    public static final ComponentMapper<BombComponent> bomb = ComponentMapper.getFor(BombComponent.class);
    public static final ComponentMapper<PowerUpComponent> power_up = ComponentMapper.getFor(PowerUpComponent.class);
    public static final ComponentMapper<CreepComponent> creep = ComponentMapper.getFor(CreepComponent.class);
    public static final ComponentMapper<InputComponent> input = ComponentMapper.getFor(InputComponent.class);
    public static final ComponentMapper<OnlinePlayerComponent> online = ComponentMapper.getFor(OnlinePlayerComponent.class);

}
