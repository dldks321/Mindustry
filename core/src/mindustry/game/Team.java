package mindustry.game;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.Rules.*;
import mindustry.game.Teams.*;
import mindustry.graphics.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.modules.*;

import static mindustry.Vars.*;

public class Team implements Comparable<Team>{
    public final int id;
    public final Color color;
    public final Color[] palette;
    public final int[] palettei = new int[3];
    public String emoji = "";
    public boolean hasPalette;
    public String name;

    /** All 256 registered teams. */
    public static final Team[] all = new Team[256];
    /** The 6 base teams used in the editor. */
    public static final Team[] baseTeams = new Team[6];

    public final static Team
        //derelict = new Team(0, "derelict", Color.valueOf("4d4e58")),
        derelict = new TeamBuilder()
                .setId(0)
                .setName("derelict")
                .setColor(Color.valueOf("4d4e58"))
                .initPalette()
                .build(),
        sharded = new TeamBuilder()
                .setId(1)
                .setName("sharded")
                .setColor(Pal.accent.cpy())
                .initPalette()
                .setPalette(Color.valueOf("ffd37f"), Color.valueOf("eab678"), Color.valueOf("d4816b"))
                .build(),
        crux = new TeamBuilder()
                .setId(2)
                .setName("crux")
                .setColor(Color.valueOf("f25555"))
                .initPalette()
                .setPalette(Color.valueOf("fc8e6c"), Color.valueOf("f25555"), Color.valueOf("a04553"))
                .build(),
        green = new TeamBuilder()
                .setId(3)
                .setName("green")
                .setColor(Color.valueOf("54d67d"))
                .initPalette()
                .setPalette(Color.valueOf("96f58c"), Color.valueOf("54d67d"), Color.valueOf("28785c"))
                .build(),
        purple = new TeamBuilder()
                .setId(4)
                .setName("purple")
                .setColor(Color.valueOf("995bb0"))
                .initPalette()
                .setPalette(Color.valueOf("f08dd5"), Color.valueOf("995bb0"), Color.valueOf("312c63"))
                .build(),
        blue = new TeamBuilder()
                .setId(5)
                .setName("blue")
                .setColor(Color.valueOf("554deb"))
                .initPalette()
                .setPalette(Color.valueOf("80aaff"), Color.valueOf("554deb"), Color.valueOf("3f207d"))
                .build();

    static{
        Mathf.rand.setSeed(8);
        //create the whole 256 placeholder teams
        for(int i = 6; i < all.length; i++){
            new TeamBuilder()
                    .setId(i)
                    .setName("team#" + i)
                    .setColor(Color.HSVtoRGB(360f * Mathf.random(), 100f * Mathf.random(0.6f, 1f), 100f * Mathf.random(0.8f, 1f), 1f))
                    .initPalette()
                    .build();
        }
        Mathf.rand.setSeed(new Rand().nextLong());
    }

    public static Team get(int id){
        return all[((byte)id) & 0xff];
    }

    protected static class TeamBuilder{
        public int id;
        public Color color;
        public Color[] palette;
        public int[] palettei = new int[3];
        public boolean hasPalette;
        public String name;

        protected TeamBuilder(){}

        protected TeamBuilder setId(int id){
            this.id = id;
            return this;
        }

        protected TeamBuilder setName(String name){
            this.name = name;
            return this;
        }

        protected TeamBuilder setColor(Color color){
            this.color = color;
            return this;
        }

        protected TeamBuilder initPalette(){
            palette = new Color[3];
            palette[0] = color;
            palette[1] = color.cpy().mul(0.75f);
            palette[2] = color.cpy().mul(0.5f);

            for(int i = 0; i < 3; i++){
                palettei[i] = palette[i].rgba();
            }
            return this;
        }

        protected TeamBuilder setPalette(Color pal1, Color pal2, Color pal3){
            palette[0] = pal1;
            palette[1] = pal2;
            palette[2] = pal3;
            for(int i = 0; i < 3; i++){
                palettei[i] = palette[i].rgba();
            }
            hasPalette = true;

            return this;
        }

        protected Team build(){
            return new Team(this);
        }
    }

    protected Team(TeamBuilder teamBuilder){
        this.name = teamBuilder.name;
        this.color = teamBuilder.color;
        this.id = teamBuilder.id;

        if(teamBuilder.id < 6) baseTeams[teamBuilder.id] = this;
        all[teamBuilder.id] = this;

        palette = new Color[3];

        for(int i = 0; i < 3; i++){
            palette[i] = teamBuilder.palette[i];
            palettei[i] = teamBuilder.palettei[i];
        }

        hasPalette = teamBuilder.hasPalette;
    }

    /** @return the core items for this team, or an empty item module.
     * Never add to the resulting item module, as it is mutable. */
    public ItemModule items(){
        return core() == null ? ItemModule.empty : core().items;
    }

    /** @return the team-specific rules. */
    public TeamRule rules(){
        return state.rules.teams.get(this);
    }

    public TeamData data(){
        return state.teams.get(this);
    }

    @Nullable
    public CoreBuild core(){
        return data().core();
    }

    public boolean active(){
        return state.teams.isActive(this);
    }

    /** @return whether this team is solely comprised of AI, with no players. */
    public boolean isAI(){
        return state.rules.waves && this == state.rules.waveTeam;
    }

    public boolean isEnemy(Team other){
        return this != other;
    }

    public Seq<CoreBuild> cores(){
        return state.teams.cores(this);
    }

    public String localized(){
        return Core.bundle.get("team." + name + ".name", name);
    }

    @Override
    public int compareTo(Team team){
        return Integer.compare(id, team.id);
    }

    @Override
    public String toString(){
        return name;
    }
}
