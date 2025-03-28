package main.java.grely;

import arc.Events;
import arc.graphics.Color;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.world.Tile;
import mindustry.gen.*;

import static main.java.grely.PVars.*;
import static main.java.grely.func.*;

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
            leftPlayerData d = leftDatas.find(p->p.getOld().uuid().equals(player.uuid()));
            if(d != null) {
                player.team(d.getTeam());
                leftDatas.remove(d);
                return;
            }
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
            leftPlayerData huy = new leftPlayerData(player, player.team());
            leftDatas.add(huy);
            Timer.schedule(()->{
                if(player.team() != Team.derelict && Groups.player.find(p->p.uuid().equals(player.uuid())) == null) {
                    Groups.build.each(b->{
                        if(b.team == player.team())
                            b.kill();
                    });
                    if(playerTeams.find(SVOGOYDA->SVOGOYDA.getTeam()==player.team()) != null)
                        playerTeams.remove(new TeamDat(player, player.team()));
                    if(leftDatas.contains(huy))
                        leftDatas.remove(huy);
                }
            }, 300);
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
            if(t.block() != Blocks.air) {
                player.sendMessage("[scarlet]На этом месте расположен " + t.block().emoji());
                return;
            }
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
                if(playerTeams.find(SVOGOYDA->SVOGOYDA.getTeam()==player.team()) == null)
                    playerTeams.add(new TeamDat(player, newTeam));
            } else {
                player.sendMessage("[scarlet]Слишком близко к ядру команды " + core.team.coloredName());
            }
        });
        Events.run(EventType.Trigger.update, () -> {
            Groups.player.each(p -> {
                if (p.team().core() == null && p.team() != Team.derelict) {
                    if (playerTeams.contains(new TeamDat(p, p.team())))
                        playerTeams.remove(new TeamDat(p, p.team()));
                    p.team(Team.derelict);
                    p.sendMessage("[scarlet]Вы проиграли!");
                    if (p.unit() != null)
                        p.unit().kill();
                }
            });
            if(playerTeams.size < 2) {
                Call.sendMessage(playerTeams.find(eb->eb.getTeam().cores() != null).getTeam().coloredName() + "[green]wins!");
                Events.fire(new EventType.GameOverEvent(Team.derelict));
            }
        });
        Events.on(EventType.WorldLoadEvent.class, e -> {
            Rules rules = new Rules();
            rules.canGameOver = false;
            rules.modeName = "OpenPvP";
            Vars.state.rules = rules.copy();

            clearData();

            Team.sharded.cores().each(GOOOL->GOOOL.health=0);

            Groups.player.each(zZzOoOvVvSvVVoO->awaitingClick.add(zZzOoOvVvSvVVoO));
        });
    }
}
