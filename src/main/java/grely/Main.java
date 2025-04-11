package main.java.grely;

import arc.util.*;
import mindustry.*;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.*;
import java.text.MessageFormat;
import static main.java.grely.func.*;
import static main.java.grely.PEvents.*;
import static main.java.grely.PVars.*;

public class Main extends Plugin{
    @Override
    public void init(){
        initEvents();
        Log.info("Loaded openpvp plugin v@", Vars.mods.getMod("openpvp").meta.version);
        Log.info("Вы можете менять правила, но правила используемые плагином все равно будут назначены им!");
        Timer.schedule(()->{
            if(GameOverWhen != null && GameStartWhen != 0){
                long now = System.currentTimeMillis();
                long elapsedMillis = now - GameStartWhen;
                long totalGameMillis = 80 * 60 * 1000L;
                long remainingMillis = totalGameMillis - elapsedMillis;

                long minutes = remainingMillis / (60 * 1000);
                long seconds = (remainingMillis / 1000) % 60;

                Call.setHudText("[sky]До конца игры осталось: " + minutes + " минут " + seconds + " секунд.");
            }
        }, 0, 1);
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("teams", "Посмотреть занятые команды.", (args, player) -> playerTeams.each(t->player.sendMessage(t.getTeam().coloredName())));
        handler.<Player>register("killme", "Перейти в серую команду.", (args, player) ->{
            if(player.team() != Team.derelict) {
                playerTeams.remove(playerTeams.find(SVOGOYDA -> SVOGOYDA.getTeam() == player.team()));
                player.unit().kill();
                Groups.build.each(b -> {
                    if (b.team == player.team())
                        b.kill()
                });
                player.team(Team.derelict);
            } else {
                player.sendMessage("[scarlet]Вы уже серой в серой команде!");
            }
        });

        /*
        TODO
        handler.<Player>register("request", "<nick or playerID>", "Попроситься к кому-либо в команду.", (args, player) ->{
            try {
                int pid = Integer.parseInt(args[0]); // player id

                Player proverkaSvyazi = Groups.player.find(katTam->katTam.id==pid);

                proverkaSvyazi.sendMessage(MessageFormat.format("[#tan]Игрок {0} [tan]отправил вам запрос на вхождение в вашу команду, пропишите [green]/reqyes[tan], чтобы перенести его в вашу команду, [red]/reqno[tan], чтобы [yellow]проигнорировать[tan] запрос. У вас есть [yellow]минутана одобрение запроса!", player.coloredName()));
                teamReqData.add(new ReqData(proverkaSvyazi, player));
                player.sendMessage("[green]Запрос отправлен!");
            } catch (Exception oblya) {
                // not player id, using nick
            }
        });

        handler.<Player>register("reqyes", "Одобрить запрос на вход.", (args, player) ->{

        });
        */
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.<Player>register("teams", "Посмотреть занятые команды.", (args, player) -> playerTeams.each(t->Log.info(t.getTeam().coloredName())));
        handler.register("click-add", "<uuid>", "добавить игрока в ожидание клика", (args) -> {
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
