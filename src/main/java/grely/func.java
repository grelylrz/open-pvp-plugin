package main.java.grely;

import arc.struct.Seq;
import arc.util.Timer;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import java.util.Random;
import mindustry.gen.*;
import lombok.Getter;
import lombok.Setter;
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
        Team finalTeam = team;
        while(playerTeams.find(SVOGOYDA->SVOGOYDA.getTeam()==finalTeam) != null) {
            te = rand.nextInt(255) + 1;
            team = Team.all[te];
        }
        /*while (playerTeams.contains(team)) {
            te = rand.nextInt(255) + 1;
            team = Team.all[te];
        }*/
        return team;
    }

    public static boolean isOwner(Player p, Team t) {
        if(playerTeams.find(zov->zov.getTeam()==t && zov.getOwner()==p) != null)
            return true;
        return false;
    }

    public static void clearData() {
        awaitingClick.clear();
        playerTeams.clear();
        leftDatas.clear();
    }

    @Getter
    @Setter
    public static class leftPlayerData {
        Player old;
        Team team;

        leftPlayerData(Player player, Team team) {
            this.old = player;
            this.team = team;
        }
        // auto-gen.
        Player getOld() {
            return old;
        }
        Team getTeam() {
            return team;
        }

        void setOld(Player newOld) {
            this.old = newOld;
        }
        void setTeam(Team newTeam) {
            this.team = newTeam;
        }
    }
    @Getter
    @Setter
    public static class TeamDat {
        Team team;
        Player owner;
        Seq<Player> players = new Seq<>();

        TeamDat(Player owner, Team team) {
            this.team = team;
            this.owner = owner;
            players.add(owner);
        }
    }

    @Getter
    @Setter
    static class ReqData {
        Team team;
        Player owner;
        Player requester;

        ReqData(Player o, Player r) {
            this.team=o.team();
            this.owner=o;
            this.requester=r;
        }
    }
}
