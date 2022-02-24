package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.*;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@RestController
public class Application {

    static class Self {
        public String href;
    }

    static class Links {
        public Self self;
    }

    static class PlayerState {
        public Integer x;
        public Integer y;
        public String direction;
        public Boolean wasHit;
        public Integer score;
    }

    static class Arena {
        public List<Integer> dims;
        public Map<String, PlayerState> state;
    }

    static class ArenaUpdate {
        public Links _links;
        public Arena arena;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }

    @GetMapping("/")
    public String index() {
        return "Let the battle begin!";
    }

    @PostMapping("/**")
    public String index(@RequestBody ArenaUpdate arenaUpdate) {
        System.out.println(arenaUpdate);
        Integer arenaX = arenaUpdate.arena.dims.get(0);
        Integer arenaY = arenaUpdate.arena.dims.get(1);

        String[][] arena = new String[arenaX][arenaY];
        String selfKey = "";

        PlayerState me = new PlayerState();

        for (Map.Entry<String, PlayerState> entry : arenaUpdate.arena.state.entrySet()) {
            if (entry.getKey().equals(arenaUpdate._links.self.href)) {
                me = entry.getValue();
                selfKey = entry.getKey();
            }

            PlayerState ps = entry.getValue();
            arena[ps.x][ps.y] = entry.getKey();
        }

        Integer fromX = 0;
        Integer fromY = 0;
        Integer toX = 0;
        Integer toY = 0;
        Integer forwardX = 0;
        Integer forwardY = 0;

        switch (me.direction) {
            case "N":
                fromY = me.y - 3;
                toY = me.y;
                fromX = me.x;
                toX = me.x;
                if (fromY < 0) {
                    fromY = 0;
                }
                forwardX = me.x;
                forwardY = me.y - 1;
                break;
            case "S":
                fromY = me.y;
                toY = me.y + 3;
                fromX = me.x;
                toX = me.x;
                if (toY > arenaY) {
                    toY = arenaY;
                }
                forwardX = me.x;
                forwardY = me.y + 1;
                break;
            case "E":
                fromY = me.y;
                toY = me.y;
                fromX = me.x;
                toX = me.x + 3;
                if (toX > arenaX) {
                    toX = arenaX;
                }
                forwardX = me.x + 1;
                forwardY = me.y;
                break;
            case "W":
                fromY = me.y;
                toY = me.y;
                fromX = me.x - 3;
                toX = me.x;
                if (fromX < 0) {
                    fromX = 0;
                }
                forwardX = me.x - 1;
                forwardY = me.y;
                break;
        }
        boolean someoneExists = false;
        for (int i = fromX; i <= toX; i++) {
            for (int j = fromY; j <= toY; j++) {
                try {
                    if (arena[i][j] != null && !arena[i][j].equals(arenaUpdate._links.self.href)) {
                        someoneExists = true;
                    }
                } catch (Exception ex) {
                    someoneExists = false;
                }
            }
        }

        System.out.println(getHighestState(arenaUpdate.arena.state, selfKey));
        if (me.wasHit) {
            if (forwardX < 0 || forwardY < 0 || forwardY >= arenaY || forwardX >= arenaX || [forwardX][forwardY] != null){
                return "R";
            } else {
                return "F";
            }
        } else if (someoneExists) {
            return "T";
        } else {
            if (forwardX < 0 || forwardY < 0 || forwardY >= arenaY || forwardX >= arenaX || [forwardX][forwardY] != null){
                return "R";
            } else {
                String[] commands = new String[]{"F", "F", "R"};
                int i = new Random().nextInt(3);
                return commands[i];
            }
        }
    }

    public List<Integer> getHighestState(Map<String, PlayerState> state, String selfKey){
        List<Integer> position = new ArrayList<>();
        Integer highest = 0;
        for (Map.Entry<String, PlayerState> entry : state.entrySet()) {
            PlayerState ps = entry.getValue();
            if (!entry.getKey().equals(selfKey)) {
                if (ps.score >= highest) {
                    highest = ps.score;
                    position = new ArrayList<>();
                    position.add(ps.x);
                    position.add(ps.y);
                }
            }
        }
        return position;
    }
}

