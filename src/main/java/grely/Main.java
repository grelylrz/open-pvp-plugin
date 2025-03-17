package main.java.grely;

import arc.Core;
import arc.util.*;
import mindustry.*;
import mindustry.mod.*;
import static main.java.grely.PEvents.*;

public class Main extends Plugin{
    @Override
    public void init(){
        initEvents();
        Log.info("Loaded openpvp plugin v@", Vars.mods.getMod("openpvp").meta.version);
    }
}
