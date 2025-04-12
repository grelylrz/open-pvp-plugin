package main.java.grely;

import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import java.util.Random;
import mindustry.gen.*;
import lombok.Getter;
import lombok.Setter;
import static mindustry.content.Planets.*;
import mindustry.world.Block;

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
    public static void displayCores() {
        ObjectIntMap<Team> teamCores = new ObjectIntMap<>();

        getCores().each(c -> {
            if (c.team != null) {
                teamCores.get(c.team, 0);
                teamCores.increment(c.team, 1);
            }
        });
        StringBuilder sb = new StringBuilder();
        teamCores.forEach(team -> {
            sb.append(playerTeams.find(z->z.getTeam()==team.key).owner.coloredName().replace("\n", "") + " [stat]имел(а) " + team.value + " ядер!\n");
        });
        Call.infoMessage(sb.toString());
        sb.setLength(0);
    }
    public static Team getTeam() {
        Random rand = new Random();

        do {
            int te = rand.nextInt(255) + 1;
            Team randomTeam = Team.all[te];
            if (randomTeam == null) continue;

            if (playerTeams.find(p -> p.getTeam() == randomTeam) == null) {
                return randomTeam;
            }

        } while (true);
    }


    public static boolean isOwner(Player p, Team t) {
        return playerTeams.find(zov -> zov.getTeam() == t && zov.getOwner() == p) != null;
    }

    public static int getCap(Team team) {
        int cap = Vars.state.rules.unitCap;

        for (Building b : getCores()) {
            if (b.team != team) continue;

            if (b.block == Blocks.coreNucleus) {
                cap += 8;
            } else if (b.block == Blocks.coreFoundation) {
                cap += 4;
            }
        }

        return cap;
    }

    public static void clearData() {
        awaitingClick.clear();
        playerTeams.clear();
        leftDatas.clear();
        gameStarted = false;
    }

    public static void addItems(Building core) {
        try {
            if(core == null)
                return;
            if(Vars.state.rules.planet == sun) {
                core.items.add(Items.beryllium, 100);
                core.items.add(Items.copper, 250);
                core.items.add(Items.lead, 250);
            } else if(Vars.state.rules.planet == serpulo) {
                core.items.add(Items.copper, 250);
                core.items.add(Items.lead, 250);
            } else if(Vars.state.rules.planet == erekir) {
                core.items.add(Items.beryllium, 250);
            } else {
                Log.err("Bruh.");
            }
        } catch (Exception e) {
            Log.err("Не могу добавить ресурсы в ядро: ", e);
        }
    }

    @Getter
    @Setter
    public static class leftPlayerData {
        Player old;
        String uuid;
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
}
