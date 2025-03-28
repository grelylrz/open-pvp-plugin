package main.java.grely;

import arc.util.*;
import mindustry.*;
import mindustry.game.Rules;
import mindustry.gen.Player;
import mindustry.mod.*;
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

        handler.<Player>register("req", "Попроситься к кому-либо в команду.", (args, player) ->{
            player.sendMessage("В процессе разработки."); // TODO
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.<Player>register("teams", "Посмотреть занятые команды.", (args, player) -> playerTeams.each(t->Log.info(t.getTeam().coloredName())));
    }
}
