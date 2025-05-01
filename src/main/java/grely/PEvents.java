package main.java.grely;

import arc.Events;
import arc.graphics.Color;
import arc.util.Log;
import arc.util.Threads;
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
    public static int coreProtectRad = 160;
    public static void initEvents() {
        Log.info("Loading events.");
        Events.on(EventType.BlockBuildEndEvent.class, e -> {
            if(e.tile == null /*How?*/ || e.tile.build == null) {
                Log.debug("[BlockBuildEnvEvent]Tile/build is null");
                return;
            }
            Tile t = e.tile;
            Timer.schedule(() -> {
                Log.debug("Timer!");
                if(e.tile == null /*How?*/ || e.tile.build == null) {
                    Log.debug("[BlockBuildEnvEvent]Tile/build is null");
                    return;
                }
                if(e.team != Team.derelict && !e.breaking && e.tile.block() == Blocks.vault /*ТЗ*/){
                    Building core = getCores().find(b -> {
                        if(b.team != e.team) {
                            int bx = (int) (b.x / 8);
                            int by = (int) (b.y / 8);
                            int dx = bx - t.x;
                            int dy = by - t.y;
                            return dx * dx + dy * dy <= coreProtectRad * coreProtectRad;
                        } else {
                            return false;
                        }
                    });
                    if(core == null) {
                        Call.effect(Fx.mine, t.x * 8, t.y * 8, 1, Color.red);
                        t.setNet(Blocks.coreShard, e.team, 1);
                        // Log.debug("Setted");
                    } else {
                        Call.label("[scarlet]Рядом ядро команды " + core.team.coloredName(), 1f, t.x*8, t.y*8);
                    }
                } else {
                    Log.debug("[BlockBuildEnvEvent]Team is der. | breaking | block not vault");
                }
            }, 1.5f);
        });

        // Это уже конечное подключение после загрузки мира.
        Events.on(EventType.PlayerJoin.class, e -> {
            Player player = e.player;
            leftPlayerData d = leftDatas.find(p->p.getUuid().equals(player.uuid()));
            if(d != null) {
                player.sendMessage("[green]Похоже, вы уже учавствовали в игре, ваша команда будет восстановлена. " + d.getTeam().name);
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
            if(player.team() != Team.derelict) {
                leftPlayerData huy = new leftPlayerData(player, player.team());
                huy.setUuid(player.uuid());
                leftDatas.add(huy);
                Timer.schedule(() -> {
                    if (player.team() != Team.derelict && Groups.player.find(p -> p.uuid().equals(player.uuid())) == null) {
                        Groups.build.each(b -> {
                            if (b.team == player.team())
                                b.kill();
                        });
                        TeamDat fdat = playerTeams.find(SVOGOYDA -> SVOGOYDA.getTeam() == player.team());
                        if (fdat != null) {
                            if(Groups.player.size() < 2)
                                gameStarted = false;
                            playerTeams.remove(new TeamDat(player, player.team()));
                            playerTeams.remove(fdat);
                        }
                        if (leftDatas.contains(huy))
                            leftDatas.remove(huy);
                    }
                }, 300);
            }
        });

        Events.on(EventType.TapEvent.class, e -> {
            if(e.tile == null) {
                Log.debug("[TapEvent]Tile is null!");
                return;
            }
            if(!awaitingClick.contains(e.player))
                return;
                Log.debug("Thread started!");
                Player player = e.player;
                Tile t = e.tile;
                if(t.block() != Blocks.air && t.build != null) {
                    player.sendMessage("[scarlet]На этом месте расположен [white]" + t.block().emoji());
                    joinRequest req = joinRequests.find(re->re.getRequester()==player);
                    if(req == null) {
                        player.sendMessage("Если вы хотите вступить в команду нажмите на любое из их ядер еще раз.");
                        joinRequests.add(new joinRequest(player, t.build.team()));
                        return;
                    } else {
                        if(req.getTeam()!=t.build.team()) {
                            joinRequests.remove(req);
                            player.sendMessage("[stat]Если вы хотите вступить в команду нажмите на любой их блок еще раз.");
                            joinRequests.add(new joinRequest(player, t.build.team()));
                            return;
                        } else if(req.getCount()==1) {
                            req.increaseCount();
                            TeamDat dat = playerTeams.find(pt->pt.getTeam()==t.build.team());
                            dat.getOwner().sendMessage("К вам поступил запрос от "+player.coloredName()+" []на вступление в вашу команду! Пропишите /yes #player-id для одобрения!");
                            player.sendMessage("[green]Запрос отправлен!");
                            return;
                        } else if(req.getCount()==2) {
                            player.sendMessage("[scarlet]Запрос уже отправлен!");
                            return;
                        } else {
                            player.sendMessage("[scarlet]Что то не так, если вы кликнули два раза, то ожидайте одобрения запроса.");
                            return;
                        }
                    }
                }
                Building core = getCores().find(b -> {
                    int bx = (int) (b.x / 8);
                    int by = (int) (b.y / 8);
                    int dx = bx - t.x;
                    int dy = by - t.y;
                    return dx * dx + dy * dy <= coreProtectRad * coreProtectRad;
                });
                if(core==null)
                    core=getBuild().find(b->{
                        int bx = (int) (b.x / 8);
                        int by = (int) (b.y / 8);
                        int dx = bx - t.x;
                        int dy = by - t.y;
                        return dx * dx + dy * dy <= coreProtectRad-50 * coreProtectRad-50;
                    });
                if(core == null) {
                    Log.debug("Finding free team...");
                    if(playerTeams.size > 255) {
                        player.sendMessage("Извините, все команды заняты...");
                        return;
                    }
                    /*getTeam() using while cycle*/
                    Threads.daemon(()->{
                    Team newTeam = getTeam();
                    Log.debug("Team @ found!", newTeam.name);
                    Call.effect(Fx.tapBlock, t.x*8, t.y*8, 1, Color.white);
                    t.setNet(Blocks.coreNucleus, newTeam, 1);
                    addItems(t.build);
                    player.team(newTeam);
                    player.sendMessage("[green]С этого момента вы являетесь участником команды " + newTeam.coloredName());
                    if(awaitingClick.contains(player))
                        awaitingClick.remove(player);
                    if(playerTeams.find(SVOGOYDA->SVOGOYDA.getTeam()==player.team()) == null)
                        playerTeams.add(new TeamDat(player, newTeam));
                    if(playerTeams.size > 1)
                        gameStarted = true;
                    });
                } else {
                    player.sendMessage("[scarlet]Слишком близко к ядру команды " + core.team.coloredName());
                }
        });
        Events.run(EventType.Trigger.update, () -> {
            Groups.player.each(p -> {
                if (p.team().core() == null && p.team() != Team.derelict) {
                    TeamDat myaah = playerTeams.find(SVOGOYDA -> SVOGOYDA.getTeam() == p.team());
                    if(myaah != null)
                        playerTeams.remove(myaah);
                    Groups.build.each(b -> {
                        if (b.team == p.team())
                            b.kill();
                    });
                    p.team(Team.derelict);
                    p.sendMessage("[scarlet]Вы проиграли!");
                    assert myaah != null;
                    Call.sendMessage("Команда "+myaah.getTeam().coloredName()+" []проиграла!");
                    if (p.unit() != null)
                        p.unit().kill();
                }
                if(p.team().core() != null && p.team() != Team.derelict) {
                    Building core = getCores(p.team()).find(e->e.block()==Blocks.coreNucleus);
                    if(core == null) {
                        TeamDat myaah = playerTeams.find(SVOGOYDA -> SVOGOYDA.getTeam() == p.team());
                        if(myaah != null)
                            playerTeams.remove(myaah);
                        Groups.build.each(b -> {
                            if (b.team == p.team())
                                b.kill();
                        });
                        p.team(Team.derelict);
                        p.sendMessage("[scarlet]Вы проиграли!");
                        assert myaah != null;
                        Call.sendMessage("Команда "+myaah.getTeam().coloredName()+" []проиграла!");
                        if (p.unit() != null)
                            p.unit().kill();
                    }
                }
            });
            if (playerTeams.size < 2 && gameStarted) {
                Call.sendMessage(playerTeams.find(eb -> eb.getTeam().cores() != null).getTeam().coloredName() + " [green]wins!");
                Events.fire(new EventType.GameOverEvent(playerTeams.find(eb -> eb.getTeam().cores() != null).getTeam()));
                gameStarted = false;
            }
        });
        Events.on(EventType.WorldLoadEvent.class, e -> Timer.schedule(()->{
            Rules rules = Vars.state.rules.copy();
            if(rules.pvp)
                Log.info("Вы можете не ставить режим пвп вручную!");
            rules.canGameOver = false;
            rules.modeName = "OpenPvP";
            rules.enemyCoreBuildRadius = 65*8;
            rules.unitCapVariable = true; // Whether cores add to unit limit
            rules.unitCap = 32;
            rules.waves = false;
            rules.bannedBlocks.add(Blocks.coreCitadel);
            rules.bannedBlocks.add(Blocks.coreBastion);
            rules.bannedBlocks.add(Blocks.coreAcropolis);
            rules.pvpAutoPause = false;
            rules.pvp = true;
            rules.infiniteResources = false;
            rules.possessionAllowed = true;
            rules.unitBuildSpeedMultiplier = 0.33f;
            Vars.state.rules = rules.copy();
            Call.setRules(Vars.state.rules);
            clearData();

            Team.sharded.cores().each(Building::kill);
            Team.derelict.cores().each(Building::kill);
            for(Player p : Groups.player) {
                if(awaitingClick.contains(p))
                    awaitingClick.remove(p);
                awaitingClick.add(p);
            }
            if(GameOverWhen != null)
                GameOverWhen.cancel();
            GameStartWhen = System.currentTimeMillis();
            GameOverWhen = Timer.schedule(()->{
                Call.sendMessage("[scarlet]Игра окончена!");
                displayCores();
                Events.fire(new EventType.GameOverEvent(Team.derelict));
            }, 180*60);
        }, 3));
    }
}
