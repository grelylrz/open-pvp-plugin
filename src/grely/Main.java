package grely;

import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.net.Administration.*;
import mindustry.world.blocks.storage.*;
import arc.Events;

public class Main extends Plugin{
    @Override
    public void init(){
        PEvents.initEvents();
    }
}
