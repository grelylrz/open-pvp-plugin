package main.java.grely;

import arc.Events;
import arc.graphics.Color;
import arc.util.Log;
import arc.util.Timer;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.world.Tile;
import mindustry.gen.*;

import static main.java.grely.PVars.*;
import static main.java.grely.func.getCores;
import static main.java.grely.func.getTeam;

public class PEvents {
    public static void initEvents() {
        Log.info("Loading events.");
        Events.on(EventType.BlockBuildEndEvent.class, e -> {
            if(e.tile == null /*How?*/) {
                Log.debug("[BlockBuildEnvEvent]Tile is null");
                return;
            }
            Tile t = e.tile;
            Timer.schedule(() -> {
                Log.debug("Timer!");
                if(e.team != Team.derelict && !e.breaking && e.tile.block() == Blocks.vault /*ТЗ*/){
                    Call.effect(Fx.mine, t.x*8, t.y*8, 1, Color.red);
                    t.setNet(Blocks.coreShard, e.team, 1);
                    // Log.debug("Setted");
                } else {
                    Log.debug("Team is der. | breaking | block not vault");
                }
            }, 1);
        });

        // Это уже конечное подключение после загрузки мира.
        Events.on(EventType.PlayerJoin.class, e -> {
            Player player = e.player;

            player.team(Team.derelict);
            if(!awaitingClick.contains(player))
                awaitingClick.add(player);
            // Кому лень читать - тут пишу о том как войти в игру.
            player.sendMessage("[tan]Вы зашли на сервер с режимом OpenPvP, для продолжения, нажмите на любой тайл, на нем появится ваше ядро. Если при нажатии этого не случилось, попробуйте кликнуть еще раз или перезайти на сервер, в случае, если это не поможет, пожалуйста, обратитесь в наш [blue]Discord[tan] сервер.");
        });

        Events.on(EventType.PlayerLeave.class, e -> {
            Player player = e.player;
            if(awaitingClick.contains(player))
                awaitingClick.remove(player);
            if(player.team() != Team.derelict) {
                Groups.build.each(b->{
                    if(b.team == player.team())
                        b.kill();
                });
            }
        });

        Events.on(EventType.TapEvent.class, e -> {
            if(e.tile == null) {
                Log.debug("[TapEvent]Tile is null!");
                return;
            }
            Player player = e.player;
            Tile t = e.tile;
            if(!awaitingClick.contains(player))
                return;
            Building core = getCores().find(b -> {
                int bx = (int) (b.x / 8);
                int by = (int) (b.y / 8);
                int dx = bx - t.x;
                int dy = by - t.y;
                return dx * dx + dy * dy <= 40 * 40;
            });
            if(core == null) {
                Team newTeam = getTeam();
                Call.effect(Fx.tapBlock, t.x*8, t.y*8, 1, Color.white);
                t.setNet(Blocks.coreShard, newTeam, 1);
                player.team(newTeam);
                player.sendMessage("[green]С этого момента вы являетесь участником команды " + newTeam.coloredName());
                if(awaitingClick.contains(player))
                    awaitingClick.remove(player);
            } else {
                player.sendMessage("[scarlet]Слишком близко к ядру команды " + core.team.coloredName());
            }
        });
        Events.run(EventType.Trigger.update, () -> {
            Groups.player.each(p->{
                if(p.team().core() == null) {
                    if(playerTeams.contains(p.team()))
                        playerTeams.remove(p.team());
                    p.team(Team.derelict);
                    p.sendMessage("[scalret]Вы проиграли!");
                }
            });
        });
    }
}
