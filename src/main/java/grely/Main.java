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
        Rules rules = new Rules();
        rules.canGameOver = false;
        rules.modeName = "OpenPvP";
        Vars.state.rules = rules.copy();
        Log.info("Loaded openpvp plugin v@", Vars.mods.getMod("openpvp").meta.version);
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("teams", "Посмотреть занятые команды.", (args, player) -> playerTeams.each(t->player.sendMessage(t.coloredName())));
    }
}
