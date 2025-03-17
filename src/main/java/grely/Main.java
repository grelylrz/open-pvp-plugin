package main.java.grely;

import arc.util.*;
import mindustry.*;
import mindustry.mod.*;
import static main.java.grely.PEvents.*;

public class Main extends Plugin{
    @Override
    public void init(){
        initEvents();
        Vars.state.rules.modeName = "OpenPvP";
        Log.info("Loaded openpvp plugin v@", Vars.mods.getMod("openpvp").meta.version);
    }
}
