package main.java.grely;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import java.util.Random;
import static main.java.grely.PVars.*;

public class func {
    public static Seq<Building> getCores() {
        Seq<Building> ret = new Seq<>();
        Groups.build.each(b -> {
            if(b.block == Blocks.coreShard || b.block == Blocks.coreAcropolis || b.block == Blocks.coreBastion || b.block == Blocks.coreCitadel || b.block == Blocks.coreFoundation || b.block == Blocks.coreNucleus)
                ret.add(b);
        });
        return ret;
    }
    public static Team getTeam() {
        Random rand = new Random();
        int te = rand.nextInt(255) + 1;
        Team team = Team.all[te];
        while (playerTeams.contains(team)) {
            te = rand.nextInt(255) + 1;
            team = Team.all[te];
        }
        return team;
    }
}
