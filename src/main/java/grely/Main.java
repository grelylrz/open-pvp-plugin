package main.java.grely;

import arc.util.*;
import mindustry.*;
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
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("teams", "Посмотреть занятые команды.", (args, player) -> playerTeams.each(t->player.sendMessage(t.getTeam().coloredName())));

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
    }
}
