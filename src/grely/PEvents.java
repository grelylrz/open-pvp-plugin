package grely;

import arc.Events;
import arc.graphics.Color;
import arc.util.Log;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.world.Tile;

public class PEvents {
    public static void initEvents() {
        Events.on(EventType.BlockBuildEndEvent.class, e -> {
            if(e.tile == null /*How?*/) {
                Log.debug("Tile is null");
                return;
            }
            Tile t = e.tile;
            if(e.team != Team.derelict && !e.breaking && e.tile.block() == Blocks.vault /*ТЗ*/){
                Call.effect(Fx.blockCrash, t.x*8, t.y*8, 1, Color.white);
                t.setNet(Blocks.coreShard, e.team, 1);
            }
        });
    }
}
