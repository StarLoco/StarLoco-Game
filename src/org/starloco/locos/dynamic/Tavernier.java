package org.starloco.locos.dynamic;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.util.TimerWaiter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tavernier extends Updatable {

    private static final Tavernier instance = new Tavernier();

    public static Tavernier getInstance() {
        return instance;
    }

    private final GameMap map;

    private byte drinkAllRound = 0;

    public Tavernier(){
        super(5 * 60_000);
        this.map = World.world.getMap((short) 10354);
    }

    @Override
    public void update() {
        if(this.verify()) {
            this.drinkAllRound++;
            int count = 0;
            for (String str : this.parseHtml()) {
                TimerWaiter.addNext(() -> {
                    this.talk(str);
                    if(Formulas.getRandomValue(0, 10) == 5)
                        this.map.send("cS-3|" + Formulas.getRandomValue(1, 15));
                }, count);
                count += 7000;
            }
            if (this.drinkAllRound == 10){
                TimerWaiter.addNext(() -> {
                    this.drinkAllRound();
                    this.drinkAllRound = 0;
                }, 2000);
            }
        }
    }

    private void drinkAllRound() {
        this.map.send("cMK|-4|Habitué de la taverne|Tournée générale pour tout le monde, c'est moi qui régale !|");
        ObjectTemplate objectTemplate = World.world.getObjTemplate(6857);
        for(Player p : this.map.getPlayers()){
            if (p.isOnline()){
                GameObject object = objectTemplate.createNewItem(1, false);
                if (Formulas.getRandomValue(0, 3) == 0){
                    if(p.addObjet(object, true))
                        World.world.addGameObject(object);
                    p.send("Im021;1~" + objectTemplate.getId());
                } else {
                    p.send("eUK"+ p.getId() +"|18");
                }
            }
        }
    }

    @Override
    public Object get() {
        return null;
    }

    private void talk(String message) {
        this.map.send("cMK|-3|Tek Abir|" + message + "|");
    }

    private List<String> parseHtml(){
        List<String> msg = getHTML(), temp = new ArrayList<>();
        for(String str : msg){
            str = str.replaceAll("document.write[(][']", "");
            str = str.replace("\\", "");
            str = str.replace("/", "");
            str = str.replace("');", "");
            str = str.replace("<br>", "");
            str = str.replace("<b>", "");
            str = str.replace("<u>", "");
            str = str.replace("<", "");
            str = str.replace(">", "");
            if (!str.matches("(.*)margin(.*)") && !str.matches("(.*)<p>(.*)") && !str.matches("(.*)--(.*)") && !str.equals("p")){
                if (str.length() > 300){
                    temp.addAll(Arrays.asList(str.split(".")));
                } else temp.add(str);
            }
        }
        return temp;
    }

    private List<String> getHTML() {
        final List<String> msg = new ArrayList<>();
        try {
            URL url = new URL("http://185.212.226.3/blagues.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.connect();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                while ((line = rd.readLine()) != null) {
                    msg.add(line);
                }
                rd.close();
                return msg;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
