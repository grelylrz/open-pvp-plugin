package main.java.grely;

import arc.util.*;
import mindustry.*;
import mindustry.game.Rules;
import mindustry.mod.*;
import static main.java.grely.PEvents.*;

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
}
